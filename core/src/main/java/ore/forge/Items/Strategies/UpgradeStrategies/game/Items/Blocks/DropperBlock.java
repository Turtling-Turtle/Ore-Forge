package ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Blocks;

import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Direction;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Item;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.DropperStrategies.DropperStrategy;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.OreRealm;

//@author Nathan Ulmen
public class DropperBlock extends Block {
    protected static final OreRealm oreRealm = OreRealm.getSingleton();
    private String oreName;
    private float ejectionSpeed;
    private double oreValue;
    private int oreTemp, multiOre, totalOreDropped;
    private DropperStrategy strategy;
    private float dropInterval;
    private float timeSinceLast;

    public DropperBlock(Direction direction, int x, int y) {
        super(direction, x, y);
    }

    public DropperBlock(Item parentItem, String oreName, double oreValue, int oreTemp, int multiOre, float speed) {
        super(parentItem);
        this.oreName = oreName;
        this.oreValue = oreValue;
        this.oreTemp = oreTemp;
        this.multiOre = multiOre;
        this.ejectionSpeed = speed;
        setProcessBlock(false);
        timeSinceLast = 0f;
    }

    public boolean dropOre() {
        Block blockInFront = map.getBlockInFront(vector2, direction);
            if (!oreRealm.stackOfOre.isEmpty() && blockInFront != null && blockInFront.isProcessBlock()) {

//                strategy.createOre(oreRealm.giveOre());


                oreRealm.giveOre()
                        .setVector(vector2)
                        .applyBaseStats(oreValue, oreTemp, multiOre, oreName)
                        .setDestination(blockInFront.getVector(), ejectionSpeed, direction);
//                blockInFront.setFull(true);
//                totalOreDropped++;
                return true;
            }
        return false;
    }


}
