package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Expressions.ComparisonOperator;
import ore.forge.Expressions.NumericOperator;
import ore.forge.Expressions.NumericOreProperties;
import ore.forge.Ore;
import ore.forge.Expressions.Condition;
import ore.forge.Expressions.Function;

//@author Nathan Ulmen
//An incremental upgrade increases and or decreases it modifier everytime a condition is met
// The "step" (how much the modifier changes) can be determined by a Function, or it can be a fixed amount.
// The modifier can be reset to its default/starting value once it reaches a specific threshold.

//Upgrade that increases its modifier by
public class IncrementalUpgrade implements UpgradeStrategy {
    private final double baseModifier;
    private final NumericOperator trueBranchOperator, falseBranchOperator;
    private final BasicUpgrade baseUpgrade;
    private final Condition triggerCondition;
    private final Function trueIncrement, falseIncrement, threshold;
//    private final ComparisonOperator thresholdComparator;

    public IncrementalUpgrade(double baseModifier, NumericOperator trueBranchOperator, NumericOperator falseBranchOperator, BasicUpgrade baseUpgrade, Condition triggerCondition, Function trueIncrement, Function falseIncrement, Function threshold, ComparisonOperator thresholdComparator) {
        this.baseModifier = baseModifier;
        this.trueBranchOperator = trueBranchOperator;
        this.falseBranchOperator = falseBranchOperator;
        this.baseUpgrade = baseUpgrade;
        this.triggerCondition = triggerCondition;
        this.trueIncrement = trueIncrement;
        this.falseIncrement = falseIncrement;
        this.threshold = threshold;
//        this.thresholdComparator = thresholdComparator;
    }

    public IncrementalUpgrade(JsonValue jsonValue) {
        baseModifier = jsonValue.getDouble("baseModifier");
        trueBranchOperator = NumericOperator.valueOf(jsonValue.getString("trueBranchOperator"));
        falseBranchOperator = NumericOperator.valueOf(jsonValue.getString("falseBranchOperator"));
//        this.thresholdComparator = thresholdComparator;

        NumericOperator operator = NumericOperator.valueOf(jsonValue.getString("numericOperator"));
        NumericOreProperties valueToModify = NumericOreProperties.valueOf(jsonValue.getString("valueToModify"));
        baseUpgrade = new BasicUpgrade(baseModifier, operator, valueToModify);

        Condition temp;
        try {
            temp = Condition.parseCondition(jsonValue.getString("triggerCondition"));
        } catch (IllegalArgumentException e) {
            temp = null;
        }
        triggerCondition = temp;

        trueIncrement = Function.parseFunction(jsonValue.getString("trueStep"));
        falseIncrement = Function.parseFunction(jsonValue.getString("falseStep"));
        threshold = Function.parseFunction(jsonValue.getString("threshold"));

    }

    private IncrementalUpgrade(IncrementalUpgrade upgradeToClone) {
        this.baseModifier = upgradeToClone.baseModifier;
        this.trueBranchOperator = upgradeToClone.trueBranchOperator;
        this.falseBranchOperator = upgradeToClone.falseBranchOperator;
        this.baseUpgrade = (BasicUpgrade) upgradeToClone.baseUpgrade.clone();
        this.triggerCondition = upgradeToClone.triggerCondition;
        this.trueIncrement = upgradeToClone.trueIncrement;
        this.falseIncrement = upgradeToClone.falseIncrement;
        this.threshold = upgradeToClone.threshold;

//        this.thresholdComparator = thresholdComparator;
    }

    @Override
    public void applyTo(Ore ore) {
        double newModifier;
        if (triggerCondition == null || triggerCondition.evaluate(ore)) { //Condition can be null/nonexistent
            newModifier = trueBranchOperator.apply(baseUpgrade.getModifier(), trueIncrement.calculate(ore));
        } else {
            newModifier = falseBranchOperator.apply(baseUpgrade.getModifier(), falseIncrement.calculate(ore));//Branch can be null/nonexistent
        }

        if (Math.abs(newModifier) > threshold.calculate(ore)) {
            newModifier = baseModifier;
        }

        baseUpgrade.setModifier(newModifier);
        baseUpgrade.applyTo(ore);

    }

    public UpgradeStrategy clone() {
        return new IncrementalUpgrade(this);
    }

    @Override
    public String toString() {
        return "[" + this.getClass().getSimpleName() + "]";
    }
}
