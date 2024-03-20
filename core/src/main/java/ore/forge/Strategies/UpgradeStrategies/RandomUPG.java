package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;

import java.util.Random;

public class RandomUPG implements UpgradeStrategy{
    private Random random;
    private final int bound;


    public RandomUPG(JsonValue jsonValue) {
        bound = jsonValue.getInt("bound");
    }

    public RandomUPG(int bound) {
        random = new Random();
        this.bound = bound;
    }

    @Override
    public void applyTo(Ore ore) {

    }


    public String toString() {
       return null;
    }
}
