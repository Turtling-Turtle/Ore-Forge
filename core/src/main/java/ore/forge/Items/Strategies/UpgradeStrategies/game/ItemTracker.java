package ore.forge.game.Items.Strategies.UpgradeStrategies.game;


import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Item;

import java.util.ArrayList;

public class ItemTracker {
    private final ArrayList<Item> placedItems = new ArrayList<>();

    protected static ItemTracker itemTracker = new ItemTracker();

    public static ItemTracker getSingleton() {
        return itemTracker;
    }

    public void add(Item item) {
        placedItems.add(item);
    }

    public void remove(Item item2) {
        placedItems.remove(item2);
    }

    public ArrayList<Item> getPlacedItems() {
        return placedItems;
    }

}
