package ore.forge;

import com.badlogic.gdx.math.Vector2;

public interface PhysicsObject {
    Vector2 position = new Vector2();
    Vector2 acceleration = new Vector2();
    Vector2 velocity = new Vector2();

    Vector2 getPosition();

    Vector2 getAcceleration();

    Vector2 getVelocity();

    Vector2 getMass();

}
