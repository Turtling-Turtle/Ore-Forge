package ore.forge.Strategies.UpgradeStrategies;

import ore.forge.Ore;

public class DestructionUPG implements UpgradeStrategy {

    @Override
    public void applyTo(Ore ore) {
        ore.setIsDoomed(true);
    }

}
