package ore.forge.Strategies.OreEffects;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;

public class Burning implements OreEffect {
    private float duration;
    private final float tempIncrease;

    public Burning(float duration, float tempIncrease) {
        this.duration = duration;
        this.tempIncrease = tempIncrease;
    }

    public Burning(JsonValue jsonValue) {
       this.duration = jsonValue.getFloat("duration");
       this.tempIncrease = jsonValue.getFloat("tempIncrease");
    }


    @Override
    public void activate(float deltaTime, Ore ore) {
        duration -= deltaTime;
        if (duration <=0) {
            ore.setIsDoomed(true);
            ore.removeEffect(this);
        } else {
            ore.setTemp(ore.getOreTemp() + tempIncrease * deltaTime);
        }
    }

    @Override
    public OreEffect clone() {
        try {
            return  (OreEffect) super.clone();
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
        return getClass().getSimpleName() + "\tDuration: " + duration + "\tTemp Increase: " + tempIncrease;
    }
}
