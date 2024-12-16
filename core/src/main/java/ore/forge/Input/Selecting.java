package ore.forge.Input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import ore.forge.Currency;
import ore.forge.ItemMap;
import ore.forge.Items.Item;
import ore.forge.Player.Inventory;
import ore.forge.Player.Player;

import java.util.ArrayList;
import java.util.HashMap;

import static com.badlogic.gdx.Input.Buttons.LEFT;
import static com.badlogic.gdx.Input.Keys.*;

/**
 * @author Nathan Ulmen
 * mousing over an item will select it.
 */
public class Selecting extends InputAdapter {
    private final InputManager inputManager;
    private ArrayList<Item> selectedItems;
    private ArrayList<Vector2> offsets;
    private boolean mouseHeld;

    public Selecting(InputManager inputManager) {
        selectedItems = new ArrayList<>();
        offsets = new ArrayList<>();
        this.inputManager = inputManager;
    }

    public void addToSelectionList(Item item) {
        assert offsets.size() == selectedItems.size();
        if (selectedItems.isEmpty()) {
            selectedItems.add(item);
            offsets.add(new Vector2(0, 0));
        } else {
            selectedItems.add(item);
            Vector2 origin = selectedItems.getFirst().getVector2();
            Vector2 itemPosition = item.getVector2();
            Vector2 offset = new Vector2(itemPosition.x - origin.x, itemPosition.y - origin.y);
            offsets.add(offset);
        }
        assert offsets.size() == selectedItems.size();
    }

    public void update() {
        System.out.println("Called update in selecting !!111");
        if (mouseHeld) {
            System.out.println("Selecting logic going!!");
            var item = ItemMap.getSingleton().getItem(inputManager.getMouseWorld());
            if (item != null && !selectedItems.contains(item)) {
                addToSelectionList(item);
            }
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        return switch (keycode) {
            case ESCAPE -> {
                //exit selecting
                yield true;
            }
            case R -> {
                //TODO pickup all items then transition into building.
                inputManager.multiplexer.removeProcessor(this);
                inputManager.getBuilding().startBuilding(selectedItems, offsets);
                selectedItems = new ArrayList<>(10); //"Reset Lists after passing to building."
                offsets = new ArrayList<>(10);
                inputManager.multiplexer.addProcessor(0, inputManager.getBuilding());
                yield true;
            }
            case C -> {
                double totalCashCost = 0, totalSpecialPointCost = 0;
                HashMap<String, Integer> lookup = new HashMap<>();
                for (Item item : selectedItems) {
                    if (item.getCurrencyBoughtWith() == Currency.CASH) {
                        totalCashCost += item.getItemValue();
                    } else if (item.getCurrencyBoughtWith() == Currency.SPECIAL_POINTS) {
                        totalSpecialPointCost += item.getItemValue();
                    }
                    lookup.put(item.getID(), lookup.getOrDefault(item.getID(), 0) + 1);
                }

                if (totalCashCost <= Player.getSingleton().getWallet() && totalSpecialPointCost <= Player.getSingleton().getSpecialPoints()) {
                    for (String itemId : lookup.keySet()) {
                        Player.getSingleton().purchaseItem(itemId, lookup.get(itemId));
                    }
                }
                yield true;
            }
            case Z -> {
                Inventory inventory = inputManager.getInventory();
                for (Item item : selectedItems) {
                    item.removeItem(); //remove item from base
                    inventory.getNode(item).pickUp(); //adjust number in inventory
                }
                offsets.clear();
                selectedItems.clear();
                // return to default state
                inputManager.getMultiplexer().removeProcessor(this);
                yield true;
            }
            case X -> {

                //Sell the item.
                yield true;
            }

            default -> false;
        };
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        System.out.println("Updating in selecting(TouchUP)!!");
        return switch (button) {
            case LEFT -> {
                mouseHeld = false;
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        System.out.println("Updating in selecting(TouchDown)!!");
        return switch (button) {
            case LEFT -> {
                selectedItems.clear();
                offsets.clear();
                mouseHeld = true;

                yield true;
            }
            default -> false;
        };
    }

    public ArrayList<Item> getSelectedItems() {
        return selectedItems;
    }

    public String toString() {
        return "Selecting Mode";
    }

}
