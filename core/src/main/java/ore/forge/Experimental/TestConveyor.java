package ore.forge.Experimental;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import java.util.function.Consumer;

public class TestConveyor extends PhysicsObject {
    static final float SPEED = 1000f;
    private static final float[] VERTICES = {
        0, 0, //bottom left
        2, 0, //bottom right
        2, 2,  //top right
        0, 2, //top left
    };


    public TestConveyor() {
        super(new Polygon(VERTICES));
        this.setOnCollision(new Consumer<PhysicsObject>() {
            final TestConveyor thisO = TestConveyor.this;

            @Override
            public void accept(PhysicsObject physicsObject) {
                float dx = MathUtils.cosDeg(thisO.getAngle());
                float dy = MathUtils.sinDeg(thisO.getAngle());
                Vector2 conveyorDirection = new Vector2(dx, dy);
                physicsObject.applyForce(conveyorDirection.scl(SPEED * Gdx.graphics.getDeltaTime()));
            }
        });
        this.setIsStatic(true);
//        this.setCollisionEnabled(true);
        this.setDirection(90);
    }

}
