package ore.forge.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import ore.forge.ButtonHelper;
import ore.forge.Enums.BooleanOperator;
import ore.forge.Enums.Operator;
import ore.forge.Items.*;
import ore.forge.ItemMap;
import ore.forge.OreForge;
import ore.forge.Strategies.OreEffects.*;
import ore.forge.Strategies.UpgradeStrategies.*;
import ore.forge.UpgradeTag;

import java.util.ArrayList;
import java.util.Stack;

//@author Nathan Ulmen
public class InputHandler {
    private enum Mode {DEFAULT, BUILDING, SELECTING}
    protected static final ItemMap itemMap = ItemMap.getSingleton();
    protected static final Player player = Player.getSingleton();
    private final float cameraSpeed;
    public Vector3 mouseScreen, mouseWorld;
    private Mode currentMode;
    private Item heldItem;
    public Item selectedItem;
    private InventoryNode heldNode;//Will replace heldItem once I build inventory.
    public Rectangle selectionRectangle;
    public Stack<Item> recentlyPlaced;
    private final ArrayList<Item> contiguousPlacedItems;
    private final ArrayList<Item> selectedItems;

    public int[][] upgraderConfig = {//Test values
//            { 0, 1, 1, 0},
//            { 0, 2, 2, 0},
//            { 0, 1, 1, 0},
            {2,2},
            {1,1},
    };
    public int [][] conveyorConfig = {
            {1, 1},
            {1, 1},
    };
    public int [][] furnaceConfig = {
            {4, 4},
            {4, 4},
    };
    public int[][] dropperConfig = {
            {0,3,0},
            {0,0,0},
            {0,0,0},
    };
    public int[][] buildingConfig = {
            {0,1,0},
            {0,1,0},
            {0,1,1}
    };



    //Test objects:

    UpgradeStrategy testUpgrade = new BasicUpgrade(3.0, Operator.MULTIPLY, BasicUpgrade.ValueToModify.ORE_VALUE);

    UpgradeStrategy destroy = new DestructionUpgrade();
    UpgradeStrategy conditional = new ConditionalUpgrade(testUpgrade, destroy, ConditionalUpgrade.Condition.VALUE, 100000*100000, BooleanOperator.GREATER_THAN);

    UpgradeTag upgradeTag = new UpgradeTag("Basic Upgrade Tag", 4, false);

    OreEffect invincibility = new Invulnerability(12, 10f);

    UpgradeStrategy simpleMultiply = new BasicUpgrade(1.02, Operator.MULTIPLY, BasicUpgrade.ValueToModify.ORE_VALUE);
    OreEffect upgradeOverTime = new UpgradeOreEffect(1, 10E10f, simpleMultiply);

    UpgradeStrategy basicUpgrade = new BasicUpgrade(.1, Operator.MULTIPLY, BasicUpgrade.ValueToModify.ORE_VALUE);
    UpgradeStrategy influencedUpgrade = new InfluencedUpgrade(InfluencedUpgrade.ValuesOfInfluence.VALUE, (BasicUpgrade) basicUpgrade, Operator.MULTIPLY);
    OreEffect influencedUpgradeOverTime = new UpgradeOreEffect(1, 2E10f, influencedUpgrade);

    OreEffect dropperStrat = new BundledOreEffect(invincibility, upgradeOverTime, influencedUpgradeOverTime, null);


    public InputHandler() {
        mouseScreen = new Vector3();
        mouseWorld = new Vector3();
        cameraSpeed = 20f;
        selectionRectangle = new Rectangle();
        contiguousPlacedItems = new ArrayList<>();
        recentlyPlaced = new Stack<>();
        selectedItems = new ArrayList<>();
        currentMode = Mode.DEFAULT;
    }

    public void updateMouse(OrthographicCamera camera) {
        mouseScreen.x = Gdx.input.getX();
        mouseScreen.y = Gdx.input.getY();
        mouseScreen.z = 0;

        mouseWorld.set(camera.unproject(mouseScreen));
//        mouseWorld.x = MathUtils.round(mouseWorld.x * 1.5f) / 1.5f;
//        mouseWorld.y = MathUtils.round(mouseWorld.y * 1.5f) / 1.5f;
//        mouseWorld.x = MathUtils.floor(mouseWorld.x);
//        mouseWorld.y = MathUtils.floor(mouseWorld.y);
    }

    public void handleInput(float deltaT, OrthographicCamera camera, OreForge game) {
        handleMovement(deltaT, camera);
        handleZoom(deltaT, camera);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {//Check to see if escape is pressed.
            if (currentMode != Mode.BUILDING) {
                game.setScreen(game.pauseMenu);
                itemMap.saveState();
            } else {
                currentMode = Mode.DEFAULT;
            }
        }

        itemSelect();
        handlePlacement();
        handleSelecting();
    }

