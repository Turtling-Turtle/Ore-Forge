package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Nullable;
import ore.forge.Ore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

//@author Nathan Ulmen
//Used To bundle multiple different upgrade strategies into one.
public class BundledUPG implements UpgradeStrategy{
    private final UpgradeStrategy[] upgradeStrategies;
    @Nullable
    public BundledUPG(UpgradeStrategy upgStrat1, UpgradeStrategy upgStrat2, UpgradeStrategy upgStrat3, UpgradeStrategy upgStrat4) {
        upgradeStrategies = new UpgradeStrategy[4];
        upgradeStrategies[0] = upgStrat1;
        upgradeStrategies[1] = upgStrat2;
        upgradeStrategies[2] = upgStrat3;
        upgradeStrategies[3] = upgStrat4;
    }

    public BundledUPG(UpgradeStrategy... upgradeStrategies) {
        this.upgradeStrategies = new UpgradeStrategy[upgradeStrategies.length];
        for (int i = 0; i < upgradeStrategies.length; i++) {
            this.upgradeStrategies[i] = upgradeStrategies[i];
        }
    }

    public BundledUPG(JsonValue jsonValue) {
//        upgradeStrategies = new UpgradeStrategy[4];
//        upgradeStrategies[0] = createOrNull(jsonValue, "upgStrat1");
//        upgradeStrategies[1] = createOrNull(jsonValue, "upgStrat2");
//        upgradeStrategies[2] = createOrNull(jsonValue, "upgStrat3");
//        upgradeStrategies[3] = createOrNull(jsonValue, "upgStrat4");

        this.upgradeStrategies = new UpgradeStrategy[jsonValue.size];
        for (int i = 0; i < jsonValue.size; i++) {
            this.upgradeStrategies[i] = createOrNull(jsonValue, "upgStrat" + String.valueOf(i+1));
        }
    }

    @Override
    public void applyTo(Ore ore) {
        for (UpgradeStrategy upgStrat : upgradeStrategies) {
            if (upgStrat != null) {
                upgStrat.applyTo(ore);
            }
        }
    }

    private UpgradeStrategy createOrNull(JsonValue jsonValue, String valueToGet) {
        Class<?> aClass;
        try {
            try {
                aClass = Class.forName(jsonValue.get(valueToGet).getString("upgradeName"));
            } catch (NullPointerException e) {
                return null;
            }
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            return (UpgradeStrategy) constructor.newInstance(jsonValue.get(valueToGet));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (UpgradeStrategy upgStrat : upgradeStrategies) {
            if (upgStrat != null) {
                s.append("\n" + upgStrat);
            }
        }
        return s.toString();
    }

}
