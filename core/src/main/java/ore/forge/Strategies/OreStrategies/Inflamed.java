package ore.forge.Strategies.OreStrategies;

import ore.forge.Ore;
import ore.forge.OreRealm;

public class Inflamed implements OreStrategy, Cloneable{

    protected static final OreRealm oreRealm = OreRealm.getSingleton();
    private final float duration;
    private float currentTime;
    private final float tempIncrease;

    public Inflamed(float duration, float tempIncrease) {
        currentTime = 0f;
        this.duration = duration;
        this.tempIncrease = tempIncrease;
    }

    @Override
    public void activate(float deltaTime, Ore ore) {
        currentTime += deltaTime;
        if (currentTime > duration) {
            oreRealm.takeOre(ore);
        } else {
            ore.setTemp(ore.getOreTemp() + tempIncrease * deltaTime);
        }
    }

    @Override
    public OreStrategy clone() {
        try {
            return (OreStrategy) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "\tDuration: " + duration + "\tTemp Increase: " + tempIncrease;
    }
}
