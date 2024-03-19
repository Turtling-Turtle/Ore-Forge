package ore.forge;


import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Strategies.OreStrategies.Invulnerability;
import ore.forge.Strategies.UpgradeStrategies.BasicUpgrade;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        Class<?> clasz = null;
        try {
            clasz = Class.forName("ore.forge.Strategies.UpgradeStrategies.PrimaryUPGS.MultiplyUPG");
            System.out.println(clasz.getConstructor(JsonValue.class));
            Constructor<?> constructor = clasz.getConstructor(double.class, BasicUpgrade.ValueToModify.class);
            Object o = constructor.newInstance(4, BasicUpgrade.ValueToModify.ORE_VALUE);
            System.out.println(o.toString());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }

    }

    public TheShop() {

    }

}
