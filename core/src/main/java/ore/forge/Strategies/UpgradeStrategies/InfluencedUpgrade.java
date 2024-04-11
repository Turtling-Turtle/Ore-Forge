package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.*;
import ore.forge.Enums.KeyValue;
import ore.forge.Enums.Operator;
import ore.forge.Enums.OreProperty;
import ore.forge.Enums.ValueOfInfluence;
import ore.forge.Player.Player;
import ore.forge.Strategies.Function;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

//@author Nathan Ulmen
//TODO: Integrate Function to class.
//An InfluencedUPG takes a BasicUPG and adjusts its modifier using 2 paramaters.
//	1. ValueOfInfluence - The Value that the adjustment is based on.
//	2. Operation - How the ValueOfInfluence is applied to the the BasicUPGs modifier.

//A maximum and minimum value can be set to ensure a modifier stays within a range.
//An Influenced Upgrade takes a Basic upgrade and adjusts its modifier,
//The finalModifier is equal to/ found by this equation: finalModifier = scalar * (ValueOfInfluence [operator] baseModifier)
//You can also set min and maximum values for the finalModifier to ensure it isnt greater than or less than a specific value.
public class InfluencedUpgrade implements UpgradeStrategy {
    private final BasicUpgrade upgrade;
    private final Function upgradeFunction;
    private final double minModifier, maxModifier;

    //Used for testing.
    public InfluencedUpgrade(Function upgradeFunction, BasicUpgrade upgrade) {
        this.upgrade = upgrade;
        this.upgradeFunction = upgradeFunction;
        minModifier = -1000; //Default values for testing.
        maxModifier = 2000;
    }

    public InfluencedUpgrade(JsonValue jsonValue) {
        upgradeFunction = Function.parseFunction(jsonValue);
        KeyValue tempValueOfInfluence;
        try {
            tempValueOfInfluence = ValueOfInfluence.valueOf(jsonValue.getString("valueOfInfluence"));
        } catch (IllegalArgumentException e) {
            try {
                tempValueOfInfluence = OreProperty.valueOf(jsonValue.getString("valueOfInfluence"));
            } catch (IllegalArgumentException e2) {
                throw new RuntimeException("Invalid value of influence" + e2);
            }
        }

        try {
            Class<?> aClass = Class.forName(jsonValue.get("baseUpgrade").getString("upgradeName"));
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            upgrade = (BasicUpgrade) constructor.newInstance(jsonValue.get("baseUpgrade"));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

//        upgrade = new BasicUpgrade(jsonValue.get("baseUpgrade"));

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
    public String toString() {
        return "[" + this.getClass().getSimpleName()+ "]" +
            "\tOperator Type: " + upgrade.getOperator() +
            ", Value To Modify: " + upgrade.getValueToModify() +
            ", Upgrade Function: " + upgradeFunction +
            ", Minimum Modifier: " + minModifier +
            ", Max Modifier: " + maxModifier;

    }

}
