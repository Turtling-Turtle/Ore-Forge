package ore.forge;


import ore.forge.Items.Item;

import java.util.*;

//@author Nathan Ulmen
public class TheShop {
    private final HashMap<String, Item> allItems;
    public static void main(String[] args) {
        //You have the origin of an obejct (which is the position)
        // You have the area of an object
        //When you move you go through all entities and compare origin and position to the area of all other enteties.
        int [][] conveyorConfig = {
                {1, 1},
                {1, 1},
                {1, 1, 1, 1, 1, 1},
        };
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            UUID uuid = new UUID(random.nextInt(), random.nextInt());
            System.out.println(uuid);
        }
//        Function<Double, Boolean> comparator;
//        String string = null;
//        comparator = switch (string) {
//            case "Greater Than" -> (x) -> x > threshold;
//            case "Less Than" -> (x) -> x < threshold;
//            case "equal" -> (x) -> x == threshold;
//            default -> throw new IllegalStateException("Unexpected value: " + string);
//        };
//        System.out.println(comparator.apply(value2));
        // Define the size of the list
    }

    public TheShop(ResourceManager resourceManager) {
        allItems = resourceManager.getAllItems();
    }




}
