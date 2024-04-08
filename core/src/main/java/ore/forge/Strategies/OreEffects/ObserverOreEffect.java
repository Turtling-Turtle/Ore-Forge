package ore.forge.Strategies.OreEffects;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Enums.ValueOfInfluence;
import ore.forge.Ore;
import ore.forge.Strategies.StrategyInitializer;
import ore.forge.Strategies.UpgradeStrategies.UpgradeStrategy;

//@author Nathan Ulmen
//TODO: Invulnerability could have its implementation changed to be an ObserverEffect???
public class ObserverOreEffect implements OreEffect , StrategyInitializer<UpgradeStrategy> {
    //On upgrade(temp change, value change, multi-ore change, Speed Change, 
    private float duration;
    private int charges;
    private final UpgradeStrategy upgradeStrategy;
    //what to evaluate (Currently only oreProperties, dont know about evaluating other effects besides Burning and Frostbite).
    //Charges/number of times it will trigger.
    //Guard against infinite loops? (EX: don't want effects to trigger themselves over and over again.)

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

    public ObserverOreEffect(ObserverOreEffect clone) {
        this.duration = clone.duration;
        this.charges = clone.charges;
        this.upgradeStrategy = clone.upgradeStrategy;
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
        try {
            return (OreEffect) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEndStepEffect() {
        return false;
    }
}
