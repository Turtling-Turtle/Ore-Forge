package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import ore.forge.ItemMap;

public class ObserverModeProcessor implements InputMode {
    private final Vector3 mouseWorld, mouseScreen;
    protected static final ItemMap itemMap = ItemMap.getSingleton();
    private final InputHandler handler;
    private static final float ZOOM_SPEED = 0.05f;
    private static final float CAMERA_SPEED = 20.0f;

    public ObserverModeProcessor(InputHandler handler) {
        this.handler = handler;
        mouseWorld = handler.mouseWorld;
        mouseScreen = handler.mouseScreen;
    }

    @Override
    public void update(float delta, OrthographicCamera camera) {
        updateMouse(camera);
        handleMovement(delta, camera);
        handleZoom(delta, camera);
        checkModes();
    }

    public void handleZoom(float delta, OrthographicCamera camera) {
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

    public void handleMovement(float delta, OrthographicCamera camera) {
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

    public void checkModes() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            handler.pauseGame();
            return;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F1) || Gdx.input.isKeyJustPressed(Input.Keys.I)) {
           handler.setMode(InputHandler.Mode.INVENTORY);
           return;
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !isInvalid() && itemMap.getBlock(mouseWorld.x, mouseWorld.y) != null) {
            handler.setMode(InputHandler.Mode.SELECTING);
        }
    }

    private void updateMouse(OrthographicCamera camera) {
        mouseScreen.x = Gdx.input.getX();
        mouseScreen.y = Gdx.input.getY();
        mouseWorld.set(camera.unproject(mouseScreen));
    }


    private boolean isInvalid() {
        return mouseWorld.x > itemMap.mapTiles.length - 1 || mouseWorld.x < 0 || mouseWorld.y > itemMap.mapTiles[0].length - 1 || mouseWorld.y < 0;
    }

    public String toString() {
        return "ObserverModeProcessor";
    }


}
