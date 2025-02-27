package ore.forge.Experimental;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ore.forge.Collision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.Consumer;

public class PhysicsObject {
    private static final PhysicsWorld world = PhysicsWorld.getInstance();
    private float angularVelocity;
    private float mass;
    private float angle;
    private Vector2 position, velocity, acceleration;
    private Polygon body;
    private boolean collisionEnabled;
    private boolean isStatic;
    private Consumer<PhysicsObject> onCollision;

    public PhysicsObject(Polygon body) {
        this.body = body;
        position = new Vector2();
        velocity = new Vector2();
        acceleration = new Vector2();
        angularVelocity = 0;
        mass = 1;
        angle = 0;
        onCollision = null;
        collisionEnabled = false;
    }

    public PhysicsObject(Polygon body, Consumer<PhysicsObject> onCollision) {
        this.body = body;
        position = new Vector2();
        velocity = new Vector2();
        acceleration = new Vector2();
        angularVelocity = 0;
        mass = 1;
        angle = 0;
        this.onCollision = onCollision;
    }

    public void update(float delta, Collection<PhysicsObject> objects) {
        if (this.isStatic) {
            return;
        }
        for (PhysicsObject otherObject : objects) {
            var mtv = intersects(this, otherObject);
            if (otherObject != this && mtv != null && mtv.depth > 0) {
                if (otherObject.collisionEnabled) {
                    solveCollision(this, otherObject, mtv);
                }
                this.onCollision(otherObject);
                otherObject.onCollision(this);
            }
        }

        //Project Body across path of movement.
//        PriorityQueue<PhysicsObject> priorityQueue = createPriorityQueue();
//        world.getObjects(priorityQueue, null);
//        while (!priorityQueue.isEmpty()) {
//            PhysicsObject otherObject = priorityQueue.poll();
//            boolean intersects = intersects(this, otherObject);
//            if (intersects && otherObject.collisionEnabled) {
//                //move up to this object
//                this.onCollision(otherObject);
//                otherObject.onCollision(this);
//                break; //we've moved through all objects cant go further so stop
//            } else if (intersects) {
//                //move through object and trigger collision
//                this.onCollision(otherObject);
//                otherObject.onCollision(this);
//            }
//        }
        Vector2 friction = velocity.cpy().nor().scl(-0.95f);
        friction.scl(1 / mass);
        acceleration.add(friction);

        Vector2 previousPosition = position.cpy();
        velocity.add(new Vector2(acceleration).scl(delta));
        position.add(new Vector2(velocity).scl(delta));
        angle += delta * angularVelocity;

        var oldBox = body.getBoundingRectangle();
        body.setPosition(position.x, position.y);
        body.setRotation(angle);

        if (!isStatic && world.cellChange(this, previousPosition)) {
            world.updateObject(this, oldBox);
        }

        acceleration.setZero();
    }

    private PriorityQueue<PhysicsObject> createPriorityQueue() {
        //NOTE: this is based on center position and not polygon edges.
        var thisObject = this;
        return new PriorityQueue<>(new Comparator<>() {
            final PhysicsObject anchor = thisObject;

            @Override
            public int compare(PhysicsObject o1, PhysicsObject o2) {
                //return which object is closest to this object.
                float dist1 = o1.getPosition().dst(anchor.position);
                float dist2 = o2.getPosition().dst(anchor.position);
                return Float.compare(dist1, dist2);
            }
        });
    }

    public Collection<Collision> checkCollision() {
        var collisions = new ArrayList<Collision>();
        ArrayList<PhysicsObject> objects = new ArrayList<>();
        world.getObjects(objects, this.body.getBoundingRectangle());
        for (PhysicsObject object : objects) {
            if (object == this) {
                continue;
            }
            var mtv = intersects(this, object);
            if (mtv != null) {
                collisions.add(new Collision(this, object, mtv));
            }

//            if (otherObject != this && mtv != null && mtv.depth > 0) {
//                if (otherObject.collisionEnabled) {
//                    solveCollision(this, otherObject, mtv);
//                }
//                this.onCollision(otherObject, delta);
//                otherObject.onCollision(this, delta);
//            }

        }
        return collisions;
    }


    public static Intersector.MinimumTranslationVector intersects(PhysicsObject object1, PhysicsObject object2) {
        Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
        if (Intersector.overlaps(object1.getBody().getBoundingRectangle(), object2.getBody().getBoundingRectangle())) {
            Intersector.overlapConvexPolygons(object1.body, object2.body, mtv);
            return mtv;
        }
        return null;
    }


    public static void solveCollision(PhysicsObject object1, PhysicsObject object2, Intersector.MinimumTranslationVector mtv) {
        assert mtv.depth > 0;
        if (object2.collisionEnabled) {
            float e = 0.5f;

            Vector2 relativeVelocity = object1.velocity.cpy().sub(object2.velocity);
            Vector2 impulseDirection = mtv.normal;

            float impulseMagnitude = -(1 + e) * Vector2.dot(relativeVelocity.x, relativeVelocity.y, mtv.normal.x, mtv.normal.y) / object1.getInverseMass() + object2.getInverseMass();

            Vector2 jn = impulseDirection.scl(impulseMagnitude);

            jn.scl((float) Math.pow(1 + mtv.depth, 10*mtv.depth));
            object1.applyForce(jn);
            object2.applyForce(jn.scl(-1));
        }
        object1.onCollision(object2);
        object2.onCollision(object1);
    }

    public void updatePosition(float delta) {
        Vector2 friction = velocity.cpy().nor().scl(-0.95f);
        friction.scl(1 / mass);
        acceleration.add(friction);

        Vector2 previousPosition = position.cpy();
        velocity.add(new Vector2(acceleration).scl(delta));
        position.add(new Vector2(velocity).scl(delta));
        angle += delta * angularVelocity;

        var oldBox = body.getBoundingRectangle();
        body.setPosition(position.x, position.y);
        body.setRotation(angle);

        if (!isStatic && world.cellChange(this, previousPosition)) {
            world.updateObject(this, oldBox);
        }

        acceleration.setZero();
    }

    public float getStaticFriction() {
        return 0.5f; //Example
    }

    public float getDynamicFriction() {
        return 0.3f; //Example
    }

//    public static void solveCollision(PhysicsObject object1, PhysicsObject object2) {
//        return;
//    }

    public Polygon getBody() {
        return body;
    }

    public void onCollision(PhysicsObject otherObject) {
        if (onCollision != null) {
            this.onCollision.accept(otherObject);
        }
    }

    public void applyForce(Vector2 force) {
        acceleration.add(force);
    }

    public float getInverseMass() {
        assert mass != 0;
        return 1 / mass;
    }

    public float getRestitution() {
        return 0.5f;
    }

    public void applyAngularVelocity(float angularVelocity) {
        this.angularVelocity += angularVelocity;
    }

    public float getXPos() {
        return position.x;
    }

    public float getYPos() {
        return position.y;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public void setOnCollision(Consumer<PhysicsObject> onCollision) {
        this.onCollision = onCollision;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        this.body.setPosition(position.x, position.y);
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
        this.body.setPosition(x, y);
    }

    public void setPosition(Vector3 position) {
        this.position.x = position.x;
        this.position.y = position.y;
        this.body.setPosition(position.x, position.y);
    }

    public void setDirection(float angle) {
        this.body.setRotation(angle);
        this.angle = angle;
    }

    public float getMass() {
        return mass;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setIsStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public void setCollisionEnabled(boolean collisionEnabled) {
        this.collisionEnabled = collisionEnabled;
    }


}


