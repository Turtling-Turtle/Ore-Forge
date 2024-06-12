package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import ore.forge.ItemMap;
import ore.forge.Items.Item;
import ore.forge.OreForge;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;
import ore.forge.Player.Player;

public class InputHandler {

    private InputMode currentMode;
    private InputMode previousMode;

    private InventoryNode heldNode;
    private Item heldItem;



    private final ObserverMode observerMode;
    private final SelectMode selectMode;
    private final BuildMode buildMode;
    private final InventoryMode inventoryMode;
    private final OreObserver oreObserver;


    private final static Inventory inventory = Player.getSingleton().getInventory();
    private final static ItemMap itemMap = ItemMap.getSingleton();
    private final OreForge game;
    public final Vector3 mouseWorld, mouseScreen;

    public InputHandler(OreForge game) {
        mouseWorld = new Vector3();
        mouseScreen = new Vector3();
        this.game = game;
        observerMode = new ObserverMode();
        selectMode = new SelectMode();
        buildMode = new BuildMode(heldNode, heldItem);
        inventoryMode = new InventoryMode(this);
        oreObserver = new OreObserver();
    }

    public void update(float delta, OrthographicCamera camera) {
        if (Gdx.graphics.getWidth() == 0 || Gdx.graphics.getHeight() == 0) {
            return;
        }
        updateMouse(camera);
        currentMode.update(delta, camera, this);
    }

    public void pauseGame() {
        game.setScreen(game.pauseMenu);
    }

    public InputMode getCurrentMode() {
        return currentMode;
    }

    public void updateMouse(OrthographicCamera camera) {
        mouseScreen.x = Gdx.input.getX();
        mouseScreen.y = Gdx.input.getY();
        mouseWorld.set(camera.unproject(mouseScreen));
    }

    public void setCurrentMode(InputMode newMode) {
        previousMode = this.currentMode;
        this.currentMode = newMode;
        currentMode.setActive(this);
//        Gdx.app.log("InputHandler", "New Mode:" + currentMode);
    }

    public InputMode getPreviousMode() {
        return previousMode;
    }

    public InventoryNode getHeldNode() {
        return heldNode;
    }

    public void setHeldNode(InventoryNode heldNode) {
        this.heldNode = heldNode;
    }

    public ObserverMode getObserverMode() {
        return observerMode;
    }

    public SelectMode getSelectMode() {
        return selectMode;
    }

    public InventoryMode getInventoryMode() {
        return inventoryMode;
    }

    public OreObserver getOreObserver() {
        return oreObserver;
    }

    public void setInventoryNode(InventoryNode node) {
        this.heldNode = node;
    }

    public void setInventoryNode(Item item) {
        this.heldNode = inventory.getNode(item.getID());
    }

    public InventoryNode getInventoryNode() {
        return heldNode;
    }

    public void setHeldItem(Item item) {
        this.heldItem = item;
        setInventoryNode(item);
    }

    public Item getHeldItem() {
        return heldItem;
    }

    public BuildMode getBuildMode() {
        return buildMode;
    }

    public Vector3 getMouseWorld() {
        return mouseWorld;
    }

    public float getMouseWorldX() {
        return mouseWorld.x;
    }

    public float getMouseWorldY() {
        return mouseWorld.y;
    }

    public boolean isCoordinatesInvalid() {
        return mouseWorld.x > itemMap.mapTiles.length - 1 || mouseWorld.x < 0 || mouseWorld.y > itemMap.mapTiles[0].length - 1 || mouseWorld.y < 0;
    }
}
