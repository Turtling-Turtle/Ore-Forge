package ore.forge.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import ore.forge.Constants;
import ore.forge.Items.Item;
import ore.forge.ResourceManager;

import java.lang.StringBuilder;
import java.util.*;

//@author Nathan Ulmen
public class Inventory {

    private final ArrayList<InventoryNode> inventoryNodes;

    private final HashMap<String, Item> allItems;

    public Inventory(ResourceManager resourceManager) {
        inventoryNodes = new ArrayList<>();
        allItems = resourceManager.getAllItems();
        loadInventory();
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
            inventoryDataList.add(new InventoryData(name,type,owned,id));
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
            for (JsonValue jsonValue: fileContents) {
                if (allItems.containsKey(jsonValue.getString("id"))) {
                    String itemID = jsonValue.getString("id");
                    int owned = jsonValue.getInt("totalOwned");
                    InventoryNode node = new InventoryNode(allItems.get(itemID), owned);
                    inventoryNodes.add(node);
                } else {
                    System.out.println(Constants.RED + jsonValue.getString("itemName") + "is not a valid Item" + Constants.DEFAULT);
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

    }

    private boolean containsItem(String targetID) {
        for (InventoryNode node : inventoryNodes) {
            if (node.getHeldItemID().equals(targetID)){
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
        bubbleSort(nameComparator);
    }

    public void sortByType() {
        TypeComparator typeComparator = new TypeComparator();
        bubbleSort(typeComparator);
    }

    public void sortByTier() {
        TierComparator tierComparator = new TierComparator();
        bubbleSort(tierComparator);
    }

    public void sortByStored() {
        StoredComparator storedComparator = new StoredComparator();
        bubbleSort(storedComparator);
    }

    private <E extends Comparator<InventoryNode>> void bubbleSort(E compareType) {
        for (int waterLine = inventoryNodes.size()-1; waterLine>=0; waterLine--) {
            for (int net = 0; net < waterLine; net++) {
                if (compareType.compare(inventoryNodes.get(net), inventoryNodes.get(net+1)) >0) {
                    InventoryNode temp = inventoryNodes.get(net);
                    inventoryNodes.set(net, inventoryNodes.get(net+1));
                    inventoryNodes.set(net+1, temp);
                }
            }
        }
    }

    //Make it not case-sensitive. E == e
    //Make it so it looks for items that contain string.
    // EX: Upgrade would return All items that have upgrade in name.
    public ArrayList<InventoryNode> searchFor(String userInput) {
        ArrayList<InventoryNode> desiredItems = new ArrayList<>();
        userInput = userInput.toLowerCase();
        for (InventoryNode node : inventoryNodes) {
            if (node.getName().toLowerCase().contains(userInput)) {
                desiredItems.add(node);
            }
        }

        return desiredItems;
    }

    public String toString() {
        StringBuilder string = new StringBuilder();
        for (InventoryNode node: inventoryNodes) {
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
            if(result != 0) {return result;}
            //Type
            result = node1.getHeldItem().getClass().getSimpleName().
                    compareTo(node2.getHeldItem().getClass().getSimpleName());
            if (result != 0) {return result;}
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
            if (result != 0) {return result;}
            //Tier
            result = node1.getHeldItem().getTier().compareTo(node2.getHeldItem().getTier());
            if(result != 0) {return result;}
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

    private record InventoryData(String itemName, String type, int totalOwned, String id) {}

}
