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

import static ore.forge.Enums.OreProperty.*;
import static ore.forge.Enums.ValueOfInfluence.*;

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
    protected static final Player player = Player.getSingleton();
    protected static final OreRealm oreRealm = OreRealm.getSingleton();
    protected static final ItemMap itemTracker = ItemMap.getSingleton();
    private final KeyValue valueOfInfluence;
    private final BasicUpgrade upgrade;
    private final Operator operator;
    private Function upgradeFunction;
    private final double minModifier, maxModifier, influenceScalar;

    public InfluencedUpgrade(KeyValue valuesOfInfluence, BasicUpgrade upgrade, Operator operator) {
        this.valueOfInfluence = valuesOfInfluence;
        this.upgrade = upgrade;

        minModifier = -1000; //Default values for testing.
        maxModifier = 2000;
        influenceScalar = 1;
        this.operator = operator;

    }

    public InfluencedUpgrade(JsonValue jsonValue) {
//        upgradeFunction = Function.parseFunction(jsonValue);
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
        valueOfInfluence = tempValueOfInfluence;

        try {
            Class<?> aClass = Class.forName(jsonValue.get("baseUpgrade").getString("upgradeName"));
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            upgrade = (BasicUpgrade) constructor.newInstance(jsonValue.get("baseUpgrade"));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        operator = Operator.valueOf(jsonValue.getString("operation"));

        upgradeFunction = Function.parseFunction(jsonValue.getString("upgradeFunction"));

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

        try {
            temp = jsonValue.getDouble("scalar");
        } catch (IllegalArgumentException e) {
            temp = 1;
        }
        influenceScalar = temp;
    }

    @Override
    public void applyTo(Ore ore) {
        // finalModifier = scalar * (valueOfInfluence [operator] baseModifier)
        double originalModifier = upgrade.getModifier();
        double finalModifier = calculateFinalModifier(ore, originalModifier);
        //double finalModifier = upgradeFunction.calculate(ore);


        if (finalModifier > maxModifier) {
            upgrade.setModifier(maxModifier);
        } else if (finalModifier < minModifier) {
            upgrade.setModifier(minModifier);
        } else {
            upgrade.setModifier(finalModifier);
        }

        upgrade.applyTo(ore);
        upgrade.setModifier(originalModifier);
    }

    private double calculateFinalModifier(Ore ore, double originalModifier) {
        return influenceScalar * switch (valueOfInfluence) {
            case ORE_VALUE-> operator.apply(ore.getOreValue(), originalModifier);
            case TEMPERATURE -> operator.apply(ore.getOreTemp(), originalModifier);
            case MULTIORE -> operator.apply(ore.getMultiOre(), originalModifier);
            case UPGRADE_COUNT -> operator.apply(ore.getUpgradeCount(), originalModifier);
            case ACTIVE_ORE -> operator.apply(oreRealm.getActiveOre().size(), originalModifier);
            case PLACED_ITEMS -> operator.apply(itemTracker.getPlacedItems().size(), originalModifier);
            case SPECIAL_POINTS -> operator.apply(player.getSpecialPoints(), originalModifier);
            case WALLET -> operator.apply(player.getWallet(), originalModifier);
            case PRESTIGE_LEVEL -> operator.apply(player.getPrestigeLevel(), originalModifier);
            default -> throw new IllegalStateException("Unexpected value: " + valueOfInfluence);
        };
    }

    @Override
    public String toString() {
        return "InfluencedUPG{" +
            "valueOfInfluence=" + valueOfInfluence +
            ", \nmethodOfModification=" + upgrade +
            ", influenceOperator=" + operator +
            ", minimumModifier=" + minModifier +
            ", maxModifier=" + maxModifier +
            '}';
    }

}
