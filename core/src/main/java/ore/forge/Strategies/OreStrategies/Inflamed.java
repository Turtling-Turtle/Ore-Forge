package ore.forge.Strategies.OreStrategies;

import ore.forge.Ore;

public class Inflamed implements OreStrategy {
    private float duration;
    private final float tempIncrease;

    public Inflamed(float duration, float tempIncrease) {
        this.duration = duration;
        this.tempIncrease = tempIncrease;
    }

    public Inflamed(Inflamed clone) {
       this.duration = clone.duration;
       this.tempIncrease = clone.tempIncrease;
    }


    @Override
    public void activate(float deltaTime, Ore ore) {
        duration -= deltaTime;
        if (duration <=0) {
            ore.setIsDying(true);
            ore.removeEffect(this);
        } else {
            ore.setTemp(ore.getOreTemp() + tempIncrease * deltaTime);
        }
    }

    @Override
    public OreStrategy clone() {
        return new Inflamed(this);
    }

    @Override
    public boolean isEndStepEffect() {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "\tDuration: " + duration + "\tTemp Increase: " + tempIncrease;
    }
}
