package ore.forge;

import ore.forge.Items.Item;
import ore.forge.Items.Upgrader;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class InventoryTester {

    public static void main(String[] args) {
        int[][] upgraderConfig = {//Test values
//            { 0, 1, 1, 0},
//            { 0, 2, 2, 0},
//            { 0, 1, 1, 0},
                {2,2},
                {1,1}
        };


        ArrayList<InventoryNode> inventoryNodes = new ArrayList<>();
        HashMap<String, Item> allItems = new HashMap<>();
        Upgrader upgrader = new Upgrader("Advanced Upgrader", "basic", upgraderConfig, Item.Tier.SPECIAL, 40.0, 3.0f, null, null);
        Upgrader upgrader1 = new Upgrader("Basic Upgrader", "Advanced", upgraderConfig, Item.Tier.EXOTIC, 40.0, 3.0f, null, null);
        Upgrader upgrader2 = new Upgrader("Awesome Upgrader", "Advanced", upgraderConfig, Item.Tier.EXOTIC, 40.0, 3.0f, null, null);
        allItems.put(upgrader.getName(), upgrader1);
        allItems.put(upgrader1.getName(), upgrader1);
        allItems.put(upgrader2.getName(), upgrader2);
        InventoryNode node = new InventoryNode(upgrader,10);
        InventoryNode node1 = new InventoryNode(upgrader1,10);
        InventoryNode node2 = new InventoryNode(upgrader2,10);
        inventoryNodes.add(node);
        inventoryNodes.add(node1);
        inventoryNodes.add(node2);
        Inventory inventory = new Inventory(allItems, inventoryNodes);
        for (int i = 0; i < 1_000; i++) {

        }



//        inventory.saveInventory();
        System.out.println(inventory);
//        inventory.sortByName();
//        inventory.sortByStored();
        inventory.sortByType();

        System.out.println(inventory);


        searchTest(inventory);
    }

    private static void searchTest(Inventory inventory) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the Item you want to search for!");
        String userInput = scanner.nextLine();

        long t1 = System.currentTimeMillis();
        ArrayList<InventoryNode> nodes = inventory.searchFor(userInput);
        for (InventoryNode node : nodes) {
            System.out.println(node.toString());
        }
        long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
    }


}
