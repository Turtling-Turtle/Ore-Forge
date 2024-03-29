package ore.forge.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Items.Blocks.Block;
import ore.forge.Items.Blocks.ConveyorBlock;
import ore.forge.Items.Blocks.UpgradeBlock;
import ore.forge.Strategies.UpgradeStrategies.UpgradeStrategy;
import ore.forge.UpgradeTag;

//@author Nathan Ulmen
public class Upgrader extends Item{

    private final UpgradeTag upgradeTag;

    private final UpgradeStrategy upgrade;

    private final float conveyorSpeed;

    //Used to create from scratch.
    public Upgrader(String name, String description, int[][] blockLayout, Tier tier, double itemValue, float conveyorSpeed, UpgradeStrategy upgrade, UpgradeTag tag) {//Upgrader Constructor
        super(name, description, blockLayout, tier, itemValue);
        this.conveyorSpeed = conveyorSpeed;
        this.upgrade = upgrade;
        this.upgradeTag = tag;
        initBlockConfiguration(this.numberConfig);
        setTexture(new Texture(Gdx.files.internal("Upgrader.png")));
    }

    public Upgrader(JsonValue jsonValue) {
        super(jsonValue);
        this.conveyorSpeed = jsonValue.getFloat("conveyorSpeed");
        this.upgradeTag = new UpgradeTag(jsonValue.get("upgradeTag"));
        this.upgrade = loadViaReflection(jsonValue.get("upgrade"), "upgradeName");
        setTexture(new Texture(Gdx.files.internal("Upgrader.png")));
        initBlockConfiguration(this.numberConfig);
    }

    //Used to "clone" an item.
    public Upgrader(Upgrader itemToClone) {
        super(itemToClone);
        this.conveyorSpeed = itemToClone.conveyorSpeed;
        this.upgrade =  itemToClone.upgrade;
        this.upgradeTag = itemToClone.upgradeTag;
        this.setTexture(itemToClone.getTexture());
        initBlockConfiguration(itemToClone.numberConfig);
        alignWith(itemToClone.direction);
    }

    @Override
    public void initBlockConfiguration(int[][] numberConfig) {
        for (int i = 0; i < numberConfig.length; i++) {
            for (int j = 0; j < numberConfig[0].length; j++) {
                switch (numberConfig[i][j]) {
                    case 0:
                        this.blockConfig[i][j] = new Block(this);
                        break;
                    case 1:
                        this.blockConfig[i][j] = new ConveyorBlock(this, conveyorSpeed, direction);
                        break;
                    case 2:
                        this.blockConfig[i][j] = new UpgradeBlock(this, upgrade, upgradeTag, conveyorSpeed);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid Block Config Value For item Type: " + numberConfig[i][j]);
                }
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + upgradeToString();
    }

    public String upgradeToString() {
        return upgrade.toString();
    }

    @Override
    public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {

    }

    /**
     * Draws this drawable at the specified bounds. The drawable should be tinted with {@link Batch#getColor()}, possibly by
     * mixing its own color.
     *
     * @param batch
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {

    }

    @Override
    public float getLeftWidth() {
        return 0;
    }

    @Override
    public void setLeftWidth(float leftWidth) {

    }

    @Override
    public float getRightWidth() {
        return 0;
    }

    @Override
    public void setRightWidth(float rightWidth) {

    }

    @Override
    public float getTopHeight() {
        return 0;
    }

    @Override
    public void setTopHeight(float topHeight) {

    }

    @Override
    public float getBottomHeight() {
        return 0;
    }

    @Override
    public void setBottomHeight(float bottomHeight) {

    }

    @Override
    public float getMinWidth() {
        return 0;
    }

    @Override
    public void setMinWidth(float minWidth) {

    }

    @Override
    public float getMinHeight() {
        return 0;
    }

    @Override
    public void setMinHeight(float minHeight) {

    }
}
