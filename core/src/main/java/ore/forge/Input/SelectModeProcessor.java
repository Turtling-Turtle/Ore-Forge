package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import ore.forge.Items.Item;

import java.util.ArrayList;

public class SelectModeProcessor implements InputMode {
    private Item selectedItem;
    private final Vector3 mouseWorld, mouseScreen;
    private final InputHandler handler;
    private final ArrayList<Item> selectedItems;

    public SelectModeProcessor(InputHandler inputHandler) {
        this.handler = inputHandler;
        mouseWorld = handler.mouseWorld;
        mouseScreen = handler.mouseScreen;
        selectedItems = new ArrayList<>();
    }

    @Override
    public void update(float delta, OrthographicCamera camera) {
        handleSelection();
        updateMouse(camera);
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    private void handleSelection() {
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            selectedItem.removeItem();
            selectedItem = null;
            handler.exitMode();
            //Change mode to Default/Observer
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            //TODO: MIGHT BE BUG
            selectedItem.removeItem();
            handler.exitMode();
            handler.setMode(InputHandler.Mode.BUILDING);
            //Change to BuildMode
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            //Handle logic for purchasing a copy of an item.
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            selectedItem.removeItem();
            selectedItem = null;
            handler.exitMode();
            //Handle Logic for selling the item.
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            //Trigger the selected items unique effect.
        }
    }


    private void updateMouse(OrthographicCamera camera) {
        mouseScreen.x = Gdx.input.getX();
        mouseScreen.y = Gdx.input.getY();
        mouseWorld.set(camera.unproject(mouseScreen));
    }

    public void setSelectedItem(Item item) {
        this.selectedItem = item;
    }

    public String toString() {
        return "Select Mode";
    }
}
