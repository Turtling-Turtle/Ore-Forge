package ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Blocks;


import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Direction;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Furnace;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Item;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies.UpgradeStrategy;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Ore;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.OreRealm;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Player.Player;

//@author Nathan Ulmen
public class FurnaceBlock extends Block implements Worker {
    private final UpgradeStrategy upgrade;
    protected static OreRealm oreRealm = OreRealm.getSingleton();
    protected static Player player = Player.getSingleton();
    private final int specialPointReward;

    public FurnaceBlock(Direction direction, int x, int y, UpgradeStrategy upgrade) {
        super(direction, x, y);
        this.upgrade = upgrade;
        specialPointReward = 0;
    }

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
        calculateSpecialReward(ore);
        ore.reset();
        oreRealm.takeOre(ore);
        setFull(false);
//        Gdx.app.log("Wallet:", String.valueOf(player.getWallet()));
//        Gdx.app.log("Special Points", String.valueOf(player.getSpecialPoints()));
    }

    private void calculateSpecialReward(Ore ore) {
        for (int i = 0; i < ore.getMultiOre(); i++) {
            ((Furnace) parentItem).incrementProgress();
            if (((Furnace)parentItem).getRewardProgess() >= ((Furnace)parentItem).getRewardThreshold()) {
                player.addSpecialPoints(specialPointReward);
                ((Furnace)parentItem).setProgress(((Furnace) parentItem).getRewardProgess() % ((Furnace) parentItem).getRewardThreshold());
            }
        }
    }

}
