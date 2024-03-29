package ore.forge.Strategies.UpgradeStrategies;


import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Enums.BooleanOperator;
import ore.forge.FunctionalInterfaces.BinomialFunction;
import ore.forge.Ore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

//@author Nathan Ulmen
//TODO: Add support so that you can evaluate whether or not ore is under the influence of specific effects.
public class ConditionalUPG implements UpgradeStrategy {
    public enum Condition {VALUE, UPGRADE_COUNT, TEMPERATURE, MULTIORE} //Condition to be evaluated
    private final Condition condition;
    private final UpgradeStrategy ifModifier;
    private final UpgradeStrategy elseModifier;
    private final double threshold;
    private final BooleanOperator comparator;
    private final Function<Ore, Number> propertyRetriever;
    private final BinomialFunction<Ore, Function<Ore, Number>, Boolean> evaluator;

    public ConditionalUPG(UpgradeStrategy ifMod, UpgradeStrategy elseMod, Condition condition, double threshold, BooleanOperator comparison) {
        ifModifier = ifMod;
        elseModifier = elseMod;
        this.threshold = threshold;
        this.condition = condition;
        this.comparator = comparison;

        propertyRetriever = switch (condition) {
            case VALUE -> (Ore::getOreValue);
            case UPGRADE_COUNT -> (Ore::getUpgradeCount);
            case TEMPERATURE -> (Ore::getOreTemp);
            case MULTIORE -> (Ore::getMultiOre);
        };

        evaluator = (ore, propertyRetriever) -> comparator.evaluate((Double) propertyRetriever.apply(ore), this.threshold);
    }

    public ConditionalUPG(JsonValue jsonValue) {
        ifModifier = createOrNull(jsonValue, "ifModifier");
        elseModifier = createOrNull(jsonValue, "elseModifier");
        this.threshold = jsonValue.getDouble("threshold");
        this.condition = Condition.valueOf(jsonValue.getString("condition"));
        this.comparator = BooleanOperator.valueOf(jsonValue.getString("comparison"));

        propertyRetriever = switch (Condition.valueOf(jsonValue.getString("condition"))) {
            case VALUE -> (Ore::getOreValue);
            case UPGRADE_COUNT -> (Ore::getUpgradeCount);
            case TEMPERATURE -> (Ore::getOreTemp);
            case MULTIORE -> (Ore::getMultiOre);
        };

        evaluator = (ore, propertyRetriever) -> comparator.evaluate((Double) propertyRetriever.apply(ore), this.threshold);
    }

    @Override
    public void applyTo(Ore ore) {
        if (evaluator.apply(ore, propertyRetriever)) {
            ifModifier.applyTo(ore);
        } else if (elseModifier != null) {
            elseModifier.applyTo(ore);
        }

    }


    private UpgradeStrategy createOrNull(JsonValue jsonValue, String field) {
        if (jsonValue.get(field) == null) {return null;}

        try {
            Class<?> aClass = Class.forName(jsonValue.get(field).getString("upgradeName"));
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            return (UpgradeStrategy) constructor.newInstance(jsonValue.get(field));
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return getClass().getSimpleName() + "\tCondition: " + condition + "\tComparison: " + comparator +
            "\tThreshold: " + threshold +
            "\n\nifModifier: " + ifModifier.toString() + "\n\nelseModifier: " + elseModifier.toString();
    }


}
