package ore.forge.Strategies.OreStrategies;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;
import ore.forge.Strategies.UpgradeStrategies.UpgradeStrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

//@author Nathan Ulmen
public class UpgradeOverTimeEffect implements OreStrategy {
    private final float interval;
    private float duration;
    private float currentTime;
    private final UpgradeStrategy strategy;

    public UpgradeOverTimeEffect(float interval, float effectDuration, UpgradeStrategy strategyToApply) {
        this.duration = effectDuration;
        this.interval = interval;
        strategy = strategyToApply;
    }

    public UpgradeOverTimeEffect(JsonValue jsonValue) {
        try {
            Class<?> aClass = Class.forName(jsonValue.getString("type"));
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            this.strategy = (UpgradeStrategy) constructor.newInstance(jsonValue);
        } catch (ClassNotFoundException
                 | NoSuchMethodException
                 | InvocationTargetException
                 | InstantiationException
                 | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        this.duration = jsonValue.getFloat("duration");
        this.interval = jsonValue.getFloat("interval");
        this.currentTime = 0;
    }

    @Override
    public void activate(float deltaT, Ore ore) {
        duration -= deltaT;
        if (duration <= 0) {
            ore.removeEffect(this);
        }
        currentTime += deltaT;
        while(currentTime >= interval) {
            strategy.applyTo(ore);
            currentTime -= interval;
        }
    }


    @Override
    public OreStrategy clone() {
        try {
            return (OreStrategy) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEndStepEffect() {
        return false;
    }

    public String toString() {
        return "\n" +getClass().getSimpleName() + "\tInterval: " + interval +
            "\tDuration: " + duration + "\nUpgradeStrategy: " + strategy.toString();
    }
}
