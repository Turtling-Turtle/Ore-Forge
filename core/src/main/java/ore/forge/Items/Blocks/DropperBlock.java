package ore.forge.Items.Blocks;

import ore.forge.Enums.Direction;
import ore.forge.Items.Dropper;
import ore.forge.Items.Item;
import ore.forge.OreRealm;
import ore.forge.Strategies.DropperStrategies.DropperStrategy;
import ore.forge.Strategies.OreEffects.OreEffect;

//@author Nathan Ulmen
public class DropperBlock extends Block {
    protected static final OreRealm oreRealm = OreRealm.getSingleton();
    private String oreName;
    private float ejectionSpeed;
    private double oreValue;
    private int oreTemp, multiOre, totalOreDropped;
    private OreEffect strategy;
    private DropperStrategy dropperEffect;
    private float dropInterval;
    private float timeSinceLast;

    public DropperBlock(Direction direction, int x, int y) {
        super(direction, x, y);
    }

    public DropperBlock(Item parentItem, String oreName, double oreValue, int oreTemp, int multiOre, float speed, OreEffect strategy) {
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

    public void dropOre() {
        //TODO: Implement Dropper Effects.
        Block blockInFront = itemMap.getBlockInFront(vector2, direction);
        if (!oreRealm.stackOfOre.isEmpty() && blockInFront != null && blockInFront.isProcessBlock()) {
            OreEffect effect;
            if (strategy == null) {
                effect = null;
            } else {
                effect = strategy.clone();
            }
//            if (dropperEffect != null) {
//                dropperEffect.applyTo(oreRealm.peek(), (Dropper) this.parentItem);
//            }
            createOre(blockInFront, effect);
        }
    }

    private void createOre(Block blockInFront, OreEffect effect) {
        oreRealm.giveOre()
            .setVector(vector2)
            .applyBaseStats(oreValue, oreTemp, multiOre, oreName, effect)
            .setDestination(blockInFront.getVector(), ejectionSpeed, direction);
        increaseTotalOreDropped();
    }

    private void increaseTotalOreDropped() {
        ((Dropper) parentItem).incrementTotalOreDropped();
    }

}
