package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.*;
import ore.forge.Strategies.Function;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**@author Nathan Ulmen
An Influenced Upgrade dynamically changes/adapts the value of its Modifier based on the returned result of its Function.
A maximum and minimum value can be set to ensure the modifier stays within a specified range.*/
public class InfluencedUpgrade implements UpgradeStrategy {
    private final BasicUpgrade upgrade;
    private final Function upgradeFunction;
    private final double minModifier, maxModifier;

    public InfluencedUpgrade(Function upgradeFunction, BasicUpgrade upgrade, double minModifier, double maxModifier) {
        this.upgrade = upgrade;
        this.upgradeFunction = upgradeFunction;
        this.minModifier = minModifier;
        this.maxModifier = maxModifier;
    }

    public InfluencedUpgrade(JsonValue jsonValue) {
        upgradeFunction = Function.parseFunction(jsonValue.getString("upgradeFunction"));
        try {
            Class<?> aClass = Class.forName(jsonValue.get("baseUpgrade").getString("upgradeName"));
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            upgrade = (BasicUpgrade) constructor.newInstance(jsonValue.get("baseUpgrade"));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //If field doesn't exist that means we need to set it to the "default" .
        double temp;
        try {
            temp = jsonValue.getDouble("minModifier");
        } catch (IllegalArgumentException e) {
            temp = Double.MIN_VALUE;
        }
        minModifier = temp;
        try {
            temp = jsonValue.getDouble("maxModifier");
        } catch (IllegalArgumentException e) {
            temp = Double.MAX_VALUE;
        }
        maxModifier = temp;
    }

    private InfluencedUpgrade(InfluencedUpgrade influencedUpgradeClone) {
        this.upgrade = influencedUpgradeClone.upgrade;//Don't need to clone
        this.upgradeFunction = influencedUpgradeClone.upgradeFunction;
        this.minModifier = influencedUpgradeClone.minModifier;
        this.maxModifier = influencedUpgradeClone.maxModifier;
    }

    @Override
    public void applyTo(Ore ore) {
        double originalModifier = upgrade.getModifier();
        double newModifier = upgradeFunction.calculate(ore);

        if (newModifier > maxModifier) {
            upgrade.setModifier(maxModifier);
        } else if (newModifier < minModifier) {
            upgrade.setModifier(minModifier);
        } else {
            upgrade.setModifier(newModifier);
        }

        upgrade.applyTo(ore);
        upgrade.setModifier(originalModifier);
    }

    @Override
    public UpgradeStrategy clone() {
        return this;
    }

    @Override
    public String toString() {
        return "[" + this.getClass().getSimpleName()+ "]" +
            "\tOperator Type: " + upgrade.getOperator() +
            ", Value To Modify: " + upgrade.getValueToModify() +
            ", Upgrade Function: " + upgradeFunction +
            ", Minimum Modifier: " + minModifier +
            ", Max Modifier: " + maxModifier;
    }

}
