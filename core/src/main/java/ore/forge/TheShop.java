package ore.forge;


import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Strategies.OreStrategies.Invulnerability;
import ore.forge.Strategies.UpgradeStrategies.BasicUpgrade;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.DoubleBinaryOperator;

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
        double value1 = 100;
        double value2 = 50;
        DoubleBinaryOperator operator = null;
        String methodOfModification = "multiplication";
        operator = switch (methodOfModification) {
            case "addition" -> (a, b) -> a + b;
            case "multiplication" -> (a, b) -> a * b;
            case "subtraction" -> (a, b) -> a - b;
            case "division" -> (a,b) -> a / b;
            default -> operator;
        };
        System.out.println(operator.applyAsDouble(value1, value2));
    }

    public TheShop() {

    }



}
