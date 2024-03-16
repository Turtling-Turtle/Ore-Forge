package ore.forge.Strategies.OreStrategies;

import ore.forge.Ore;
import ore.forge.Strategies.UpgradeStrategies.UpgradeStrategy;

//@author Nathan Ulmen
public class OverTimeValueModifier implements OreStrategy, Cloneable{
    private final float interval;
    private float duration;
    private float currentTime;
    private final UpgradeStrategy strategy;

    public OverTimeValueModifier(float interval, float effectDuration, UpgradeStrategy strategyToApply) {
        this.duration = effectDuration;
        this.interval = interval;
        strategy = strategyToApply;
    }

    @Override
    public void activate(float deltaT, Ore ore) {
        duration -= deltaT;
        if (duration <= 0) {
           ore.removeEffect(this);
           //return;
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
}
