package ore.forge.Strategies.UpgradeStrategies;


import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;

//Resets all nonResetterTags on an ore.
public class ResetterUPG implements UpgradeStrategy{

    public ResetterUPG() {}

    public ResetterUPG(JsonValue jsonValue) {

    }

    @Override
    public void applyTo(Ore ore) {
        ore.resetNonResetterTags();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
