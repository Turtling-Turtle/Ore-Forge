package ore.forge.Strategies.OreStrategies;

import ore.forge.Ore;

public class FrostBite implements OreStrategy {
    private float duration;
    private final float tempDecrease;


    public FrostBite(float duration, float tempDecrease) {
        this.duration = duration;
        this.tempDecrease = tempDecrease;

    }

    public FrostBite(FrostBite clone) {
        this.duration = clone.duration;
        this.tempDecrease = clone.tempDecrease;
    }


    @Override
    public void activate(float deltaT, Ore ore) {
        duration -= deltaT;
        if (duration <= 0) {
            ore.setSpeedScalar(1);
            ore.removeEffect(this);
        } else {
            ore.setSpeedScalar(.5f);
            ore.setTemp(ore.getOreTemp() + tempDecrease * deltaT);
        }
    }

    @Override
    public OreStrategy clone() {
       return new FrostBite(this);
    }

    @Override
    public boolean isEndStepEffect() {
        return false;
    }

    @Override
    public String toString() {
        return "FrostBite{" +
            "duration=" + duration +
            "tempDecrease=" + tempDecrease +
            '}';
    }
}
