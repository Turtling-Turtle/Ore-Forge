package ore.forge;
import ore.forge.Items.Item;

import java.util.*;

//@author Nathan Ulmen
public class TheShop {
    private final HashMap<String, Item> allItems;
    public static void main(String[] args) {
        //You have the origin of an obejct (which is the position)
        // You have the area of an object
        //When you move you go through all entities and compare origin and position to the area of all other entities.

        int [][] conveyorConfig = {
                {1, 1},
                {1, 1},
                {1, 1, 1, 1, 1, 1},
        };
        int zero = 0;
        int number2 = 2;
        int number3 = 3;
        number3 = number2;
        number2++;
        System.out.println(number2/zero);
        System.out.println(number2);

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
