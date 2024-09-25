package ore.forge.Input;

import com.badlogic.gdx.graphics.OrthographicCamera;

public interface GameplayController {
    final static CameraController cameraController = new CameraController();

    void update(OrthographicCamera camera);

}
