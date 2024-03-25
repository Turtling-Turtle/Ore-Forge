package ore.forge.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import ore.forge.ButtonHelper;
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
    protected static final ItemMap itemMap = ItemMap.getSingleton();
    protected static final Player player = Player.getSingleton();
    public Vector3 mouseScreen, mouseWorld;
    private final float cameraSpeed;
    private boolean buildMode;
    private Item heldItem;
    public Item selectedItem;
    public Stack<Item> recentlyPlaced;
    private InventoryNode heldNode;
    private boolean isSelecting;
    private boolean isHeldDown;
    public Rectangle selectionRectangle;
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


    UpgradeStrategy testUpgrade = new BasicUpgrade(3.0, BasicUpgrade.Operator.MULTIPLY, BasicUpgrade.ValueToModify.ORE_VALUE);

    UpgradeStrategy destroy = new DestructionUPG();
    UpgradeStrategy conditional = new ConditionalUPG(testUpgrade, destroy, ConditionalUPG.Condition.VALUE, 100000*100000, ConditionalUPG.Comparison.GREATER_THAN);

    UpgradeTag upgradeTag = new UpgradeTag("Basic Upgrade Tag", 4, false);

    OreEffect invincibility = new Invulnerability(12, 10f);

    UpgradeStrategy simpleMultiply = new BasicUpgrade(1.02, BasicUpgrade.Operator.MULTIPLY, BasicUpgrade.ValueToModify.ORE_VALUE);
    OreEffect upgradeOverTime = new UpgradeOverTimeEffect(1, 10E10f, simpleMultiply);

    UpgradeStrategy basicUpgrade = new BasicUpgrade(.1, BasicUpgrade.Operator.MULTIPLY, BasicUpgrade.ValueToModify.ORE_VALUE);
    UpgradeStrategy influencedUpgrade = new InfluencedUPG(InfluencedUPG.ValuesOfInfluence.VALUE, (BasicUpgrade) basicUpgrade, BasicUpgrade.Operator.MULTIPLY);
    OreEffect influencedUpgradeOverTime = new UpgradeOverTimeEffect(1, 2E10f, influencedUpgrade);

    OreEffect dropperStrat = new BundledEffect(invincibility, upgradeOverTime, influencedUpgradeOverTime, null);


    public InputHandler() {
        mouseScreen = new Vector3();
        mouseWorld = new Vector3();
        cameraSpeed = 20f;
        buildMode = false;
        isSelecting = false;
        selectionRectangle = new Rectangle();
        contiguousPlacedItems = new ArrayList<>();
        recentlyPlaced = new Stack<>();
        selectedItems = new ArrayList<>();
    }

    public void updateMouse(OrthographicCamera camera) {
        mouseScreen.x = Gdx.input.getX();
        mouseScreen.y = Gdx.input.getY();
        mouseScreen.z = 0;

        mouseWorld.set(camera.unproject(mouseScreen));
    }

    public void handleInput(float deltaT, OrthographicCamera camera, OreForge game) {
        handleMovement(deltaT, camera);
        handleZoom(deltaT, camera);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!buildMode) {
                game.setScreen(game.pauseMenu);
//                map.saveState();
            } else {
                buildMode = false;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            //buildMode, active item becomes conveyor
            if (!buildMode) {
                buildMode = true;
                heldItem = new Conveyor("Basic Conveyor", "test", conveyorConfig, Item.Tier.COMMON, 0.0, 8);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            //buildMode, active item becomes Dropper
            if (!buildMode) {
                buildMode = true;
                heldItem = new Dropper( "Test Dropper", "test", dropperConfig, Item.Tier.COMMON, 0.0, "Test Ore", 20, 1, 1, 1.f, dropperStrat);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            //buildMode, active item becomes upgrader
            if (!buildMode) {
                buildMode = true;
                heldItem = new Upgrader("Basic Upgrader", "test", upgraderConfig, Item.Tier.COMMON, 0.0, 5, conditional, upgradeTag);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
            //buildmode, Active Item becomes Furnace
            if (!buildMode) {
                buildMode = true;
                heldItem = new Furnace("Basic Furnace", "test", furnaceConfig, Item.Tier.COMMON, 0.0, 32, 5, testUpgrade);
            }
        }
//        normalPlacement();
        handleDragPlacement();
        handleObserverMode();
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

    private void handleDragPlacement() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && buildMode) {
            heldItem.rotateClockwise();
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && buildMode) {
            isHeldDown = true;
            //1st Attempt to placeItem, Check/compare to all previously placed items in this "session" of drag placement
            if (heldItem.placeItem(mouseWorld, contiguousPlacedItems)) {
                contiguousPlacedItems.add(heldItem);
                updateHeldItem();
            }
        } else {
            isHeldDown = false;
            contiguousPlacedItems.clear();
        }
        undo();
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && buildMode) {
            if (!recentlyPlaced.isEmpty()) {
                recentlyPlaced.pop().removeItem();
            }
        } else if (!buildMode) {
            recentlyPlaced.clear();
        }
    }

    private void handleObserverMode(){
        if (!buildMode && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (!isInvalid() && itemMap.getBlock(mouseWorld.x, mouseWorld.y) !=null) {
                selectedItem = itemMap.getItem(mouseWorld);
                isSelecting = true;
            } else {
                isSelecting = false;
                selectedItem = null;
                contiguousPlacedItems.clear();
            }
        }
        if (isSelecting && Gdx.input.isKeyPressed(Input.Keys.R)) {//Start build mode with the selected item.
            isSelecting = false;
            selectedItem.removeItem();
            buildMode = true;
            heldItem = selectedItem;
            selectedItem = null;
        }
        if (isSelecting && Gdx.input.isKeyPressed(Input.Keys.Z)) {
            selectedItem.removeItem();
            isSelecting = false;
            selectedItem = null;
        }
        if (isSelecting && Gdx.input.isKeyPressed(Input.Keys.E)) {
            //Logic for item specific effects.
            //item.activate();
        }
    }

    public Item getHeldItem() {
        return heldItem;
    }

    public boolean isBuilding() {
        return buildMode;
    }

    public boolean isSelecting() {
        return isSelecting;
    }

    public ArrayList<Item> getSelectedItems() {
        return selectedItems;
    }
    private boolean isInvalid() {
        return mouseWorld.x > itemMap.mapTiles.length || mouseWorld.x < 0 || mouseWorld.y > itemMap.mapTiles[0].length || mouseWorld.y < 0;
    }


}
