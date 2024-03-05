package ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Blocks.Block;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Blocks.DropperBlock;

//@author Nathan Ulmen
public class Dropper extends Item {

    protected final float ejectionSpeed = 6f;
    protected final String oreName;
    protected final double oreValue;
    protected final int oreTemp, multiOre;
    protected float timeSinceLast, dropInterval;

    //Used to create from scratch.
    public Dropper(String name, String description, int[][] blockLayout, Tier tier, double itemValue, String oreName, double oreVal, int oreTemp, int multiOre, float dropInterval ) {
        super(name, description, blockLayout, tier, itemValue);
        this.dropInterval = dropInterval;
        this.oreName = oreName;
        this.oreValue = oreVal;
        this.oreTemp = oreTemp;
        this.multiOre = multiOre;
        timeSinceLast = 0;

        initBlockConfiguration(blockLayout);
        setTexture(new Texture(Gdx.files.internal("Dropper.jpg")));
    }

    //Used to "clone" an item.
    public Dropper(Dropper itemToClone) {
        super(itemToClone);
        this.oreName = itemToClone.oreName;
        this.oreValue = itemToClone.oreValue;
        this.oreTemp = itemToClone.oreTemp;
        this.multiOre = itemToClone.multiOre;
        this.dropInterval = itemToClone.getDropInterval();
        this.timeSinceLast = 0f;

        initBlockConfiguration(numberConfig);
        alignWith(itemToClone.direction);
//        this.printBlockConfig();
    }


    public void update(float deltaTime) {//Needs some refinement...
        boolean canReset = false;
        timeSinceLast += deltaTime;
        if (timeSinceLast >= dropInterval) {
            for (Block[] blocks : blockConfig) {
                for (int j = 0; j < blocks.length; j++) {
                    if (blocks[j] instanceof DropperBlock) {
                        if (((DropperBlock) blocks[j]).dropOre()) {
                            canReset = true;
                        }
                    }
                }
            }
        }
        if (canReset) {
            timeSinceLast = 0f;
        }
    }

    @Override
    public void initBlockConfiguration(int[][] numberConfig) {
        for (int i = 0; i < numberConfig.length; i++) {
            for (int j = 0; j < numberConfig[0].length; j++) {
                switch (numberConfig[i][j]) {
                    case 0:
                        blockConfig[i][j] = new Block(this);
                        break;
                    case 3:
                        blockConfig[i][j] = new DropperBlock(this, oreName, oreValue, oreTemp, multiOre, ejectionSpeed);
                        break;
                    case 1:
                    case 4:
                    case 2:
                    default:
                        throw new IllegalArgumentException("Invalid Block Config Value: " + numberConfig[i][j]);
                }
            }
        }
    }

    public float getDropInterval() {
        return dropInterval;
    }

}
