package ore.forge.Strategies.UpgradeStrategies;

import ore.forge.Ore;

public class TargetedCleanser implements UpgradeStrategy{
    private final String effectToRemove;

    public TargetedCleanser() {
        this.effectToRemove = "";
    }

    @Override
    public void applyTo(Ore ore) {
        ore.removeEffect(effectToRemove);
    }

}
