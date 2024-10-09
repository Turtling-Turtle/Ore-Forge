package ore.forge.Input;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import ore.forge.ItemMap;
import ore.forge.Screens.InventoryTable;
import ore.forge.Screens.ShopMenu;
import ore.forge.Screens.UserInterface;

import static com.badlogic.gdx.Input.Keys.*;

public class InputManager {
    public final InputMultiplexer multiplexer;
    private final OrthographicCamera camera;
    private final Game game;
    private final UserInterface ui;
    private Vector3 mouseWorld;
    private final static ItemMap ITEM_MAP = ItemMap.getSingleton();
    private final DefaultState defaultState;
    private final CameraController controller;

    /*
     * Default Mode
     * Selecting
     * Building
     * Observing Ore
     *
     * */


    public InputManager(OrthographicCamera camera, Game game, UserInterface ui) {
        controller = new CameraController();
        this.ui = ui;
        this.camera = camera;
        this.game = game;
        multiplexer = new InputMultiplexer();
        defaultState = new DefaultState(this);

        ui.getInventoryTable().setInputManager(this);
        multiplexer.addProcessor(ui.stage);
        multiplexer.addProcessor(defaultState);

        Gdx.input.setInputProcessor(multiplexer);
    }

    public void update() {
        defaultState.update(camera);
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

    public void setDefaultState(GameplayController mode) {

    }

    public void updateProcessor() {
        Gdx.input.setInputProcessor(multiplexer);
    }

    public CameraController getController() {
        return controller;
    }

    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }

    public InventoryTable getInventory() {
        return ui.getInventoryTable();
    }

    public ShopMenu getShop() {
        return ui.getShopUI();
    }

    public Game getGame() {
        return game;
    }
}
