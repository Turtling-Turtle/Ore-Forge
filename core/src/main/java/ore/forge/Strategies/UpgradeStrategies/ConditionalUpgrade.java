package ore.forge.Strategies.UpgradeStrategies;


import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;
import ore.forge.Expressions.Condition;
import ore.forge.ReflectionLoader;

/**@author Nathan Ulmen
* TODO: Add support so that you can evaluate whether or not ore is under the influence of specific effects.
* A conditional Upgrade takes two upgrade Strategies, a trueBranchStrategy and a falseBranchStrategy, and a condition.
* The condition is evaluated and either the trueBranch or the falseBranch is activated based on the result.
*/
public class ConditionalUpgrade implements UpgradeStrategy {
    private final Condition condition;
    private final UpgradeStrategy trueBranchStrategy;
    private final UpgradeStrategy falseBranchStrategy;

    //Used for testing purposes.
    public ConditionalUpgrade(UpgradeStrategy trueBranch, UpgradeStrategy falseBranch, Condition condition) {
        this.condition = condition;
        trueBranchStrategy = trueBranch;
        falseBranchStrategy = falseBranch;
    }

    //used to create from JSON Data.
    public ConditionalUpgrade(JsonValue jsonValue) {
        trueBranchStrategy = ReflectionLoader.createOrNull(jsonValue, "trueBranch", "upgradeName");
        falseBranchStrategy = ReflectionLoader.createOrNull(jsonValue, "falseBranch", "upgradeName");
        condition = Condition.parseCondition(jsonValue.getString("condition"));
    }

    //Clone constructor
    private ConditionalUpgrade(ConditionalUpgrade conditionalUpgradeClone) {
        this.condition = conditionalUpgradeClone.condition;
        this.trueBranchStrategy = conditionalUpgradeClone.trueBranchStrategy.cloneUpgradeStrategy();
        this.falseBranchStrategy = conditionalUpgradeClone.falseBranchStrategy.cloneUpgradeStrategy();
    }

    @Override
    public void applyTo(Ore ore) {
        if (condition.evaluate(ore)) {//evaluate the condition.
            trueBranchStrategy.applyTo(ore);
        } else if (falseBranchStrategy != null) {
            falseBranchStrategy.applyTo(ore);
        }
    }

    @Override
    public UpgradeStrategy cloneUpgradeStrategy() {
        return new ConditionalUpgrade(this);
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "]" +
            " Condition:" + condition +
            "\n\tTrueBranch:" + trueBranchStrategy +  "}" +
            "\n\tFalseBranch:" + falseBranchStrategy + "}";
    }
}
