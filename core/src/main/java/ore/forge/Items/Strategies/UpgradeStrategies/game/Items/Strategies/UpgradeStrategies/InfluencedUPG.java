package ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies;


import ore.forge.game.Items.Strategies.UpgradeStrategies.game.ItemTracker;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Ore;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.OreRealm;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Player.Player;

public class InfluencedUPG extends AbstractUpgrade {
    public enum ValuesOfInfluence {VALUE, TEMPERATURE, MULTIORE, UPGRADE_COUNT,
        ACTIVE_ORE, PLACED_ITEMS, SPECIAL_POINTS, WALLET, PRESTIGE_LEVEL}
    protected static final Player player = Player.getSingleton();
    protected static final OreRealm oreRealm = OreRealm.getSingleton();

    private ValuesOfInfluence influenceVal;
    public InfluencedUPG(double mod, ValueToModify val, ValuesOfInfluence influencer) {
        super(mod, val);
    }

    @Override
    public void applyTo(Ore ore) {
       calcModifier(ore);
    }

    private void calcModifier(Ore ore) {
        double finalModifier;
        switch (influenceVal) {
            case VALUE:
                finalModifier = ore.getOreValue() * getModifier();
                upgrade(ore, finalModifier);
                break;
            case TEMPERATURE:
                finalModifier = ore.getOreTemp() * getModifier();
                upgrade(ore, finalModifier);
                break;
            case MULTIORE:
                finalModifier = ore.getMultiOre() * getModifier();
                upgrade(ore, finalModifier);
                break;
            case UPGRADE_COUNT:
                finalModifier = ore.getUpgradeCount() * getModifier();
                upgrade(ore, finalModifier);
                break;
            case ACTIVE_ORE:
                finalModifier = OreRealm.getSingleton().activeOre.size() * getModifier();
                upgrade(ore, finalModifier);
                break;
            case PLACED_ITEMS:
                finalModifier = ItemTracker.getSingleton().getPlacedItems().size() * getModifier();
                upgrade(ore, finalModifier);
                break;
            case PRESTIGE_LEVEL:
                finalModifier = Player.getSingleton().getPrestigeLevel() * getModifier();
                upgrade(ore, finalModifier);
                break;
            case SPECIAL_POINTS:
                finalModifier = Player.getSingleton().getSpecialPoints() * getModifier();
                upgrade(ore, finalModifier);
                break;
            case WALLET:
                finalModifier = Player.getSingleton().getWallet() * getModifier();
                upgrade(ore, finalModifier);
                break;
        }
    }

    private void upgrade(Ore ore, double finalModifier) {
        switch (this.getValueToMod()) {
            case ORE_VALUE:
                ore.setOreValue(ore.getOreValue()* finalModifier);
                break;
            case TEMPERATURE:
                ore.setTemp((int) (ore.getOreTemp() * finalModifier));
                break;
            case MULTIORE:
                ore.setMultiOre((int) (ore.getMultiOre() * finalModifier));
                break;
        }
    }

}
