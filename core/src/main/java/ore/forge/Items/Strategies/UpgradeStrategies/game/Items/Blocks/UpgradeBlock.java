package ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Blocks;


import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Direction;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Item;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies.UpgradeStrategy;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Ore;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.UpgradeTag;

//@author Nathan Ulmen
public class UpgradeBlock extends Block implements Worker {
   private UpgradeTag upgradeTag;
   private UpgradeStrategy upgrade;
   private float speed;
    public UpgradeBlock(Direction direction, int x, int y) {
        super(direction, x, y);

    }

    public UpgradeBlock(Item parentItem, UpgradeStrategy upgrade, UpgradeTag upgradeTag, float speed) {
        super(parentItem);
        this.upgrade = upgrade;
        this.upgradeTag = upgradeTag;
        this.speed = speed;
        setName("Upgrade Block");
        setProcessBlock(true);
    }

    @Override
    public void handle(Ore ore) {
        Block blockInFront = map.getBlockInFront(vector2, direction);
        if(blockInFront != null && blockInFront.isValid()) {
            if (ore.getUpgradeTag(upgradeTag).getCurrentUpgrades() < upgradeTag.getMaxUpgrades()) {
                upgrade.applyTo(ore);
                ore.incrementTag(upgradeTag);
            }
            ore.setDestination(blockInFront.getVector(), this.speed, this.direction);
//            ore.setVector(map.getBlockInFront(vector2, direction).getVector());
//            blockInFront.setFull(true); //set the block in-front to full because it now has an ore.
            this.setFull(false); //Set this block to empty because it has now moved the ore.
        }
    }


}
