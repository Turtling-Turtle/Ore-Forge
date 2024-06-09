package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import ore.forge.ItemMap;
import ore.forge.OreForge;
import ore.forge.Player.Inventory;
import ore.forge.Player.Player;

import java.util.Stack;

public class InputHandler {
    public enum Mode {OBSERVING, SELECTING, BUILDING, INVENTORY}

    private InputMode currentMode;
    public Mode mode;
    private final ObserverModeProcessor observerModeProcessor;
    private final SelectModeProcessor selectModeProcessor;
    private final BuildModeProcessor buildModeProcessor;
    private final InventoryModeProcessor inventoryModeProcessor;
    private final Stack<InputHandler.Mode> modeStack;
    private final static Inventory inventory = Player.getSingleton().getInventory();
    private final static ItemMap itemMap = ItemMap.getSingleton();
    private final OreForge game;
    public final Vector3 mouseWorld, mouseScreen;

    public InputHandler(OreForge game) {
        mouseWorld = new Vector3();
        mouseScreen = new Vector3();
        this.game = game;
        observerModeProcessor = new ObserverModeProcessor(this);
        selectModeProcessor = new SelectModeProcessor(this);
        buildModeProcessor = new BuildModeProcessor(this);
        inventoryModeProcessor = new InventoryModeProcessor(this);
        modeStack = new Stack<>();
        setMode(Mode.OBSERVING);

    }

    public void update(float delta, OrthographicCamera camera) {
        currentMode.update(delta, camera);
    }

    public void exitMode() {
        modeStack.pop();
        currentMode = getMode(modeStack.peek());
        if (currentMode instanceof InventoryModeProcessor) {
            try {
                ((InventoryModeProcessor) currentMode).activate();

            } catch (NullPointerException e) {

            }

        }
        Gdx.app.log("INPUT MODE", currentMode.toString());
    }


    public void setMode(Mode mode) {
        System.out.println(mode);
//        var newMode = getMode(mode);
        modeStack.push(mode);
        if (getMode(mode) instanceof BuildModeProcessor) {
            if (currentMode instanceof SelectModeProcessor) {
                ((BuildModeProcessor) getMode(mode)).setHeldNode(inventory.getNode(((SelectModeProcessor) currentMode).getSelectedItem().getID()));
            } else if (currentMode instanceof InventoryModeProcessor) {
                ((BuildModeProcessor) getMode(mode)).setHeldNode(((InventoryModeProcessor) currentMode).getSelectedNode());
                Gdx.app.log("INPUT MODE", currentMode.toString());
            }
        }

        if (getMode(mode) instanceof SelectModeProcessor) {
            ((SelectModeProcessor) getMode(mode)).setSelectedItem(itemMap.getItem(mouseWorld));
        }

        currentMode = getMode(mode);
        if (currentMode instanceof InventoryModeProcessor) {
            try {
                ((InventoryModeProcessor) currentMode).activate();

            } catch (NullPointerException e) {

            }

        }
        Gdx.app.log("INPUT MODE", currentMode.toString());
    }

    private InputMode getMode(Mode newMode) {
        this.mode = newMode;
        return switch (newMode) {
            case SELECTING -> selectModeProcessor;
            case BUILDING -> buildModeProcessor;
            case INVENTORY -> inventoryModeProcessor;
            case OBSERVING -> observerModeProcessor;
        };
    }

    public void pauseGame() {
        game.setScreen(game.pauseMenu);
    }

    public InputMode getCurrentMode() {
        return currentMode;
    }

    public Mode getMode() {
        return mode;
    }


}
