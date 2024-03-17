package ore.forge.Items.Blocks;


import ore.forge.Items.Furnace;
import ore.forge.Items.Item;
import ore.forge.Strategies.UpgradeStrategies.UpgradeStrategy;
import ore.forge.Ore;
import ore.forge.OreRealm;
import ore.forge.Player.Player;

//@author Nathan Ulmen
public class FurnaceBlock extends Block implements Worker{
    private final UpgradeStrategy upgrade;
    protected static OreRealm oreRealm = OreRealm.getSingleton();
    protected static Player player = Player.getSingleton();
    private final int specialPointReward;

    public FurnaceBlock(Item parentItem, UpgradeStrategy upgrade, int specialPointReward) {
        super(parentItem);
        this.upgrade = upgrade;
        this.specialPointReward = specialPointReward;
        setProcessBlock(true);
    }

    @Override
    public void handle(Ore ore) {
        upgrade.applyTo(ore);
        player.addToWallet(ore.getOreValue() * ore.getMultiOre());
//        Gdx.app.log("Sold For:", String.valueOf(ore.getOreValue() * ore.getMultiOre()));
        calculateSpecialReward(ore);
        oreRealm.takeOre(ore);
        setFull(false);
//        Gdx.app.log("Wallet:", String.valueOf(player.getWallet()));
//        Gdx.app.log("Special Points", String.valueOf(player.getSpecialPoints()));
    }

    private void calculateSpecialReward(Ore ore) {
        ((Furnace)parentItem).setProgress(((Furnace) parentItem).getRewardProgess() + ore.getMultiOre());
        player.addSpecialPoints(specialPointReward *(int) Math.floor(((Furnace) parentItem).getRewardProgess() / ((Furnace) parentItem).getRewardThreshold()));
        ((Furnace) parentItem).setProgress(ore.getMultiOre() % ((Furnace) parentItem).getRewardThreshold());
    }

}
