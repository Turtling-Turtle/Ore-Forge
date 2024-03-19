package ore.forge.Strategies.OreStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class BundledEffect implements OreStrategy {
    private final OreStrategy[] strategies;

    public BundledEffect(OreStrategy effect1, OreStrategy effect2, OreStrategy effect3, OreStrategy effect4) {
        strategies = new OreStrategy[4];
        strategies[0] = effect1;
        strategies[1] = effect2;
        strategies[2] = effect3;
        strategies[3] = effect4;
    }

    public BundledEffect(JsonValue jsonValue) {
        strategies = new OreStrategy[4];
        strategies[0] = createOreStrategyOreNull(jsonValue, "effect1");
        strategies[1] = createOreStrategyOreNull(jsonValue, "effect2");
        strategies[2] = createOreStrategyOreNull(jsonValue, "effect3");
        strategies[3] = createOreStrategyOreNull(jsonValue, "effect4");
    }

    private void setEffect(int i, OreStrategy effect) {
        strategies[i] = effect;
    }

    @Override
    public void activate(float deltaT, Ore ore) {
        for (int i = 0; i < strategies.length; i++) {
            if (strategies[i]!= null) {
                strategies[i].activate(deltaT, ore);
            }
        }
    }

    private OreStrategy createOreStrategyOreNull(JsonValue jsonValue, String valueToGet) {
        try {
            jsonValue.get(valueToGet);
        } catch (NullPointerException e) {
            return null;
        }
        try {
            Class<?> aClass = Class.forName(jsonValue.get(valueToGet).getString("effectType"));
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            return (OreStrategy) constructor.newInstance(jsonValue.get(valueToGet));
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OreStrategy clone() {
        return this;
    }

    @Override
    public boolean isEndStepEffect() {
        return false;
    }

    public OreStrategy[] getStrategies() {
        return strategies;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (OreStrategy strategy : strategies) {
            if (strategy != null) {
                s.append(strategy);
            }
        }
        return String.valueOf(s);
    }

}
