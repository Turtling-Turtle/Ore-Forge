package ore.forge;


import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Strategies.OreStrategies.Invulnerability;
import ore.forge.Strategies.UpgradeStrategies.BasicUpgrade;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

public class TheShop {
    static float deltaTime;
    static boolean isRunning;
    public static void main(String[] args) {
        //You have the origin of an obejct (which is the position)
        // You have the area of an object
        //When you move you go through all entities and compare origin and position to the area of all other enteties.
        int [][] conveyorConfig = {
                {1, 1},
                {1, 1},
                {1, 1, 1, 1, 1, 1},
        };
        double value2 = 50;
        double threshold = 100;

        Function<Double, Boolean> comparator;
        String string = null;
        comparator = switch (string) {
            case "Greater Than" -> (x) -> x > threshold;
            case "Less Than" -> (x) -> x < threshold;
            case "equal" -> (x) -> x == threshold;
            default -> throw new IllegalStateException("Unexpected value: " + string);
        };
        System.out.println(comparator.apply(value2));
    }

    public TheShop() {

    }



}
