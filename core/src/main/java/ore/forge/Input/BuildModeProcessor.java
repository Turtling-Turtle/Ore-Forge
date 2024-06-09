package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import ore.forge.ButtonHelper;
import ore.forge.Items.*;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;

import java.util.ArrayList;
import java.util.Stack;


/**
* Build Mode:
* Rotate Right 90 Degrees: 'R'.
* Undo: 'Z',
* Place: 'Left Click'
* Cancel/Exit: 'Escape'
* */
public class BuildModeProcessor implements InputMode {
    private static final float ZOOM_SPEED = 0.05f;
    private static final float CAMERA_SPEED = 20.0f;
    private final Vector3 mouseWorld, mouseScreen;
    private Item heldItem;
    private InventoryNode heldNode;
    private final InputHandler inputHandler;

    private Inventory inventory;

    private final Stack<Item> recentlyPlaced;
    private final ArrayList<Item> contiguousPlacedItems;

    public BuildModeProcessor(InputHandler inputHandler) {
        mouseWorld = inputHandler.mouseWorld;
        mouseScreen = inputHandler.mouseScreen;
        recentlyPlaced = new Stack<>();
        recentlyPlaced.setSize(30);
        contiguousPlacedItems = new ArrayList<>(30);
        this.inputHandler = inputHandler;
    }

    @Override
    public void update(float delta, OrthographicCamera camera) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || !heldNode.hasSupply()) {
            inputHandler.exitMode();
            return;
        }
        updateMouse(camera);
        handleZoom(delta,camera);
        handleMovement(delta,camera);
        handlePlacement();
    }

    public void setHeldNode(InventoryNode heldNode) {
        this.heldNode = heldNode;
        System.out.println(heldNode.getHeldItem());
        cloneItem(heldNode.getHeldItem());
    }

    public Item getHeldItem() {
        return heldItem;
    }

    private void handlePlacement() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            heldItem.rotateClockwise();
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (heldNode.hasSupply() && heldItem.didPlace(mouseWorld, contiguousPlacedItems)) { //make sure the item was placed down
                heldNode.place();
                contiguousPlacedItems.add(heldItem);
                System.out.println(heldItem.getVector2());
                ButtonHelper.playPlaceSound();
                recentlyPlaced.push(heldItem);
                cloneItem(heldItem);
            }
        } else {
            //Clear items as we are no longer holding down the place button.
            contiguousPlacedItems.clear();
        }
        undo();
    }

    public void handleZoom(float delta, OrthographicCamera camera) {
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom -= ZOOM_SPEED * delta;
            if (camera.zoom < 0.01f) {
                camera.zoom = 0.01f;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom += ZOOM_SPEED * delta;
        }
    }

    public void handleMovement(float delta, OrthographicCamera camera) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.position.y += CAMERA_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.position.y -= CAMERA_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.position.x += CAMERA_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.position.x -= CAMERA_SPEED * delta;
        }
    }


    private void cloneItem(Item item) {
        if (item == null) {
            throw new IllegalStateException("Item not set");
        }
        switch (item) {
            case Upgrader upgrader -> heldItem = new Upgrader(upgrader);
            case Furnace furnace -> heldItem = new Furnace(furnace);
            case Dropper dropper -> heldItem = new Dropper(dropper);
            case Conveyor conveyor -> heldItem = new Conveyor(conveyor);
            default -> throw new IllegalStateException("Unexpected value: " + heldItem);
        }
    }

    //TODO: Change so that nodes are updated.
    private void undo() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && !recentlyPlaced.isEmpty()) {
            recentlyPlaced.pop().removeItem();
        } else {
            recentlyPlaced.clear();
        }
    }

    private void updateMouse(OrthographicCamera camera) {
        mouseScreen.x = Gdx.input.getX();
        mouseScreen.y = Gdx.input.getY();
        mouseScreen.z = 0;
        mouseWorld.set(camera.unproject(mouseScreen));
    }

    public String toString() {
        return "BuildModeProcessor\t" +  heldNode;
    }


}
