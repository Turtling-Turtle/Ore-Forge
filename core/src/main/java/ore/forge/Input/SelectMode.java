package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import ore.forge.ItemMap;
import ore.forge.Items.Item;


public class SelectMode extends InputMode {
    protected static final ItemMap itemMap = ItemMap.getSingleton();
    private Item selectedItem;

    @Override
    public void update(float deltaTime, OrthographicCamera camera, InputHandler context) {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (!context.isCoordinatesInvalid() && itemMap.getBlock(context.getMouseWorld().x, context.getMouseWorld().y) != null) {
                selectedItem = itemMap.getItem(context.mouseWorld);
                context.setHeldItem(selectedItem);
            } else {
                context.setCurrentMode(context.getObserverMode());
                return;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            context.setCurrentMode(context.getObserverMode());
            return;
        }
        handleSelection(context);
    }

    @Override
    public void setActive(InputHandler context) {
        this.selectedItem = context.getHeldItem();
    }


    public Item getSelectedItem() {
        return selectedItem;
    }

    private void handleSelection(InputHandler handler) {
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            selectedItem.removeItem();
            handler.setCurrentMode(handler.getObserverMode());
            return;
            //Change mode to Default/Observer
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            selectedItem.removeItem();
            handler.setHeldItem(selectedItem);
            handler.setCurrentMode(handler.getBuildMode());
            return;
            //Change to BuildMode
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            //Handle logic for purchasing a copy of an item.
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            selectedItem.removeItem();
            //Handle Logic for selling the item.
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            //Trigger the selected items unique effect.
        }
    }

    public void setSelectedItem(Item item) {
        this.selectedItem = item;
    }


    public String toString() {
        return "Select Mode";
    }

}
