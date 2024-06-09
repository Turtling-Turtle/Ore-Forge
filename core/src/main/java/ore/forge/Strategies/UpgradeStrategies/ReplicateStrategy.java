package ore.forge.Strategies.UpgradeStrategies;

import ore.forge.ButtonHelper;
import ore.forge.Expressions.NumericOperator;
import ore.forge.Expressions.NumericOreProperties;
import ore.forge.Ore;
import ore.forge.OreRealm;
import ore.forge.Strategies.OreEffects.OreEffect;
import ore.forge.Strategies.OreEffects.UpgradeOreEffect;

import java.util.Random;

public class ReplicateStrategy implements OreEffect, UpgradeStrategy {
    private final static OreRealm oreRealm = OreRealm.getSingleton();
    private final Random rand;

    public ReplicateStrategy() {
        rand = new Random();
    }

    @Override
    public void activate(float deltaT, Ore ore) {
        if (rand.nextFloat() * 100 < 50) {
            Ore replicant = oreRealm.queueOre();
            replicant.setVector(ore.getVector());
            replicant.setDestination(ore.getDestination(), ore.getMoveSpeed(), ore.getDirection());
            replicant.applyEffect(new UpgradeOreEffect(0, 0.5f, new BasicUpgrade(0.1, NumericOperator.ASSIGNMENT, NumericOreProperties.SPEED_SCALAR)));
            replicant.applyBaseStats(ore.getOreValue(), ore.getOreTemp(), ore.getMultiOre(), ore.getName(), ore.getID(), this);
        }
    }

    @Override
    public void applyTo(Ore ore) {
        if (rand.nextFloat() * 100 < 50) {
            Ore replicant = oreRealm.queueOre();
            replicant.setVector(ore.getVector());
            replicant.setDestination(ore.getDestination(), ore.getMoveSpeed(), ore.getDirection());
            replicant.applyEffect(new UpgradeOreEffect(0, 0.5f, new BasicUpgrade(0.1, NumericOperator.ASSIGNMENT, NumericOreProperties.SPEED_SCALAR)));
            replicant.applyBaseStats(ore.getOreValue(), ore.getOreTemp(), ore.getMultiOre(), ore.getName(), ore.getID(), this);
        }
    }


    @Override
    public UpgradeStrategy cloneUpgradeStrategy() {
        return this;
    }

    @Override
    public OreEffect cloneOreEffect() {
        return this;
    }


    @Override
    public boolean isEndStepEffect() {
        return false;
    }
}
