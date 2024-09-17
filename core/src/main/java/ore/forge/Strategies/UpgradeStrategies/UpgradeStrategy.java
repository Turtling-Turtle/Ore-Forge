package ore.forge.Strategies.UpgradeStrategies;

import ore.forge.Ore;


//interface for upgrade strategies.
@SuppressWarnings("unused")
public interface UpgradeStrategy {

    void applyTo(Ore ore);

    //Used to ensure that upgrades remain unique to the upgrade item it's attached to.
    UpgradeStrategy cloneUpgradeStrategy();

}
