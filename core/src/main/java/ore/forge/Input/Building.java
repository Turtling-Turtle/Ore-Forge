package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.ItemPlacedGameEvent;
import ore.forge.ItemMap;
import ore.forge.Items.*;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;

import java.util.ArrayList;
import java.util.HashMap;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.badlogic.gdx.Input.Keys.R;

public class Building extends InputAdapter {
    private ArrayList<Item> items;
    private ArrayList<Vector2> offsets;
    private final InputManager manager;
    private boolean mouseHeld;

    public Building(InputManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean keyUp(int keycode) {
        return switch (keycode) {
            case R -> {
                assert offsets.size() == items.size();
                for (int i = 0; i < offsets.size(); i++) {
                    var offset = offsets.get(i);
                    var item = items.get(i);
                    float temp = offset.x;
                    offset.x = -offset.y;
                    offset.y = temp;
                    item.rotateClockwise();
                }
                yield true;
            }
            case ESCAPE -> {
                stopBuilding();
                manager.getMultiplexer().removeProcessor(this);
                yield true;
            }
            default -> false;
        };
    }

    public void update() {
        if (mouseHeld) {
            placeItems();
            System.out.println("Made it here_!#!");
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            mouseHeld = false;
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            mouseHeld = true;
            System.out.println("CLICKED!!!_()!");
        }

        return true;
    }

    public void startBuilding(ArrayList<Item> itemsToPlace, ArrayList<Vector2> offsets) {
        for (Item item : itemsToPlace) {
            item.removeItem();
        }
        items = itemsToPlace;
        this.offsets = offsets;
    }

    public void stopBuilding() {
        items = null;
        offsets = null;
        manager.getMultiplexer().removeProcessor(this);
    }

    public boolean hasEnoughCheck() {
        HashMap<String, Integer> lookup = new HashMap<>();
        for (Item item : items) {
            String itemID = item.getID();
            if (lookup.containsKey(itemID)) {
                int count = lookup.get(itemID);
                count++;
                lookup.put(itemID, count);
            } else {
                lookup.put(itemID, 1);
            }
        }

        Inventory inventory = manager.getInventory();

        for (Item item : items) {
            InventoryNode node = inventory.getNode(item.getID());
            if (node.getStored() < lookup.get(item.getID())) {
                return false;
            }
        }
        return true;
    }

    public void placeItems() {
        Vector3 mouseWorld = manager.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        if (!ItemMap.getSingleton().isInBounds(mouseWorld) || !canPlaceAll()) { return; }
        Inventory inventory = manager.getInventory();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            Vector2 offset = offsets.get(i);
            item.fastPlace((int) (mouseWorld.x + offset.x), (int) (mouseWorld.y + offset.y));
            inventory.getNode(item).place();
            EventManager.getSingleton().notifyListeners(new ItemPlacedGameEvent(item));
        }
        if (!hasEnoughCheck()) {
            stopBuilding();
            return;
        }
        copyHeldItems();
    }

    public boolean canPlaceAll() {
        Vector3 mouseWorld = manager.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            Vector2 offset = offsets.get(i);
            if (!item.canBePlaced((int) (mouseWorld.x + offset.x), (int) (mouseWorld.y + offset.y))) {
                Gdx.app.log("Building", "Could not place all items, something in the way");
                return false;
            }
        }
        return true;
    }

    public void copyHeldItems() {
        items.replaceAll(item -> switch (item) {
            case Dropper dropper -> new Dropper(dropper);
            case Furnace furnace -> new Furnace(furnace);
            case Upgrader upgrader -> new Upgrader(upgrader);
            case Conveyor conveyor -> new Conveyor(conveyor);
            default -> throw new IllegalStateException("Unexpected value: " + item);
        });
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public ArrayList<Vector2> getOffsets() {
        return offsets;
    }

    public String toString() {
        return "Build Mode";
    }

}
