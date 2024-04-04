package ore.forge.Strategies.OreEffects;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Enums.ValueOfInfluence;
import ore.forge.Ore;
import ore.forge.Strategies.UpgradeStrategies.UpgradeStrategy;

//@author Nathan Ulmen
//TODO: Invulnerability could have its implementation changed to be an ObserverEffect???
public class ObserverOreEffect implements OreEffect {
    private float duration;
    private int charges;
    private UpgradeStrategy upgradeStrategy;
    //what to evaluate (Currently only oreProperties, dont know about evaluating other effects besides Burning and Frostbite).
    //Charges/number of times it will trigger.
    //Guard against infinite loops? (EX: don't want effects to trigger themselves over and over again.)

    public ObserverOreEffect(float duration, int charges, UpgradeStrategy strategy) {
        this.duration = duration;
        this.charges = charges;
        this.upgradeStrategy = strategy;
    }

    public ObserverOreEffect(JsonValue jsonValue) {

    }

    public ObserverOreEffect(ObserverOreEffect clone) {

    }

    @Override
    public void activate(float deltaT, Ore ore) {

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
