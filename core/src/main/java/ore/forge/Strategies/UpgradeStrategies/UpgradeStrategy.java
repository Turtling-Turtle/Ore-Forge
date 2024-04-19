package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

//interface for upgrade strategies.
public interface UpgradeStrategy {

    void applyTo(Ore ore);

    //Used to ensure that upgrades remain unique to the upgrade item it's attached to.
    UpgradeStrategy clone();

}
