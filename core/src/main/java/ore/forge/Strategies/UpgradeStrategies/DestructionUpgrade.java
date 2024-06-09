package ore.forge.Strategies.UpgradeStrategies;

import ore.forge.Ore;

public class DestructionUpgrade implements UpgradeStrategy {

    @Override
    public void applyTo(Ore ore) {
        ore.setIsDoomed(true);
    }

    @Override
    public UpgradeStrategy cloneUpgradeStrategy() {
        return this;
    }

}
