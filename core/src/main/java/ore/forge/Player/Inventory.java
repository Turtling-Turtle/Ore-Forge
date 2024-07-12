package ore.forge.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import ore.forge.Color;
import ore.forge.Constants;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.ObtainedEvent;
import ore.forge.Items.Item;
import ore.forge.ItemManager;
import ore.forge.Stopwatch;

import java.lang.StringBuilder;
import java.util.*;
import java.util.concurrent.TimeUnit;

//@author Nathan Ulmen
public class Inventory {

    private final ArrayList<InventoryNode> inventoryNodes;
    private final HashMap<String, InventoryNode> lookUp;

    private final HashMap<String, Item> allItems;
    private final HashMap<String, ArrayList<InventoryNode>> cachedResults;

    public Inventory(ItemManager itemManager) {
        inventoryNodes = new ArrayList<>();
        allItems = itemManager.getAllItems();
        cachedResults = new HashMap<>();

        loadInventory();
        lookUp = new HashMap<>(inventoryNodes.size());
        for (InventoryNode node : inventoryNodes) {
            lookUp.put(node.getHeldItemID(), node);
        }
    }


    public InventoryNode getNode(String itemID) {
        return lookUp.get(itemID);
    }

    public ArrayList<InventoryNode> getInventoryNodes() {
        return inventoryNodes;
    }

    public void saveInventory() {
        Gdx.app.log("INVENTORY", "Saving Inventory");
        printInventory();
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        sortByName();

        List<InventoryData> inventoryDataList = new ArrayList<>(inventoryNodes.size());

        for (InventoryNode node : inventoryNodes) {
            String name = node.getHeldItem().getName();
            String type = node.getHeldItem().getClass().getSimpleName();
            String id = node.getHeldItemID();
            int owned = node.getTotalOwned();
            boolean isUnlocked = node.getHeldItem().isUnlocked();
            inventoryDataList.add(new InventoryData(name, type, owned, id, isUnlocked));
        }


        String jsonOutput = json.prettyPrint(inventoryDataList);
        FileHandle fileHandle = Gdx.files.local(Constants.INVENTORY_FP);
        fileHandle.writeString(jsonOutput, false);
    }

    public void loadInventory() {
//        System.out.println("loading inventory now!");
        Gdx.app.log("INVENTORY", "Loading Inventory");
        JsonReader jsonReader = new JsonReader();
        JsonValue fileContents = null;
        boolean isPresent = true;

        try {
            fileContents = jsonReader.parse(Gdx.files.local(Constants.INVENTORY_FP));
        } catch (SerializationException e) {
            Gdx.app.log("INVENTORY", "No inventory data present.");
            isPresent = false;
        }

        if (isPresent && fileContents != null) {
            //Go through each item in saved inventory and initialize node from allItems
            for (JsonValue jsonValue : fileContents) {
                if (allItems.containsKey(jsonValue.getString("id"))) {
                    String itemID = jsonValue.getString("id");
                    int owned = jsonValue.getInt("totalOwned");
                    InventoryNode node = new InventoryNode(allItems.get(itemID), owned);
                    node.getHeldItem().setUnlocked(jsonValue.getBoolean("isUnlocked"));
                    inventoryNodes.add(node);
                } else {
                    Gdx.app.log("INVENTORY", Color.highlightString("Unknown item ID: " + jsonValue.getString("id"), Color.YELLOW));
                }
            }
        } else {
            //TODO: create the default/beginner inventory.
        }

        //Add nodes for new Items that didn't exist previously.
        Stack<InventoryNode> nodesToAdd = new Stack<>();
        for (Item item : allItems.values()) {
            if (!containsItem(item.getID())) {
                nodesToAdd.push(new InventoryNode(item, 0));
            }
        }

        while (!nodesToAdd.isEmpty()) {
            inventoryNodes.add(nodesToAdd.pop());
        }

        sortByTier();
    }

    public void addItem(String itemID, int count) {

        for (InventoryNode node : inventoryNodes) {
            if (node.getHeldItemID().equals(itemID)) {
                node.addNew(count);
                EventManager.getSingleton().notifyListeners(new ObtainedEvent(node.getHeldItem(), count));
                return;
            }
        }
    }

    public void addItem(Item item, int count) {
        lookUp.get(item.getID()).addNew(count);
    }

    public void prestigeReset() {
        for (InventoryNode node : inventoryNodes) {
            if (!node.getHeldItem().isPrestigeProof()) {
                node.resetOwned();
            }
        }
    }

    public void pickUp(Item item) {
        lookUp.get(item.getID()).pickUp();
    }

    private boolean containsItem(String targetID) {
        for (InventoryNode node : inventoryNodes) {
            if (node.getHeldItemID().equals(targetID)) {
                return true;
            }
        }
        return false;
    }

    public void printInventory() {
        for (InventoryNode node : inventoryNodes) {
            Gdx.app.log("INVENTORY", "Item: " + node.getHeldItem().getName() + "\tNumber Owned: " + node.getTotalOwned());
        }
    }

    public void sortByName() {
        NameComparator nameComparator = new NameComparator();
        quickSort(nameComparator);
    }

    public void sortByType() {
        TypeComparator typeComparator = new TypeComparator();
        quickSort(typeComparator);
    }

    public void sortByTier() {
        TierComparator tierComparator = new TierComparator();
        quickSort(tierComparator);
    }

