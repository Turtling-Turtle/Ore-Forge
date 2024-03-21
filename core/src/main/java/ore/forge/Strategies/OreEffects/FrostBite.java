package ore.forge.Strategies.OreEffects;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;

public class FrostBite implements OreEffect {
    private float duration;
    private final float tempDecrease;


    public FrostBite(float duration, float tempDecrease) {
        this.duration = duration;
        this.tempDecrease = tempDecrease;

    }

    public FrostBite(JsonValue jsonValue) {
        this.duration = jsonValue.getFloat("duration");
        this.tempDecrease = jsonValue.getFloat("tempDecrease");
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
    public OreEffect clone() {
        try {
            return (OreEffect) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
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
