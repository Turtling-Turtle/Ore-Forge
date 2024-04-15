package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;

import java.util.Random;

public class RandomUpgrade implements UpgradeStrategy {
    private Random random;
    private final int bound;


    public RandomUpgrade(JsonValue jsonValue) {
        bound = jsonValue.getInt("bound");
    }

    public RandomUpgrade(int bound) {
        random = new Random();
        this.bound = bound;
    }

    @Override
    public void applyTo(Ore ore) {

    }

    @Override
    public UpgradeStrategy clone() {
        return null;
    }

    @Override
    public String toString() {
       return "[RandomUpgrade]";
    }
}
