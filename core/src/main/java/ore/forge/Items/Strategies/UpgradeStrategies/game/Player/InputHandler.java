package ore.forge.game.Items.Strategies.UpgradeStrategies.game.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.*;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies.AbstractUpgrade;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies.PrimaryUPGS.MultiplyUPG;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies.UpgradeStrategy;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Map;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.TheTycoonGame;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.UpgradeTag;

import java.util.Stack;

//@author Nathan Ulmen
public class InputHandler {
    protected static final Map map = Map.getSingleton();
    public Vector3 mouseScreen, mouseWorld;
    private float cameraSpeed;
    private boolean buildMode;
    private Item heldItem;
    public Item selectedItem;
    public Stack<Item> recentlyPlaced;
    private boolean isSelecting;
    public Rectangle selectionRectangle;

    public int[][] upgraderConfig = {//Test values
            { 0, 1, 1, 0},
            { 0, 2, 2, 0},
            { 0, 1, 1, 0},
//            {2,2},
//            {1,1},
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

    UpgradeStrategy testUpgrade = new MultiplyUPG(3.0, AbstractUpgrade.ValueToModify.ORE_VALUE);
    UpgradeTag upgradeTag = new UpgradeTag("Basic Upgrade Tag", 4, false);

    public InputHandler() {
        mouseScreen = new Vector3();
        mouseWorld = new Vector3();
        cameraSpeed = 20f;
        buildMode = false;
        isSelecting = false;
        selectionRectangle = new Rectangle();
    }

    public void updateMouse(OrthographicCamera camera) {
        mouseScreen.x = Gdx.input.getX();
        mouseScreen.y = Gdx.input.getY();
        mouseScreen.z = 0;

        mouseWorld.set(camera.unproject(mouseScreen));
    }

    public void handleInput(float deltaT, OrthographicCamera camera, TheTycoonGame game) {
        handleMovement(deltaT, camera);
        handleZoom(deltaT, camera);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!buildMode) {
                game.setScreen(game.pauseMenu);
            } else {
                buildMode = false;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            //buildMode, active item becomes conveyor
            if (!buildMode) {
                buildMode = true;
                heldItem = new Conveyor("Basic Conveyor", "test", conveyorConfig, Item.Tier.COMMON, 0.0, 50);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            //buildMode, active item becomes Dropper
            if (!buildMode) {
                buildMode = true;
                heldItem = new Dropper( "Test Dropper", "test", dropperConfig, Item.Tier.COMMON, 0.0, "Ore", 20, 1, 1, 0.05f);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            //buildMode, active item becomes upgrader
            if (!buildMode) {
                buildMode = true;
                heldItem = new Upgrader("Basic Upgrader", "test", upgraderConfig, Item.Tier.COMMON, 0.0, 3, testUpgrade, upgradeTag);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
            //buildmode, Active Item becomes Furnace
            if (!buildMode) {
                buildMode = true;
                heldItem = new Furnace("Basic Furnace", "test", furnaceConfig, Item.Tier.COMMON, 0.0, testUpgrade, 32, 5);
            }
        }
        handlePlacement();
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

    private void handlePlacement() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && buildMode) {//Will need to update to perform inventory checks.
            heldItem.placeItem((int)mouseWorld.x, (int)mouseWorld.y);
//            if (heldItem instanceof Conveyor) {
//                heldItem = new Conveyor((Conveyor) heldItem);
//            } else if (heldItem instanceof Upgrader) {
//                heldItem = new Upgrader((Upgrader) heldItem);
//            } else if (heldItem instanceof Dropper) {
//                heldItem = new Dropper((Dropper) heldItem);
//            } else if (heldItem instanceof Furnace) {
//                heldItem = new Furnace((Furnace) heldItem);
//            }
            switch (heldItem) {
                case Upgrader upgrader -> heldItem = new Upgrader(upgrader);
                case Furnace furnace -> heldItem = new Furnace(furnace);
                case Dropper dropper -> heldItem = new Dropper(dropper);
                case Conveyor conveyor -> heldItem = new Conveyor(conveyor);
                default -> throw new IllegalStateException("Unexpected value: " + heldItem);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && buildMode) {
            heldItem.rotateClockwise();
        }


    }

    private void handleObserverMode(){
        if (!buildMode && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (!isInvalidCoord() && map.getBlock(mouseWorld.x, mouseWorld.y) !=null) {
                selectedItem = map.getBlock(mouseWorld.x, mouseWorld.y).getParentItem();
                isSelecting = true;
            } else {
                isSelecting = false;
                selectedItem = null;
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

    private boolean isInvalidCoord() {
        return mouseWorld.x > map.mapTiles.length || mouseWorld.x < 0 || mouseWorld.y > map.mapTiles[0].length || mouseWorld.y < 0;
    }

}