    public void sortByStored() {
        StoredComparator storedComparator = new StoredComparator();
        quickSort(storedComparator);
    }

    private <E extends Comparator<InventoryNode>> ArrayList<InventoryNode> bubbleSort(E compareType, ArrayList<InventoryNode> data) {
        for (int waterLine = data.size() - 1; waterLine >= 0; waterLine--) {
            for (int net = 0; net < waterLine; net++) {
                if (compareType.compare(data.get(net), data.get(net + 1)) > 0) {
                    InventoryNode temp = data.get(net);
                    data.set(net, data.get(net + 1));
                    data.set(net + 1, temp);
                }
            }
        }
        return data;
    }

    private <E extends Comparator<InventoryNode>> void quickSort(E compareType) {
        quickSort(compareType, 0, inventoryNodes.size() - 1);
    }

    private <E extends Comparator<InventoryNode>> void quickSort(E compareType, int min, int max) {
        if (min >= max) {
            return;
        }
        int indexOfPartition = partition(compareType, min, max);

        quickSort(compareType, min, indexOfPartition - 1);
        quickSort(compareType, indexOfPartition + 1, max);
    }

    private <E extends Comparator<InventoryNode>> int partition(E compareType, int min, int max) {
        InventoryNode partitionedElement;
        int left, right;
        int midpoint = (min + max) / 2;
        partitionedElement = inventoryNodes.get(midpoint);
        swap(midpoint, min);

        left = min;
        right = max;
        while (left < right) {
            while (left < max && compareType.compare(inventoryNodes.get(left), partitionedElement) <= 0) {
                left++;
            }

            while (right > min && compareType.compare(inventoryNodes.get(right), partitionedElement) > 0) {
                right--;
            }
            if (left < right) {
                swap(left, right);
            }
        }

        swap(min, right);
        return right;
    }

    private void swap(int firstIndex, int secondIndex) {
        var temp = inventoryNodes.get(secondIndex);
        inventoryNodes.set(secondIndex, inventoryNodes.get(firstIndex));
        inventoryNodes.set(firstIndex, temp);
    }

    public ArrayList<InventoryNode> searchFor(String userInput) {
        var stpwatch = new Stopwatch(TimeUnit.MICROSECONDS);
        stpwatch.start();
        userInput = userInput.toLowerCase();
        if (!userInput.isEmpty()) {
            if (cachedResults.containsKey(userInput)) {
                stpwatch.stop();
                Gdx.app.log("INVENTORY", Color.highlightString("Search completed in " + stpwatch.getElapsedTime() + " micro seconds", Color.GREEN));
                return cachedResults.get(userInput);
            }
            ArrayList<InventoryNode> desiredItems = new ArrayList<>();
            for (InventoryNode node : inventoryNodes) {
                if (node.getName().toLowerCase().contains(userInput)) {
                    desiredItems.add(node);
                }
            }
            var sortedResults = bubbleSort(new TierComparator(), desiredItems);
            cachedResults.put(userInput, desiredItems);
            stpwatch.stop();
            Gdx.app.log("INVENTORY", Color.highlightString("Search completed in " + stpwatch.getElapsedTime() + " micro seconds", Color.YELLOW));
            return sortedResults;
        }
        return inventoryNodes;
    }

    public String toString() {
        StringBuilder string = new StringBuilder();
        for (InventoryNode node : inventoryNodes) {
            string.append(node.toString()).append("\n");
        }
        return string.toString();
    }

    static class NameComparator implements Comparator<InventoryNode> {
        @Override
        public int compare(InventoryNode node1, InventoryNode node2) {
            return node1.getName().compareTo(node2.getName());
        }
    }

    static class TierComparator implements Comparator<InventoryNode> {
        //Sort by Tier should sort by tier, then type, then Name.
        @Override
        public int compare(InventoryNode node1, InventoryNode node2) {
            //Tier
            int result = node1.getHeldItem().getTier().compareTo(node2.getHeldItem().getTier());
            if (result != 0) {
                return result;
            }
            //Type
            result = node1.getHeldItem().getClass().getSimpleName().
                compareTo(node2.getHeldItem().getClass().getSimpleName());
            if (result != 0) {
                return result;
            }
            //Name
            return node1.getName().compareTo(node2.getName());

        }
    }

    static class TypeComparator implements Comparator<InventoryNode> {
        //Sort by type should sort by type, tier,  then name.
        @Override
        public int compare(InventoryNode node1, InventoryNode node2) {
            //Type
            int result = node1.getHeldItem().getClass().getSimpleName().
                compareTo(node2.getHeldItem().getClass().getSimpleName());
            if (result != 0) {
                return result;
            }
            //Tier
            result = node1.getHeldItem().getTier().compareTo(node2.getHeldItem().getTier());
            if (result != 0) {
                return result;
            }
            //Name
            return node1.getName().compareTo(node2.getName());

        }
    }

    //Sort by Most to least, currently doesn't do that.
    static class StoredComparator implements Comparator<InventoryNode> {
        @Override
        public int compare(InventoryNode node1, InventoryNode node2) {
            Integer firstStored = node1.getStored();
            Integer secondStored = node2.getStored();
            return firstStored.compareTo(secondStored);
        }
    }

    private record InventoryData(String itemName, String type, int totalOwned, String id, boolean isUnlocked) {}
}
