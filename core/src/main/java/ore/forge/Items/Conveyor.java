package ore.forge.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Items.Blocks.Block;
import ore.forge.Items.Blocks.ConveyorBlock;

//@author Nathan Ulmen
public class Conveyor extends Item {
    private final float conveyorSpeed;

    //Used to create from scratch
    public Conveyor(String name, String description, int[][] blockLayout, Tier tier, double itemValue, float speed){
        super(name, description, blockLayout, tier, itemValue);
        conveyorSpeed = speed;
        initBlockConfiguration(blockLayout);
        setTexture(new Texture(Gdx.files.internal("BasicConveyor.png")));
    }

    public Conveyor(JsonValue jsonValue) {
        super(jsonValue);
        this.conveyorSpeed = jsonValue.getFloat("conveyorSpeed");
        initBlockConfiguration(this.numberConfig);
        setTexture(new Texture(Gdx.files.internal("BasicConveyor.png")));
    }
    //Used to "clone" an item.
    public Conveyor(Conveyor itemToClone) {
        super(itemToClone);
        this.conveyorSpeed = itemToClone.conveyorSpeed;
        initBlockConfiguration(itemToClone.numberConfig);
        alignWith(itemToClone.direction);
        setTexture(new Texture(Gdx.files.internal("BasicConveyor.png")));
    }

    public void update() {
        for (Block[] blocks : blockConfig) {
            for (Block block: blocks) {
                block.setFull(false);
            }
        }
    }


    @Override
    public void initBlockConfiguration(int[][] numberConfig) {
        for (int i = 0; i < numberConfig.length; i++) {
            for (int j = 0; j < numberConfig[0].length; j++) {
                if (numberConfig[i][j] == 1) {
                    this.blockConfig[i][j] =new ConveyorBlock(this, conveyorSpeed, direction);
                } else {
                    throw new IllegalArgumentException("Invalid Block Config Value For item Type: " + numberConfig[i][j]);
                }
            }
        }
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
