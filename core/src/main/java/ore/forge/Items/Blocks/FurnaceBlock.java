package ore.forge.Items.Blocks;


import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.OreDroppedEvent;
import ore.forge.EventSystem.Events.OreSoldEvent;
import ore.forge.Items.Furnace;
import ore.forge.Items.Item;
import ore.forge.Ore;
import ore.forge.OreRealm;
import ore.forge.Player.Player;
import ore.forge.QuestComponents.QuestManager;
import ore.forge.QuestComponents.UpdateType;
import ore.forge.Strategies.UpgradeStrategies.UpgradeStrategy;

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
        eventManager.notifyListeners(new OreSoldEvent(ore, (Furnace) this.getParentItem()));
//        Gdx.app.log("Sold For:", String.valueOf(ore.getOreValue() * ore.getMultiOre()));
        calculateSpecialReward(ore);
        oreRealm.takeOre(ore);
//        ButtonHelper.playFurnaceSellSound();
        setFull(false);
//        Gdx.app.log("Wallet:", String.valueOf(player.getWallet()));
//        Gdx.app.log("Special Points", String.valueOf(player.getSpecialPoints()));
    }

    private void calculateSpecialReward(Ore ore) {
        Furnace parentFurnace = (Furnace) parentItem;
        parentFurnace.setProgress(parentFurnace.getRewardProgess() + ore.getMultiOre());
        player.addSpecialPoints(specialPointReward *(int) (double) ((parentFurnace.getRewardProgess() / parentFurnace.getRewardThreshold())));
        parentFurnace.setProgress(parentFurnace.getRewardProgess() % parentFurnace.getRewardThreshold());
    }

}
