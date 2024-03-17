package ore.forge.Strategies.OreStrategies;

import ore.forge.Ore;

public class BundledEffect implements OreStrategy {
    private final OreStrategy[] strategies;

    public BundledEffect(OreStrategy effect1, OreStrategy effect2, OreStrategy effect3, OreStrategy effect4) {
        strategies = new OreStrategy[4];
        strategies[0] = effect1;
        strategies[1] = effect2;
        strategies[2] = effect3;
        strategies[3] = effect4;
    }

    public BundledEffect() {
        strategies = new OreStrategy[4];
    }

    private void setEffect(int i, OreStrategy effect) {
        strategies[i] = effect;
    }

    @Override
    public void activate(float deltaT, Ore ore) {
        for (int i = 0; i < strategies.length; i++) {
            if (strategies[i]!= null) {
                strategies[i].activate(deltaT, ore);
            }
        }
    }

    @Override
    public OreStrategy clone() {
        BundledEffect clone = new BundledEffect();
        for (int i = 0; i < strategies.length; i++) {
            if (strategies[i] != null) {
                clone.setEffect(i, strategies[i].clone());
            }
        }
        return clone;
    }

    @Override
    public boolean isEndStepEffect() {
        return false;
    }

    public OreStrategy[] getStrategies() {
        return strategies;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (OreStrategy strategy : strategies) {
            if (strategy != null) {
                s.append(strategy);
            }
        }
        return String.valueOf(s);
    }

}
