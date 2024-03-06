package ore.forge.Strategies.UpgradeStrategies;

import ore.forge.Ore;
import ore.forge.Strategies.OreStrategies.OreStrategy;

public class ApplyEffect implements UpgradeStrategy {
    private final OreStrategy effect;

    public ApplyEffect(OreStrategy strategy) {
        effect = strategy;
    }

    @Override
    public void applyTo(Ore ore) {
        ore.applyEffect(effect);
    }

    @Override
    public String toString() {
        return  getClass().getSimpleName() + "\t" + effect.toString();
    }
}
