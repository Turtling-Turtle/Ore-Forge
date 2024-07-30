package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;
import ore.forge.ReflectionLoader;

/**
 * @author Nathan Ulmen *
 */
public class IncrementalUpgrade implements UpgradeStrategy {
    private int currentIndex;
    private final UpgradeStrategy[] strategies;

    public IncrementalUpgrade(IncrementalUpgrade incrementalUpgrade) {
        currentIndex = 0;
        strategies = new UpgradeStrategy[incrementalUpgrade.strategies.length];
        for (int i = 0; i < strategies.length; i++) {
            strategies[i] = strategies[currentIndex].cloneUpgradeStrategy();
        }
    }

    public IncrementalUpgrade(JsonValue jsonValue) {
        currentIndex = 0;
        strategies = new UpgradeStrategy[jsonValue.size];
        for (int i = 0; i < strategies.length; i++) {
            strategies[i] = ReflectionLoader.createOrNull(jsonValue.get(i),"upgradeName");
            if (strategies[i] == null) {
                throw new IllegalArgumentException("Could not find UpgradeStrategy for " + jsonValue.get(i));
            }
        }
    }

    @Override
    public void applyTo(Ore ore) {
        strategies[currentIndex++].applyTo(ore);
        if (currentIndex > strategies.length) {
            currentIndex = 0;
        }
    }

    @Override
    public UpgradeStrategy cloneUpgradeStrategy() {
        return new IncrementalUpgrade(this);
    }
}
