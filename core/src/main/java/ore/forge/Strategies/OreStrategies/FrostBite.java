package ore.forge.Strategies.OreStrategies;

import ore.forge.Ore;

public class FrostBite implements OreStrategy {
    private final float duration;
    private float current;
    private final float tempDecrease;

    public FrostBite(float duration, float tempDecrease) {
        this.duration = duration;
        this.tempDecrease = tempDecrease;
    }

    @Override
    public void activate(float deltaT, Ore ore) {
        current += deltaT;
        if (current > duration) {
            ore.setMoveSpeed(ore.getMoveSpeed() * .5f);
        } else {
            ore.setTemp(ore.getOreTemp() + tempDecrease * deltaT);
        }
    }

    @Override
    public OreStrategy clone() {
        try {
            return (Inflamed) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
