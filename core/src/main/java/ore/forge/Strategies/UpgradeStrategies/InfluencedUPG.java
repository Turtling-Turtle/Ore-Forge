package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.ItemTracker;
import ore.forge.Ore;
import ore.forge.OreRealm;
import ore.forge.Player.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.DoubleBinaryOperator;

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
    private final ValuesOfInfluence valueOfInfluence;
    private final BasicUpgrade methodOfModification;
    private DoubleBinaryOperator influenceOperator;
    private double minimumModifier, maxModifier, influenceScalar;

    public InfluencedUPG(ValuesOfInfluence valueOfInfluence, BasicUpgrade methodOfModification) {
        this.valueOfInfluence = valueOfInfluence;
        this.methodOfModification = methodOfModification;
    }

    public InfluencedUPG(ValuesOfInfluence valuesOfInfluence, BasicUpgrade upgrade, BasicUpgrade.Operation operation) {
        this.valueOfInfluence = valuesOfInfluence;
        this.methodOfModification = upgrade;
        influenceOperator = switch (operation) {
            case ADD -> (x,y) -> x + y;
            case SUBTRACT -> (x,y) -> x -y;
            case MULTIPLY -> (x,y) -> x * y;
            case DIVIDE -> (x,y) -> x / y;
            case MODULO -> (x, y) -> x % y;
        };

    }

    public InfluencedUPG(JsonValue jsonValue) {
        valueOfInfluence = ValuesOfInfluence.valueOf(jsonValue.getString("valueOfInfluence"));
        try {
            Class<?> aClass = Class.forName(jsonValue.get("baseUpgrade").getString("upgradeName"));
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            methodOfModification = (BasicUpgrade) constructor.newInstance(jsonValue.get("baseUpgrade"));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void applyTo(Ore ore) {
        double finalModifier = switch (valueOfInfluence) {
            case VALUE -> ore.getOreValue() * methodOfModification.getModifier();
            case TEMPERATURE -> ore.getOreTemp() * methodOfModification.getModifier();
            case MULTIORE -> ore.getMultiOre() * methodOfModification.getModifier();
            case UPGRADE_COUNT -> ore.getUpgradeCount() * methodOfModification.getModifier();
            case ACTIVE_ORE -> oreRealm.activeOre.size() * methodOfModification.getModifier();
            case PLACED_ITEMS -> ItemTracker.getSingleton().getPlacedItems().size() * methodOfModification.getModifier();
            case PRESTIGE_LEVEL -> player.getPrestigeLevel() * methodOfModification.getModifier();
            case SPECIAL_POINTS -> player.getSpecialPoints() * methodOfModification.getModifier();
            case WALLET -> player.getWallet() * methodOfModification.getModifier();
        };
        double original = methodOfModification.getModifier();
        methodOfModification.setModifier(finalModifier);
        methodOfModification.applyTo(ore);
        methodOfModification.setModifier(original);
    }

    public void apply(Ore ore) {
        //finalModifier = (modifier, influence) -> operation
        //where operation is the multiply, add, subtract, divide.
        double originalModifier = methodOfModification.getModifier();
        double finalModifier = switch (valueOfInfluence) {
            case VALUE -> influenceOperator.applyAsDouble(originalModifier, ore.getOreValue());
            case TEMPERATURE -> influenceOperator.applyAsDouble(originalModifier, ore.getOreTemp());
            case MULTIORE -> influenceOperator.applyAsDouble(originalModifier, ore.getMultiOre());
            case UPGRADE_COUNT -> influenceOperator.applyAsDouble(originalModifier, ore.getUpgradeCount());
            case ACTIVE_ORE -> influenceOperator.applyAsDouble(originalModifier, oreRealm.activeOre.size());
            case PLACED_ITEMS -> influenceOperator.applyAsDouble(originalModifier, ItemTracker.getSingleton().getPlacedItems().size());
            case SPECIAL_POINTS -> influenceOperator.applyAsDouble(originalModifier, player.getSpecialPoints());
            case WALLET -> influenceOperator.applyAsDouble(originalModifier, player.getWallet());
            case PRESTIGE_LEVEL -> influenceOperator.applyAsDouble(originalModifier, player.getPrestigeLevel());
        };
//        finalModifier = Math.max(minimumModifier, Math.min(finalModifier, maxModifier));
        if (finalModifier > maxModifier) {
            methodOfModification.setModifier(maxModifier);
        } else if (finalModifier < minimumModifier) {
            methodOfModification.setModifier(minimumModifier);
        } else {
            methodOfModification.setModifier(finalModifier);
        }

//        methodOfModification.setModifier(finalModifier);
        methodOfModification.applyTo(ore);
        methodOfModification.setModifier(originalModifier);
    }

}
