package ore.forge.Strategies.UpgradeStrategies;

import ore.forge.Ore;

@SuppressWarnings("unused")
public class EffectPurger implements UpgradeStrategy {

    public EffectPurger() {

    }

    @Override
    public void applyTo(Ore ore) {
        ore.purgeEffects();
        //might not use this....
        ore.setIsDoomed(false);
    }

    @Override
    public UpgradeStrategy cloneUpgradeStrategy() {
        return this;
    }

}
