package ore.forge.Input;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import ore.forge.ItemMap;
import ore.forge.Screens.UserInterface;

import static com.badlogic.gdx.Input.Keys.*;

public class InputManager {
    public final InputMultiplexer multiplexer;
    private final OrthographicCamera camera;
    private final Game game;
    private final UserInterface ui;
    private Vector3 mouseWorld;
    private final static ItemMap ITEM_MAP = ItemMap.getSingleton();

    /*
     * Default Mode
     * Selecting
     * Building
     * Observing Ore
     *
     * */


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

    public static boolean handleKey(int keycode, boolean isPressed, CameraController controller) {
        return switch (keycode) {
            case Q -> {
                controller.setZoomIn(isPressed);
                yield true;
            }
            case E -> {
                controller.setZoomOut(isPressed);
                yield true;
            }
            case W -> {
                controller.setMoveUp(isPressed);
                yield true;
            }
            case S -> {
                controller.setMoveDown(isPressed);
                yield true;
            }
            case A -> {
                controller.setMoveLeft(isPressed);
                yield true;
            }
            case D -> {
                controller.setMoveRight(isPressed);
                yield true;
            }
            default -> false;
        };
    }

    public Vector3 mouseWorld() {
        return mouseWorld;
    }

    public boolean isCoordsValid() {
        return !(mouseWorld.x > ITEM_MAP.mapTiles.length - 1) && !(mouseWorld.x < 0) && !(mouseWorld.y > ITEM_MAP.mapTiles[0].length - 1) && !(mouseWorld.y < 0);
    }

    private class DefaultController extends InputAdapter {


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
