package ore.forge.Strategies.UpgradeStrategies;


import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Enums.ComparisonOperator;
import ore.forge.Enums.KeyValue;
import ore.forge.Enums.OreProperty;
import ore.forge.Enums.ValueOfInfluence;
import ore.forge.Ore;
import ore.forge.Strategies.StrategyInitializer;
import ore.forge.Strategies.Function;

/**@author Nathan Ulmen
TODO: Add support so that you can evaluate whether or not ore is under the influence of specific effects.
A conditional Upgrade takes two upgrade Strategies, a trueBranchStrategy and a falseBranchStrategy, a condition, threshold, and a comparison operator.
The condition is compared to the threshold using the comparison operator and depending on the result either the true or false branch is activated.
The condition is a KeyValue which means it can be a property of the ore being upgraded or a more universal value like active ore or player level.
The threshold can either be a predetermined fixed Value or a KeyValue.*/
public class ConditionalUpgrade implements UpgradeStrategy , StrategyInitializer<UpgradeStrategy> {
    private final KeyValue condition; //Key Value includes OreProperties and ValueOfInfluence
    private Function conditionFunction;
    private final UpgradeStrategy trueBranchStrategy;
    private final UpgradeStrategy falseBranchStrategy;
    private final double fixedThreshold;
    private final KeyValue dynamicThreshold;
    private final ComparisonOperator comparator;
    private final java.util.function.Function<Ore, Number> conditionSupplier, thresholdSupplier;
    private final java.util.function.Function<Ore, Boolean> evaluator;

    //Used for testing purposes.
    public ConditionalUpgrade(UpgradeStrategy trueBranch, UpgradeStrategy falseBranch, KeyValue condition, double fixedThreshold, KeyValue dynamicThreshold, ComparisonOperator comparison) {
        trueBranchStrategy = trueBranch;
        falseBranchStrategy = falseBranch;
        this.fixedThreshold = fixedThreshold;
        this.dynamicThreshold = dynamicThreshold;
        this.condition = condition;
        this.comparator = comparison;
        conditionSupplier = configureSupplier(condition);

        if (dynamicThreshold != null) {
            thresholdSupplier = configureSupplier(dynamicThreshold);
            evaluator = (Ore ore) -> comparator.evaluate((Double) conditionSupplier.apply(ore), (Double) thresholdSupplier.apply(ore));
        } else {
            thresholdSupplier = null;
            evaluator = (Ore ore) -> comparator.evaluate((Double) conditionSupplier.apply(ore), this.fixedThreshold);
        }

    }

    //used to create from JSON Data.
    public ConditionalUpgrade(JsonValue jsonValue) {
        trueBranchStrategy = createOrNull(jsonValue, "ifModifier", "upgradeName");
        falseBranchStrategy = createOrNull(jsonValue, "elseModifier", "upgradeName");
        this.condition = configureKeyValue(jsonValue, "condition");
        this.comparator = ComparisonOperator.valueOf(jsonValue.getString("comparison"));
        conditionSupplier = configureSupplier(condition);

        if (jsonValue.get("threshold").isNumber()) {
            fixedThreshold = jsonValue.getDouble("threshold");
            dynamicThreshold = null;
            thresholdSupplier = null;
            evaluator = (Ore ore) -> comparator.evaluate((Double) conditionSupplier.apply(ore), fixedThreshold);
        } else {
            dynamicThreshold = configureKeyValue(jsonValue, "threshold");
            thresholdSupplier = configureSupplier(dynamicThreshold);
            fixedThreshold = 1;
            evaluator = (Ore ore) -> comparator.evaluate((Double) conditionSupplier.apply(ore), (Double) thresholdSupplier.apply(ore));
        }

    }

    //Clone constructor
    private ConditionalUpgrade(ConditionalUpgrade conditionalUpgradeClone) {
        this.condition = conditionalUpgradeClone.condition;
        this.trueBranchStrategy = conditionalUpgradeClone.trueBranchStrategy.clone();
        this.falseBranchStrategy = conditionalUpgradeClone.falseBranchStrategy.clone();
        this.fixedThreshold = conditionalUpgradeClone.fixedThreshold;
        this.dynamicThreshold = conditionalUpgradeClone.dynamicThreshold;
        this.comparator = conditionalUpgradeClone.comparator;
        this.conditionFunction = conditionalUpgradeClone.conditionFunction;
        this.evaluator = conditionalUpgradeClone.evaluator;
        this.conditionSupplier = conditionalUpgradeClone.conditionSupplier;
        this.thresholdSupplier = conditionalUpgradeClone.thresholdSupplier;
    }

    @Override
    public void applyTo(Ore ore) {
        if (evaluator.apply(ore)) {//evaluator function is applied to ore
            trueBranchStrategy.applyTo(ore);
        } else if (falseBranchStrategy != null) {
            falseBranchStrategy.applyTo(ore);
        }
    }

    @Override
    public UpgradeStrategy clone() {
        return new ConditionalUpgrade(this);
    }

    //Configures the behavior of a supplier function so that it returns the correct value.
    private java.util.function.Function<Ore, Number> configureSupplier(KeyValue condition) {
        if (condition instanceof OreProperty) {
            return switch ((OreProperty)condition) {
                case ORE_VALUE -> (Ore::getOreValue);
                case SPEED -> (Ore::getMoveSpeed);
                case UPGRADE_COUNT -> (Ore::getUpgradeCount);
                case TEMPERATURE -> (Ore::getOreTemp);
                case MULTIORE -> (Ore::getMultiOre);
            };
        } else if (condition instanceof ValueOfInfluence) {
            return (Ore ore) -> ((ValueOfInfluence) condition).getAssociatedValue();
        }
        throw new RuntimeException(condition + "is not a valid value of Influence");
    }

    //Determines which enum that the KeyValue is from JSON.
    private KeyValue configureKeyValue(JsonValue jsonValue, String fieldToGet) {
        KeyValue tempCondition;
        try {
            tempCondition = OreProperty.valueOf(jsonValue.getString(fieldToGet));
        } catch (IllegalArgumentException e) {
            try {
                tempCondition = ValueOfInfluence.valueOf(jsonValue.getString(fieldToGet));
            }catch (IllegalArgumentException e2) {
                throw new RuntimeException("Invalid Condition" + e2);
            }
        }
        return tempCondition;
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "]" +
            " Condition:" + condition +
            ", Threshold:" + fixedThreshold +
            ", Comparator:" + comparator +
            "\n\tTrueBranch:" + trueBranchStrategy +  "}" +
            "\n\tFalseBranch:" + falseBranchStrategy + "}";
    }
}
