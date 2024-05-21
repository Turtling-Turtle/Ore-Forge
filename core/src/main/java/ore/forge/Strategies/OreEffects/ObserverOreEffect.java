package ore.forge.Strategies.OreEffects;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Expressions.ValueOfInfluence;
import ore.forge.Ore;
import ore.forge.Strategies.StrategyInitializer;
import ore.forge.Strategies.UpgradeStrategies.UpgradeStrategy;

//@author Nathan Ulmen
//TODO: Invulnerability could have its implementation changed to be an ObserverEffect???
//On state change/notify evaluate a condition then do something depending on the result of this evaluation.
public class ObserverOreEffect implements OreEffect , StrategyInitializer<UpgradeStrategy> {
    //On upgrade(temp change, value change, multi-ore change, Speed Change,
    private float duration;
    private int charges;
    private final UpgradeStrategy upgradeStrategy;


    public ObserverOreEffect(float duration, int charges, UpgradeStrategy strategy) {
        this.duration = duration;
        this.charges = charges;
        this.upgradeStrategy = strategy;

    }

    public ObserverOreEffect(JsonValue jsonValue) {
        this.duration = jsonValue.getFloat("duration");
        this.charges = jsonValue.getInt("charges");
        upgradeStrategy = createOrNull(jsonValue, "upgrade", "upgradeName");
    }

    //Clone Constructor
    private ObserverOreEffect(ObserverOreEffect clone) {
        this.duration = clone.duration;
        this.charges = clone.charges;
        this.upgradeStrategy = clone.upgradeStrategy.clone();
    }

    @Override
    public void activate(float deltaT, Ore ore) {
        if (upgradeStrategy != null) {
            upgradeStrategy.applyTo(ore);
        }
    }

    public ValueOfInfluence getObservedField() {
        return null;
    }

    @Override
    public OreEffect clone() {
        return new ObserverOreEffect(this);
    }

    @Override
    public boolean isEndStepEffect() {
        return false;
    }
}
