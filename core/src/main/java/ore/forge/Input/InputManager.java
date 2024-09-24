package ore.forge.Input;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import ore.forge.Screens.UserInterface;

import static com.badlogic.gdx.Input.Keys.*;

public class InputManager {
    public final InputMultiplexer multiplexer;
    private final OrthographicCamera camera;
    private final Game game;
    private final UserInterface ui;


    private boolean moveUp, moveDown, moveLeft, moveRight;
    private boolean zoomIn, zoomOut;

    public InputManager(OrthographicCamera camera, Game game, UserInterface ui) {
        this.camera = camera;
        this.game = game;
        multiplexer = new InputMultiplexer();
        this.ui = ui;
    }

    public void updateCamera() {

    }

    private class DefaultController extends InputAdapter {

        private boolean handleKey(int keycode, boolean isPressed) {
            return switch (keycode) {
                case Q -> {
                    zoomIn = isPressed;
                    yield true;
                }
                case E -> {
                    zoomOut = isPressed;
                    yield true;
                }
                case W -> {
                    moveUp = isPressed;
                    yield true;
                }
                case S -> {
                    moveDown = isPressed;
                    yield true;
                }
                case A -> {
                    moveLeft = isPressed;
                    yield true;
                }
                case D -> {
                    moveRight = isPressed;
                    yield true;
                }
                default -> false;
            };
        }

        @Override
        public boolean keyDown(int keycode) {
            if (ui.getInventoryTable().isSearching()) {
                return false;
            }
            return handleKey(keycode, true);
        }

        @Override
        public boolean keyUp(int keycode) {
            if (ui.getInventoryTable().isSearching()) {
                return false;
            }
            return handleKey(keycode, false);
        }

    }

}
