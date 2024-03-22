package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.*;
import ore.forge.Player.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

//@author Nathan Ulmen
//TODO: Figure Out a way to incorporate Effects, mass?, Name/OreType
//An InfluencedUPG takes a BasicUPG and adjusts its modifier using 2 paramaters.
//	1. ValueOfInfluence - The Value that the adjustment is based on.
//	2. Operation - How the ValueOfInfluence is applied to the the BasicUPGs modifier.
//A maximum and minimum value can be set to ensure a modifier stays within a range.
public class InfluencedUPG implements UpgradeStrategy {
    public enum ValuesOfInfluence {VALUE, TEMPERATURE, MULTIORE, UPGRADE_COUNT,
        ACTIVE_ORE, PLACED_ITEMS, SPECIAL_POINTS, WALLET, PRESTIGE_LEVEL}
    protected static final Player player = Player.getSingleton();
    protected static final OreRealm oreRealm = OreRealm.getSingleton();
    protected static final ItemTracker itemTracker = ItemTracker.getSingleton();
    private final ValuesOfInfluence valueOfInfluence;
    private final BasicUpgrade upgrade;
    private final DoubleBinaryOperator influenceOperator;
    private Function<Double, Number> operation;
    private BinomialFunction<ValuesOfInfluence, Function<Double, Number>, Number> function;
    private double minimumModifier, maxModifier, influenceScalar;

    public InfluencedUPG(ValuesOfInfluence valuesOfInfluence, BasicUpgrade upgrade, BasicUpgrade.Operator operator) {
        this.valueOfInfluence = valuesOfInfluence;
        this.upgrade = upgrade;
        influenceOperator = switch (operator) {
            case ADD -> (x,y) -> x + y;
            case SUBTRACT -> (x,y) -> x - y;
            case MULTIPLY -> (x,y) -> x * y;
            case DIVIDE -> (x,y) -> x / y;
            case MODULO -> (x, y) -> x % y;
        };
        double baseModifier = upgrade.getModifier();
        operation= switch (operator) {
            case ADD -> (x) -> x + baseModifier;
            case SUBTRACT -> (x) -> x - baseModifier;
            case MULTIPLY -> (x) -> x * baseModifier;
            case DIVIDE -> (x) -> x / baseModifier;
            case MODULO -> (x) -> x % baseModifier;
        };

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

        influenceOperator = switch (BasicUpgrade.Operator.valueOf(jsonValue.getString("operation"))) {
            case ADD -> (x,y) -> x + y;
            case SUBTRACT -> (x,y) -> x - y;
            case MULTIPLY -> (x,y) -> x * y;
            case DIVIDE -> (x,y) -> x / y;
            case MODULO -> (x, y) -> x % y;
        };

        //If field doesn't exist that means we need to set it to the defualt .
        try {
            minimumModifier = jsonValue.getDouble("minModifier");
        } catch (IllegalArgumentException e ) {
            minimumModifier = Double.MIN_VALUE;
        }
        try {
            maxModifier = jsonValue.getDouble("maxModifier");
        } catch (IllegalArgumentException e) {
            maxModifier = Double.MIN_VALUE;
        }
        try {
            influenceScalar = jsonValue.getDouble("scalar");
        } catch (IllegalArgumentException e) {
            influenceScalar = 1;
        }
    }

    @Override
    public void applyTo(Ore ore) {
        // finalModifier = scalar * (valueOfInfluence [operator] baseModifier)
        double originalModifier = upgrade.getModifier();
        double finalModifier = influenceScalar * switch (valueOfInfluence) {
            case VALUE -> influenceOperator.applyAsDouble(originalModifier, ore.getOreValue());
            case TEMPERATURE -> influenceOperator.applyAsDouble(originalModifier, ore.getOreTemp());
            case MULTIORE -> influenceOperator.applyAsDouble(originalModifier, ore.getMultiOre());
            case UPGRADE_COUNT -> influenceOperator.applyAsDouble(originalModifier, ore.getUpgradeCount());
            case ACTIVE_ORE -> influenceOperator.applyAsDouble(originalModifier, oreRealm.activeOre.size());
            case PLACED_ITEMS -> influenceOperator.applyAsDouble(originalModifier, itemTracker.getPlacedItems().size());
            case SPECIAL_POINTS -> influenceOperator.applyAsDouble(originalModifier, player.getSpecialPoints());
            case WALLET -> influenceOperator.applyAsDouble(originalModifier, player.getWallet());
            case PRESTIGE_LEVEL -> influenceOperator.applyAsDouble(originalModifier, player.getPrestigeLevel());
        };
        if (finalModifier > maxModifier) {
            upgrade.setModifier(maxModifier);
        } else if (finalModifier < minimumModifier) {
            upgrade.setModifier(minimumModifier);
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
            ", influenceOperator=" + influenceOperator +
            ", minimumModifier=" + minimumModifier +
            ", maxModifier=" + maxModifier +
            '}';
    }

}
