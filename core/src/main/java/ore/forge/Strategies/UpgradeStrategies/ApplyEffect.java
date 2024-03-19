package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;
import ore.forge.Strategies.OreStrategies.OreStrategy;

import java.lang.reflect.InvocationTargetException;

public class ApplyEffect implements UpgradeStrategy {
    private final OreStrategy effect;

    public ApplyEffect(OreStrategy strategy) {
        effect = strategy;
    }

    public ApplyEffect(JsonValue jsonValue) {
        try {
            Class<?> clasz = Class.forName(jsonValue.getString("effect"));
            effect = (OreStrategy) clasz.getConstructor(JsonValue.class).newInstance(jsonValue);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void applyTo(Ore ore) {
        ore.applyEffect(effect);
    }

    @Override
    public String toString() {
        return  getClass().getSimpleName() + "\t" + effect.toString();
    }
}
