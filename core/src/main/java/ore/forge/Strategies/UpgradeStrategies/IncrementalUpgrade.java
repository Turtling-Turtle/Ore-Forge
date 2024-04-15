package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;
import ore.forge.Strategies.Function;

//@author Nathan Ulmen
//An incremental upgrade increases and or decreases it modifier everytime a condition is met
// The "step" (how much the modifier changes) can be determined by a Function, or it can be a fixed amount.
// The modifier can be reset to its default/starting value once it reaches a specific threshold.
public class IncrementalUpgrade implements UpgradeStrategy {
    private double initialModifier;
    private Function stepCondition, thresholdCondition, trueBranchStep, falseBranchStep;
    private BasicUpgrade upgrade;

    public IncrementalUpgrade() {

    }

    public IncrementalUpgrade(JsonValue jsonValue) {

    }

    private IncrementalUpgrade(IncrementalUpgrade upgradeToClone) {
        this.initialModifier = upgradeToClone.initialModifier;
        this.stepCondition = upgradeToClone.stepCondition;
        this.thresholdCondition = upgradeToClone.thresholdCondition;
        this.trueBranchStep = upgradeToClone.trueBranchStep;
        this.falseBranchStep = upgradeToClone.falseBranchStep;
        this.upgrade = (BasicUpgrade) upgradeToClone.upgrade.clone();//Clone the basic upgrade.
        this.upgrade.setModifier(initialModifier);
    }

    @Override
    public void applyTo(Ore ore) {
        double newModifier;
//        if (stepCondition is True) {
//              newModifier = trueBranchStep;
//        } else {
//              newModifier = falseBranchStep;
//        }

        /*
        if(newModifier thresholdCondition){
            newModifier = initialModifier
        }

        upgrade.setModifier(newModifier);
        upgrade.applyTo(ore);

        */
    }

    public UpgradeStrategy clone() {
        return new IncrementalUpgrade(this);
    }

    @Override
    public String toString() {
        return "[" + this.getClass().getSimpleName() + "]" +
            "\t initialModifier=" + initialModifier +
            ", stepCondition=" + stepCondition +
            ", thresholdCondition=" + thresholdCondition +
            ", trueBranchStep=" + trueBranchStep +
            ", falseBranchStep=" + falseBranchStep +
            ", upgrade=" + upgrade;
    }
}
