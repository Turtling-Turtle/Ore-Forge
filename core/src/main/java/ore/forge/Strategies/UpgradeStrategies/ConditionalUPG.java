package ore.forge.Strategies.UpgradeStrategies;


import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

//@author Nathan Ulmen
//TODO: Add support so that you can evaluate whether or not ore is under the influence of specific effects.
public class ConditionalUPG implements UpgradeStrategy {
    public enum Condition {VALUE, UPGRADE_COUNT, TEMPERATURE, MULTIORE} //Condition to be evaluated
    public enum Comparison {GREATER_THAN, LESS_THAN, EQUAL_TO} //Type of comparison
    private final Condition condition;
    private final Comparison comparison;
    private final UpgradeStrategy ifModifier;
    private final UpgradeStrategy elseModifier;
    private final double threshold;
    private final Function<Double, Boolean> comparator;
    private final Function<Ore, ?> propertyRetriever;


    public ConditionalUPG(UpgradeStrategy ifMod, UpgradeStrategy elseMod, Condition condition, double threshold, Comparison comparison) {
        ifModifier = ifMod;
        elseModifier = elseMod;
        this.threshold = threshold;
        this.condition = condition;
        this.comparison = comparison;
        comparator = switch (comparison) {
            case GREATER_THAN -> (x) -> x > threshold;
            case LESS_THAN -> (x) -> x < threshold;
            case EQUAL_TO -> (x) -> x == threshold;
        };
        propertyRetriever = switch (condition) {
            case VALUE -> (Ore::getOreValue);
            case UPGRADE_COUNT -> (Ore::getUpgradeCount);
            case TEMPERATURE -> (Ore::getOreTemp);
            case MULTIORE -> (Ore::getMultiOre);
        };
    }

    public ConditionalUPG(JsonValue jsonValue) {
        ifModifier = createOrNull(jsonValue, "ifModifier");
        elseModifier = createOrNull(jsonValue, "elseModifier");
        this.threshold = jsonValue.getDouble("threshold");
        this.condition = Condition.valueOf(jsonValue.getString("condition"));
        this.comparison = Comparison.valueOf(jsonValue.getString("comparison"));
        comparator = switch (Comparison.valueOf(jsonValue.getString("comparison"))) {
            case GREATER_THAN -> (x) -> x > threshold;
            case LESS_THAN -> (x) -> x < threshold;
            case EQUAL_TO -> (x) -> x == threshold;
        };

        propertyRetriever = switch (Condition.valueOf(jsonValue.getString("condition"))) {
            case VALUE -> (Ore::getOreValue);
            case UPGRADE_COUNT -> (Ore::getUpgradeCount);
            case TEMPERATURE -> (Ore::getOreTemp);
            case MULTIORE -> (Ore::getMultiOre);
        };

    }

    private UpgradeStrategy createOrNull(JsonValue jsonValue, String field) {
        try {
            jsonValue.get(field);
        } catch (NullPointerException e) {
            return null;
        }

        try {
            Class<?> aClass = Class.forName(jsonValue.get(field).getString("upgradeName"));
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            return (UpgradeStrategy) constructor.newInstance(jsonValue.get(field));
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void applyTo(Ore ore) {
        if (comparator.apply((Double) propertyRetriever.apply(ore))) {
            ifModifier.applyTo(ore);
        } else if (elseModifier != null){
            elseModifier.applyTo(ore);
        }
    }

    public String toString() {
        return getClass().getSimpleName() + "\tCondition: " + condition + "\tComparison: " + comparison +
            "\tThreshold: " + threshold +
            "\n\nifModifier: " + ifModifier.toString() + "\n\nelseModifier: " + elseModifier.toString();
    }


}
