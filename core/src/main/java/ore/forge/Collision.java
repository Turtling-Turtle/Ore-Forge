package ore.forge;

import com.badlogic.gdx.math.Intersector;
import ore.forge.Experimental.PhysicsObject;

public record Collision(PhysicsObject o1, PhysicsObject o2, Intersector.MinimumTranslationVector mtv) {

    public void resolve() {
        PhysicsObject.solveCollision(o1, o2, mtv);
    }

}
