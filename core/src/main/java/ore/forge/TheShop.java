package ore.forge;


import ore.forge.Items.Item;

import java.util.HashMap;
import java.util.function.Function;

import static ore.forge.Strategies.UpgradeStrategies.BasicUpgrade.Operator.ADD;
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
//        Function<Double, Boolean> comparator;
//        String string = null;
//        comparator = switch (string) {
//            case "Greater Than" -> (x) -> x > threshold;
//            case "Less Than" -> (x) -> x < threshold;
//            case "equal" -> (x) -> x == threshold;
//            default -> throw new IllegalStateException("Unexpected value: " + string);
//        };
//        System.out.println(comparator.apply(value2));
    }

    public TheShop(ResourceManager resourceManager) {
        allItems = resourceManager.getAllItems();
    }




}
