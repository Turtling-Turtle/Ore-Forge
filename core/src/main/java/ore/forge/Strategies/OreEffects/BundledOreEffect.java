package ore.forge.Strategies.OreEffects;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;
import ore.forge.Strategies.StrategyInitializer;

public class BundledOreEffect implements OreEffect , StrategyInitializer<OreEffect> {
    private final OreEffect[] strategies;

    public BundledOreEffect(OreEffect... effects) {
        strategies = new OreEffect[effects.length];
        for (int i = 0; i < strategies.length; i++) {
            strategies[i] = effects[i];
        }
    }

    public BundledOreEffect(JsonValue jsonValue) {
//        strategies = new OreEffect[jsonValue.size];
//        for (int i = 0; i < jsonValue.size; i++) {
//            strategies[i] = createOrNull(jsonValue, "effect" + String.valueOf(i+1), "effectName");
//        }

        var effectArray = jsonValue.get("effects");
        strategies = new OreEffect[effectArray.size];
        for (int i = 0; i < effectArray.size; i++) {
            strategies[i] = createOrNull(effectArray.get(i), "effectName");
        }
    }

    @Override
    public void activate(float deltaT, Ore ore) {
        throw new RuntimeException("WHY IS BUNDLED EFFECT being activated");
    }


    @Override
    public OreEffect cloneOreEffect() {
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
                s.append("\n").append(strategy);
            }
        }
        return String.valueOf(s);
    }

}