    private void itemSelect() {
        //Version with inventory would look like this:
        //heldItem = Inventory.getNode.getItem.clone()
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            currentMode = Mode.BUILDING;
            heldItem = new Conveyor("Test Conveyor", "test", conveyorConfig, Item.Tier.COMMON, 0.0, 8);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            currentMode = Mode.BUILDING;
            heldItem = new Dropper( "Test Dropper", "test", dropperConfig, Item.Tier.COMMON, 0.0, "Test Ore", 20, 1, 1, .1f, dropperStrat);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            currentMode = Mode.BUILDING;
            heldItem = new Upgrader("Test Upgrader", "test", upgraderConfig, Item.Tier.COMMON, 0.0, 5, conditional, upgradeTag);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
            currentMode = Mode.BUILDING;
            heldItem = new Furnace("Test Furnace", "test", furnaceConfig, Item.Tier.COMMON, 0.0, 32, 5, testUpgrade);
        }
    }

    private void handleMovement(float deltaTime, OrthographicCamera camera) {
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.position.y += cameraSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.position.y -= cameraSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.position.x += cameraSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.position.x -= cameraSpeed *deltaTime;
        }

    }

    private void handleZoom(float deltaTime, OrthographicCamera camera) {
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom += .05f * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.05f * deltaTime;
            if (camera.zoom < 0.01f) {
                camera.zoom = 0.01f;
            }
        }

    }

    private void handlePlacement() {
        //If we are building we should check to see if we need to rotate the item and or place it.
        if (currentMode == Mode.BUILDING) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) { heldItem.rotateClockwise(); }
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (heldItem.didPlace(mouseWorld, contiguousPlacedItems)) { //make sure the item was placed down
                    contiguousPlacedItems.add(heldItem);
                    updateHeldItem();
                }
            } else {
                //Clear items as we are no longer holding down the place button.
                contiguousPlacedItems.clear();
            }
            //check to see if undid any placements.
            undo();
        }
    }

    private void updateHeldItem() {
        ButtonHelper.playPlaceSound();
        recentlyPlaced.push(heldItem);
        switch (heldItem) {
            case Upgrader upgrader -> heldItem = new Upgrader(upgrader);
            case Furnace furnace -> heldItem = new Furnace(furnace);
            case Dropper dropper -> heldItem = new Dropper(dropper);
            case Conveyor conveyor -> heldItem = new Conveyor(conveyor);
            default -> throw new IllegalStateException("Unexpected value: " + heldItem);
        }
    }

    private void undo() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && !recentlyPlaced.isEmpty()) {
                recentlyPlaced.pop().removeItem();
        } else if (currentMode != Mode.BUILDING) {
            recentlyPlaced.clear();
        }
    }

    private void handleSelecting() {
        if (currentMode != Mode.BUILDING && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (!isInvalid() && itemMap.getBlock(mouseWorld.x, mouseWorld.y) != null) {
                selectedItem = itemMap.getItem(mouseWorld);
                currentMode = Mode.SELECTING;
            } else {
                currentMode = Mode.DEFAULT;
                heldItem = null;
                selectedItems.clear();
            }
        }
        if (currentMode == Mode.SELECTING) {
            if (Gdx.input.isKeyPressed(Input.Keys.R)) {//Start build mode with the selected item.
                selectedItem.removeItem();
                heldItem = selectedItem;
                selectedItem = null;
                currentMode = Mode.BUILDING;
                return; //Return because we are no longer selecting.
            }
            if (Gdx.input.isKeyPressed(Input.Keys.Z)) { //Remove the selected Item from the base.
                selectedItem.removeItem();
                selectedItem = null;
                currentMode = Mode.DEFAULT;
                return; //return because we are no longer selecting.

            }
            if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                //Logic for item specific effects.
                //item.activate();
            }
        }


    }

    public Item getHeldItem() {
        return heldItem;
    }

    public boolean isBuilding() {
        return currentMode == Mode.BUILDING;
    }

    public boolean isSelecting() {
        return currentMode == Mode.SELECTING;
    }

    public ArrayList<Item> getSelectedItems() {
        return selectedItems;
    }

    private boolean isInvalid() {
        return mouseWorld.x > itemMap.mapTiles.length || mouseWorld.x < 0 || mouseWorld.y > itemMap.mapTiles[0].length || mouseWorld.y < 0;
    }



}
