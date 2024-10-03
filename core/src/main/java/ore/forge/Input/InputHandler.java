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
import ore.forge.Screens.InventoryTable;
import ore.forge.Screens.QuestMenu;
import ore.forge.Screens.ShopMenu;
import ore.forge.Screens.UserInterface;

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
    private final QuestMode questMode;


    private UserInterface userInterface;
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
        questMode = new QuestMode();
        currentMode = observerMode;
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

    public QuestMenu getQuestMenu() {
        return userInterface.getQuestMenu();
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

    public void setHeldItem(Item item) {
        this.heldItem = item;
        setInventoryNode(item);
    }

    public ShopMenu getShopMenu() {
        return userInterface.getShopUI();
    }

    public InventoryTable getInventoryUI() {
        return userInterface.getInventoryTable();
    }

    public UserInterface getUserInterface() {
        return userInterface;
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

    public void setUserInterface(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public QuestMode getQuestMode() {
        return questMode;
    }

    public float getMouseWorldX() {
        return mouseWorld.x;
    }

    public float getMouseWorldY() {
        return mouseWorld.y;
    }

    public boolean isCoordsValid() {
        return !(mouseWorld.x > itemMap.mapTiles.length - 1) && !(mouseWorld.x < 0) && !(mouseWorld.y > itemMap.mapTiles[0].length - 1) && !(mouseWorld.y < 0);
    }
}
