package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.*;
import ore.forge.Enums.Operator;
import ore.forge.Player.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

//@author Nathan Ulmen
//TODO: Figure Out a way to incorporate Effects, mass?, Name/OreType
//An InfluencedUPG takes a BasicUPG and adjusts its modifier using 2 paramaters.
//	1. ValueOfInfluence - The Value that the adjustment is based on.
//	2. Operation - How the ValueOfInfluence is applied to the the BasicUPGs modifier.
//A maximum and minimum value can be set to ensure a modifier stays within a range.
public class InfluencedUPG implements UpgradeStrategy {
    public enum ValuesOfInfluence {
        VALUE, TEMPERATURE, MULTIORE, UPGRADE_COUNT,
        ACTIVE_ORE, PLACED_ITEMS, SPECIAL_POINTS, WALLET, PRESTIGE_LEVEL
    }

    protected static final Player player = Player.getSingleton();
    protected static final OreRealm oreRealm = OreRealm.getSingleton();
    protected static final ItemMap itemTracker = ItemMap.getSingleton();
    private final ValuesOfInfluence valueOfInfluence;
    private final BasicUpgrade upgrade;
    private final Operator operator;
    private final double minModifier, maxModifier, influenceScalar;

    public InfluencedUPG(ValuesOfInfluence valuesOfInfluence, BasicUpgrade upgrade, Operator operator) {
        this.valueOfInfluence = valuesOfInfluence;
        this.upgrade = upgrade;

        minModifier = -1000; //Default values for testing.
        maxModifier = 2000;
        influenceScalar = 1;
        this.operator = operator;

    }

    public InfluencedUPG(JsonValue jsonValue) {

        valueOfInfluence = ValuesOfInfluence.valueOf(jsonValue.getString("valueOfInfluence"));
        try {
            Class<?> aClass = Class.forName(jsonValue.get("baseUpgrade").getString("upgradeName"));
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            upgrade = (BasicUpgrade) constructor.newInstance(jsonValue.get("baseUpgrade"));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        operator = Operator.valueOf(jsonValue.getString("operation"));

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
        double finalModifier = influenceScalar * switch (valueOfInfluence) {
            case VALUE -> operator.apply(ore.getOreValue(), originalModifier);
            case TEMPERATURE -> operator.apply(ore.getOreTemp(), originalModifier);
            case MULTIORE -> operator.apply(ore.getMultiOre(), originalModifier);
            case UPGRADE_COUNT -> operator.apply(ore.getUpgradeCount(), originalModifier);
            case ACTIVE_ORE -> operator.apply(oreRealm.getActiveOre().size(), originalModifier);
            case PLACED_ITEMS -> operator.apply(itemTracker.getPlacedItems().size(), originalModifier);
            case SPECIAL_POINTS -> operator.apply(player.getSpecialPoints(), originalModifier);
            case WALLET -> operator.apply(player.getWallet(), originalModifier);
            case PRESTIGE_LEVEL -> operator.apply(player.getPrestigeLevel(), originalModifier);
        };

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

    @Override
    public String toString() {
        return "InfluencedUPG{" +
            "valueOfInfluence=" + valueOfInfluence +
            ", methodOfModification=" + upgrade +
            ", influenceOperator=" + operator +
            ", minimumModifier=" + minModifier +
            ", maxModifier=" + maxModifier +
            '}';
    }

}
