package ore.forge.Items.Blocks;

import ore.forge.Direction;
import ore.forge.Items.Item;
import ore.forge.Strategies.DropperStrategies.DropperStrategy;
import ore.forge.OreRealm;
import ore.forge.Strategies.OreStrategies.OreStrategy;

//@author Nathan Ulmen
public class DropperBlock extends Block {
    protected static final OreRealm oreRealm = OreRealm.getSingleton();
    private String oreName;
    private float ejectionSpeed;
    private double oreValue;
    private int oreTemp, multiOre, totalOreDropped;
    private OreStrategy strategy;
    private float dropInterval;
    private float timeSinceLast;

    public DropperBlock(Direction direction, int x, int y) {
        super(direction, x, y);
    }

    public DropperBlock(Item parentItem, String oreName, double oreValue, int oreTemp, int multiOre, float speed, OreStrategy strategy) {
        super(parentItem);
        this.oreName = oreName;
        this.oreValue = oreValue;
        this.oreTemp = oreTemp;
        this.multiOre = multiOre;
        this.ejectionSpeed = speed;
        this.strategy = strategy;
        setProcessBlock(false);
        timeSinceLast = 0f;
    }

    public boolean dropOre() {
        Block blockInFront = map.getBlockInFront(vector2, direction);
            if (!oreRealm.stackOfOre.isEmpty() && blockInFront != null && blockInFront.isProcessBlock()) {
                OreStrategy effect = null;


//                strategy.createOre(oreRealm.giveOre());

                if (strategy != null) {
                    effect = strategy.clone();
                }
                oreRealm.giveOre()
                        .setVector(vector2)
                        .applyBaseStats(oreValue, oreTemp, multiOre, oreName, effect)
                        .setDestination(blockInFront.getVector(), ejectionSpeed, direction);
//                blockInFront.setFull(true);
//                totalOreDropped++;
                return true;
            }
        return false;
    }


}
