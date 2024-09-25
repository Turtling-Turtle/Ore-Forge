package ore.forge.Input;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraController {
    private final static float CAMERA_SPEED = 20.0f;
    private final static float ZOOM_SPEED = .05f;
    private boolean moveUp, moveDown, moveLeft, moveRight;
    private boolean zoomIn, zoomOut;

    public CameraController() {
    }

    public void updateCamera(OrthographicCamera camera) {

    }

    public void setAll(boolean state) {
        moveUp = state;
        moveDown = state;
        moveLeft = state;
        moveRight = state;
        zoomIn = state;
        zoomOut = state;
    }

    public boolean isMoveUp() {
        return moveUp;
    }

    public void setMoveUp(boolean moveUp) {
        this.moveUp = moveUp;
    }

    public boolean isMoveDown() {
        return moveDown;
    }

    public void setMoveDown(boolean moveDown) {
        this.moveDown = moveDown;
    }

    public boolean isMoveLeft() {
        return moveLeft;
    }

    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public boolean isMoveRight() {
        return moveRight;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public boolean isZoomIn() {
        return zoomIn;
    }

    public void setZoomIn(boolean zoomIn) {
        this.zoomIn = zoomIn;
    }

    public boolean isZoomOut() {
        return zoomOut;
    }

    public void setZoomOut(boolean zoomOut) {
        this.zoomOut = zoomOut;
    }

}
