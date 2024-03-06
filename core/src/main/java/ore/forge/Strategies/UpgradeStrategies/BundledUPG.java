package ore.forge.Strategies.UpgradeStrategies;

import ore.forge.Nullable;
import ore.forge.Ore;

//Used To bundle multiple different upgrade strategies into one.
public class BundledUPG implements UpgradeStrategy{
    private final UpgradeStrategy[] upgradeStrategies;
    @Nullable
    public BundledUPG(UpgradeStrategy upgStrat1, UpgradeStrategy upgStrat2, UpgradeStrategy upgStrat3, UpgradeStrategy upgStrat4) {
        upgradeStrategies = new UpgradeStrategy[4];
        upgradeStrategies[0] = upgStrat1;
        upgradeStrategies[1] = upgStrat2;
        upgradeStrategies[2] = upgStrat3;
        upgradeStrategies[3] = upgStrat4;
    }

    @Override
    public void applyTo(Ore ore) {
        for (UpgradeStrategy upgStrat : upgradeStrategies) {
            if (upgStrat != null) {
                upgStrat.applyTo(ore);
            }
        }

    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (UpgradeStrategy upgStrat : upgradeStrategies) {
            if (upgStrat != null) {
                s.append(upgStrat);
            }
        }
        return s.toString();
    }

}
