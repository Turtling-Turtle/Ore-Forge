package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import ore.forge.ButtonHelper;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.ItemPlacedEvent;
import ore.forge.Items.*;
import ore.forge.Player.InventoryNode;

import java.util.ArrayList;
import java.util.Stack;


/**
 * Build Mode:
 * Rotate Right 90 Degrees: 'R'.
 * Undo: 'Z',
 * Place: 'Left Click'
 * Cancel/Exit: 'Escape'
 */
public class BuildMode extends InputMode {
    private Item heldItem;
    private InventoryNode heldNode;
    private final Stack<Item> recentlyPlaced;
    private final ArrayList<Item> contiguousPlacedItems;

    public BuildMode(InventoryNode heldNode, Item heldItem) {
        this.heldItem = heldItem;
        this.heldNode = heldNode;
        recentlyPlaced = new Stack<>();
        recentlyPlaced.setSize(30);
        contiguousPlacedItems = new ArrayList<>(30);
    }

    @Override
    public void update(float deltaTime, OrthographicCamera camera, InputHandler handler) {
        updateCameraPosition(deltaTime, camera);
        updateCameraZoom(deltaTime, camera);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || !heldNode.hasSupply()) {
            if (handler.getPreviousMode() instanceof SelectMode) {
                handler.setCurrentMode(handler.getObserverMode());
            } else {
                handler.setCurrentMode(handler.getInventoryMode());
            }
            return;
        }
        handlePlacement(handler);
    }

    @Override
    public void setActive(InputHandler context) {
        cloneItem(context.getHeldNode().getHeldItem());
        heldNode = context.getHeldNode();
        recentlyPlaced.clear();
    }

    public Item getHeldItem() {
        return heldItem;
    }

    private void handlePlacement(InputHandler handler) {
        assert heldItem.getName().equals(heldNode.getHeldItem().getName());
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            heldItem.rotateClockwise();
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (heldNode.hasSupply() && heldItem.didPlace(handler.getMouseWorld(), contiguousPlacedItems)) { //make sure the item was placed down
                heldNode.place();
                contiguousPlacedItems.add(heldItem);
//                Gdx.app.log("BUILD MODE", "Placed Item at " + String.valueOf(heldItem.getVector2()));
                EventManager.getSingleton().notifyListeners(new ItemPlacedEvent(heldItem));
                ButtonHelper.playPlaceSound();
                recentlyPlaced.push(heldItem);
                cloneItem(heldItem);
            }
        } else {
            //Clear items as we are no longer holding down the place button.
            contiguousPlacedItems.clear();
        }
        undoLastAction();
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
    private void undoLastAction() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && !recentlyPlaced.isEmpty()) {
            recentlyPlaced.pop().removeItem();
        }
    }


    public String toString() {
        return "BuildModeProcessor\t";
    }


}
