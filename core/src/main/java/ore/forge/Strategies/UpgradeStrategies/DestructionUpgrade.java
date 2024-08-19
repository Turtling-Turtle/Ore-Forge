package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;

public class DestructionUpgrade implements UpgradeStrategy {

    public DestructionUpgrade(JsonValue jsonValue) {}

    @Override
    public void applyTo(Ore ore) {
        ore.setIsDoomed(true);
    }

    @Override
    public UpgradeStrategy cloneUpgradeStrategy() {
        return this;
    }

}
