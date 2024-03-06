package ore.forge.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import ore.forge.Items.Blocks.ConveyorBlock;

//@author Nathan Ulmen
public class Conveyor extends Item{
    private final float conveyorSpeed;

    //Used to create from scratch
    public Conveyor(String name, String description, int[][] blockLayout, Tier tier, double itemValue, float speed){
        super(name, description, blockLayout, tier, itemValue);
        conveyorSpeed = speed;

        initBlockConfiguration(blockLayout);
        setTexture(new Texture(Gdx.files.internal("BasicConveyor.png")));
    }

    //Used to "clone" an item.
    public Conveyor(Conveyor itemToClone) {
        super(itemToClone);
        this.conveyorSpeed = itemToClone.conveyorSpeed;
        initBlockConfiguration(itemToClone.numberConfig);
        alignWith(itemToClone.direction);
    }

    public void update() {
        for (int i = 0; i < blockConfig.length; i++) {
            for (int j = 0; j < blockConfig[i].length; j++) {
                blockConfig[i][j].setFull(false);
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

}
