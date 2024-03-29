package ore.forge.Strategies.OreEffects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class BundledEffect implements OreEffect {
    private final OreEffect[] strategies;

    public BundledEffect(OreEffect effect1, OreEffect effect2, OreEffect effect3, OreEffect effect4) {
        strategies = new OreEffect[4];
        strategies[0] = effect1;
        strategies[1] = effect2;
        strategies[2] = effect3;
        strategies[3] = effect4;
    }

    public BundledEffect(JsonValue jsonValue) {
        strategies = new OreEffect[4];
        strategies[0] = createOreStrategyOreNull(jsonValue, "effect1");
        strategies[1] = createOreStrategyOreNull(jsonValue, "effect2");
        strategies[2] = createOreStrategyOreNull(jsonValue, "effect3");
        strategies[3] = createOreStrategyOreNull(jsonValue, "effect4");
    }

    private void setEffect(int i, OreEffect effect) {
        strategies[i] = effect;
    }

    @Override
    public void activate(float deltaT, Ore ore) {
        throw new RuntimeException("WHY IS BUNDLED EFFECT being activated");
    }

    private OreEffect createOreStrategyOreNull(JsonValue jsonValue, String valueToGet) {
        try {
            jsonValue.get(valueToGet);
        } catch (NullPointerException e) {
            return null;
        }
        try {
            Class<?> aClass = Class.forName(jsonValue.get(valueToGet).getString("effectName"));
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            return (OreEffect) constructor.newInstance(jsonValue.get(valueToGet));
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OreEffect clone() {
        return this;
    }

    @Override
    public boolean isEndStepEffect() {
        return false;
    }

    public OreEffect[] getStrategies() {
        return strategies;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (OreEffect strategy : strategies) {
            if (strategy != null) {
                s.append(strategy);
            }
        }
        return String.valueOf(s);
    }

}
