package ore.forge.Strategies.UpgradeStrategies;


import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;
import ore.forge.Expressions.BooleanCondition;
import ore.forge.Strategies.StrategyInitializer;

/**@author Nathan Ulmen
* TODO: Add support so that you can evaluate whether or not ore is under the influence of specific effects.
* A conditional Upgrade takes two upgrade Strategies, a trueBranchStrategy and a falseBranchStrategy, and a condition.
* The condition is evaluated and either the trueBranch or the falseBranch is activated based on the result.
*/
public class ConditionalUpgrade implements UpgradeStrategy , StrategyInitializer<UpgradeStrategy> {
    private final BooleanCondition booleanCondition;
    private final UpgradeStrategy trueBranchStrategy;
    private final UpgradeStrategy falseBranchStrategy;

    //Used for testing purposes.
    public ConditionalUpgrade(UpgradeStrategy trueBranch, UpgradeStrategy falseBranch, BooleanCondition booleanCondition) {
        this.booleanCondition = booleanCondition;
        trueBranchStrategy = trueBranch;
        falseBranchStrategy = falseBranch;
    }

    //used to create from JSON Data.
    public ConditionalUpgrade(JsonValue jsonValue) {
        trueBranchStrategy = createOrNull(jsonValue, "trueBranch", "upgradeName");
        falseBranchStrategy = createOrNull(jsonValue, "falseBranch", "upgradeName");
        booleanCondition = BooleanCondition.parseCondition(jsonValue.getString("condition"));
    }

    //Clone constructor
    private ConditionalUpgrade(ConditionalUpgrade conditionalUpgradeClone) {
        this.booleanCondition = conditionalUpgradeClone.booleanCondition;
        this.trueBranchStrategy = conditionalUpgradeClone.trueBranchStrategy.clone();
        this.falseBranchStrategy = conditionalUpgradeClone.falseBranchStrategy.clone();
    }

    @Override
    public void applyTo(Ore ore) {
        if (booleanCondition.evaluate(ore)) {//evaluate the condition.
            trueBranchStrategy.applyTo(ore);
        } else if (falseBranchStrategy != null) {
            falseBranchStrategy.applyTo(ore);
        }
    }

    @Override
    public UpgradeStrategy clone() {
        return new ConditionalUpgrade(this);
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "]" +
            " Condition:" + booleanCondition +
            "\n\tTrueBranch:" + trueBranchStrategy +  "}" +
            "\n\tFalseBranch:" + falseBranchStrategy + "}";
    }
}
