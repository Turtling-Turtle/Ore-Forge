package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import ore.forge.ItemMap;
import ore.forge.Items.Item;
import ore.forge.Player.Player;


public class SelectMode extends InputMode {
    protected static final ItemMap itemMap = ItemMap.getSingleton();
    private final Player player = Player.getSingleton();
    private Item selectedItem;

    @Override
    public void update(float deltaTime, OrthographicCamera camera, InputHandler context) {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (context.isCoordsValid() && itemMap.getBlock(context.getMouseWorld().x, context.getMouseWorld().y) != null) {
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
            player.purchaseItem(selectedItem, 1);
            //Handle logic for purchasing a copy of an item.
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            selectedItem.removeItem();
            var node = player.getInventory().getNode(selectedItem.getID());
            node.sellFromBase();
            switch (selectedItem.getCurrencyBoughtWith()) {
                case PRESTIGE_POINTS ->
                    player.setPrestigeCurrency((int) (player.getPrestigeCurrency() + selectedItem.getSellPrice()));
                case SPECIAL_POINTS ->
                    player.setSpecialPoints((long) (player.getSpecialPoints() + selectedItem.getSellPrice()));
                case CASH -> player.setWallet(player.getWallet() + selectedItem.getSellPrice());
            }
            handler.setCurrentMode(handler.getObserverMode());
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
