package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

public abstract class InputMode {
    protected final static float ZOOM_SPEED = 0.05f;
    protected final static float CAMERA_SPEED = 20.0f;


    public abstract void update(float deltaTime, OrthographicCamera camera, InputHandler context);

    public abstract void setActive(InputHandler context);

    public void updateCameraZoom(float delta, OrthographicCamera camera) {
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom -= ZOOM_SPEED * delta;
            if (camera.zoom < 0.01f) {
                camera.zoom = 0.01f;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom += ZOOM_SPEED * delta;
        }
    }

    public void updateCameraPosition(float delta, OrthographicCamera camera) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.position.y += CAMERA_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.position.y -= CAMERA_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.position.x += CAMERA_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.position.x -= CAMERA_SPEED * delta;
        }
    }

}
