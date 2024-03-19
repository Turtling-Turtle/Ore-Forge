package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.ItemTracker;
import ore.forge.Ore;
import ore.forge.OreRealm;
import ore.forge.Player.Player;

import java.lang.reflect.InvocationTargetException;

//@author Nathan Ulmen
//TODO: Figure out a way to make it so Its not only multiplication but also addition and subtraction.
public class InfluencedUPG implements UpgradeStrategy{
    public enum ValuesOfInfluence {VALUE, TEMPERATURE, MULTIORE, UPGRADE_COUNT,
        ACTIVE_ORE, PLACED_ITEMS, SPECIAL_POINTS, WALLET, PRESTIGE_LEVEL}
    protected static final Player player = Player.getSingleton();
    protected static final OreRealm oreRealm = OreRealm.getSingleton();
    private final ValuesOfInfluence influenceVal;
    private final BasicUpgrade methodOfModification;

    public InfluencedUPG(ValuesOfInfluence valueOfInfluence, BasicUpgrade methodOfModification) {
        influenceVal = valueOfInfluence;
        this.methodOfModification = methodOfModification;
    }

    public InfluencedUPG(JsonValue jsonValue) {
        influenceVal = ValuesOfInfluence.valueOf(jsonValue.getString("valueOfInfluence"));
        try {
            methodOfModification = (BasicUpgrade) Class.forName(jsonValue.getString("type")).getConstructor(JsonValue.class).newInstance(jsonValue);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void applyTo(Ore ore) {
        double finalModifier = switch (influenceVal) {
            case VALUE -> ore.getOreValue() * methodOfModification.getModifier();
            case TEMPERATURE -> ore.getOreTemp() * methodOfModification.getModifier();
            case MULTIORE -> ore.getMultiOre() * methodOfModification.getModifier();
            case UPGRADE_COUNT -> ore.getUpgradeCount() * methodOfModification.getModifier();
            case ACTIVE_ORE -> oreRealm.activeOre.size() * methodOfModification.getModifier();
            case PLACED_ITEMS -> ItemTracker.getSingleton().getPlacedItems().size() * methodOfModification.getModifier();
            case PRESTIGE_LEVEL -> player.getPrestigeLevel() * methodOfModification.getModifier();
            case SPECIAL_POINTS -> player.getSpecialPoints() * methodOfModification.getModifier();
            case WALLET -> player.getWallet() * methodOfModification.getModifier();
        };
        double original = methodOfModification.getModifier();
        methodOfModification.setModifier(finalModifier);
        methodOfModification.applyTo(ore);
        methodOfModification.setModifier(original);
    }

}
