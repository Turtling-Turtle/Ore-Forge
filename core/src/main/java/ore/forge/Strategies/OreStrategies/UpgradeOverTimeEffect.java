package ore.forge.Strategies.OreStrategies;

import ore.forge.Ore;
import ore.forge.Strategies.UpgradeStrategies.UpgradeStrategy;

//@author Nathan Ulmen
public class UpgradeOverTimeEffect implements OreStrategy {
    private final float interval;
    private float duration;
    private float currentTime;
    private final UpgradeStrategy strategy;

    public UpgradeOverTimeEffect(float interval, float effectDuration, UpgradeStrategy strategyToApply) {
        this.duration = effectDuration;
        this.interval = interval;
        strategy = strategyToApply;
    }

    public UpgradeOverTimeEffect(UpgradeOverTimeEffect effectToClone) {
        this.strategy = effectToClone.strategy;
        this.duration = effectToClone.duration;
        this.interval = effectToClone.interval;
        this.currentTime = 0;
    }

    @Override
    public void activate(float deltaT, Ore ore) {
        duration -= deltaT;
        if (duration <= 0) {
            ore.removeEffect(this);
        }
        currentTime += deltaT;
        while(currentTime >= interval) {
            strategy.applyTo(ore);
            currentTime -= interval;
        }
    }


    @Override
    public OreStrategy clone() {
        try {
            return (OreStrategy) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEndStepEffect() {
        return false;
    }

    public String toString() {
        return "\n" +getClass().getSimpleName() + "\tInterval: " + interval +
            "\tDuration: " + duration + "\nUpgradeStrategy: " + strategy.toString();
    }
}
