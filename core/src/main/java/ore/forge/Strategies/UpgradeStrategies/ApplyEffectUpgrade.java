package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;
import ore.forge.Strategies.OreEffects.OreEffect;

import java.lang.reflect.InvocationTargetException;

/**@author Nathan Ulmen
Applies an OreEffect to an ore.*/
public class ApplyEffectUpgrade implements UpgradeStrategy {
    private final OreEffect effect;

    public ApplyEffectUpgrade(OreEffect strategy) {
        effect = strategy;
    }

    public ApplyEffectUpgrade(JsonValue jsonValue) {
        try {
            Class<?> clasz = Class.forName(jsonValue.get("effectToApply").getString("effectName"));
            effect = (OreEffect) clasz.getConstructor(JsonValue.class).newInstance(jsonValue.get("effectToApply"));
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void applyTo(Ore ore) {
        ore.applyEffect(effect);
    }

    @Override
    public UpgradeStrategy clone() {
        return this;
    }

    @Override
    public String toString() {
        return  "[" + getClass().getSimpleName() + "]\t" + effect.toString();
    }
}
