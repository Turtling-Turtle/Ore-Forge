package ore.forge.Strategies.UpgradeStrategies;


import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Enums.BooleanOperator;
import ore.forge.Ore;
import ore.forge.Strategies.StrategyInitializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

//@author Nathan Ulmen
//TODO: Add support so that you can evaluate whether or not ore is under the influence of specific effects.
//A conditional upgrade will execute/apply one of two different upgrade strategies depending on the result of the condition it evaluates.
public class ConditionalUpgrade implements UpgradeStrategy , StrategyInitializer<UpgradeStrategy> {
    public enum Condition {VALUE, UPGRADE_COUNT, TEMPERATURE, MULTIORE} //Condition to be evaluated
//    private final Enum<?> condition;
    private final Condition condition;
    private final UpgradeStrategy ifModifier;
    private final UpgradeStrategy elseModifier;
    private final double threshold;
    private final BooleanOperator comparator;
    private final Function<Ore, Number> propertyRetriever;
    private final Function<Ore, Boolean> evaluator;
    //private Ore oreToBeUpgraded;

    //Used for testing purposes.
    public ConditionalUpgrade(UpgradeStrategy ifMod, UpgradeStrategy elseMod, Condition condition, double threshold, BooleanOperator comparison) {
        ifModifier = ifMod;
        elseModifier = elseMod;
        this.threshold = threshold;
        this.condition = condition;
        this.comparator = comparison;
        propertyRetriever = configurePropertyRetriever(condition);

        evaluator = (Ore ore) -> comparator.evaluate((Double) propertyRetriever.apply(ore), this.threshold);
    }

    //used to create from JSON Data.
    public ConditionalUpgrade(JsonValue jsonValue) {
        ifModifier = createOrNull(jsonValue, "ifModifier", "upgradeName");
        elseModifier = createOrNull(jsonValue, "elseModifier", "upgradeName");
        this.threshold = jsonValue.getDouble("threshold");
        this.condition = Condition.valueOf(jsonValue.getString("condition"));
        this.comparator = BooleanOperator.valueOf(jsonValue.getString("comparison"));
        propertyRetriever = configurePropertyRetriever(Condition.valueOf(jsonValue.getString("condition")));

        evaluator = (Ore ore) -> comparator.evaluate((Double) propertyRetriever.apply(ore), this.threshold);
    }

    @Override
    public void applyTo(Ore ore) {
        //this.oreToBeUpgraded = ore;
        if (evaluator.apply(ore)) {
            ifModifier.applyTo(ore);
        } else if (elseModifier != null) {
            elseModifier.applyTo(ore);
        }

    }

    private Function<Ore, Number> configurePropertyRetriever(Condition condition) {
        return switch (condition) {
            case VALUE -> (Ore::getOreValue);
            case UPGRADE_COUNT -> (Ore::getUpgradeCount);
            case TEMPERATURE -> (Ore::getOreTemp);
            case MULTIORE -> (Ore::getMultiOre);
        };
    }

    @Override
    public String toString() {
        return "ConditionalUPG{" +
            "condition=" + condition +
            ", threshold=" + threshold +
            ", comparator=" + comparator +
            ", \n{ifModifier=" + ifModifier +  "}" +
            ", \n{elseModifier=" + elseModifier + "}" +
            '}';
    }
}
