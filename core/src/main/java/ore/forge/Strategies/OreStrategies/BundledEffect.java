package ore.forge.Strategies.OreStrategies;

import ore.forge.Ore;
import ore.forge.Strategies.UpgradeStrategies.UpgradeStrategy;

public class BundledEffect implements OreStrategy{
    private OreStrategy[] effects;

    public BundledEffect(OreStrategy effect1, OreStrategy effect2, OreStrategy effect3, OreStrategy effect4) {
        effects = new OreStrategy[4];
        effects[0] = effect1;
        effects[1] = effect2;
        effects[2] = effect3;
        effects[3] = effect4;
    }

    public BundledEffect() {
        effects = new OreStrategy[4];
    }

    private void setEffect(int i, OreStrategy effect) {
        effects[i] = effect;
    }

    @Override
    public void activate(float deltaT, Ore ore) {
        for (int i = 0; i < effects.length; i++) {
            if (effects[i]!= null) {
                effects[i].activate(deltaT, ore);
            }
        }

    }

    @Override
    public OreStrategy clone() {
        BundledEffect clone = new BundledEffect();
        for (int i = 0; i < effects.length; i++) {
            if (effects[i] != null) {
                clone.setEffect(i, effects[i].clone());
            }
        }
        return clone;
    }
}
