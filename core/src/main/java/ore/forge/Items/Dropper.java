package ore.forge.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Color;
import ore.forge.Items.Blocks.Block;
import ore.forge.Items.Blocks.DropperBlock;
import ore.forge.Strategies.OreEffects.OreEffect;

import java.util.ArrayList;

//@author Nathan Ulmen
public class Dropper extends Item {
    protected final float ejectionSpeed = 6f;
    protected final String oreName;
    protected final double oreValue;
    protected final int oreTemp, multiOre;
    protected float timeSinceLast, dropInterval;
    private int totalOreDropped;
    protected final OreEffect oreEffect; //Effect that the dropper will apply when creating ore.

    //Used to create from scratch.
    public Dropper(String name, String description, int[][] blockLayout, Tier tier, double itemValue, String oreName, double oreVal, int oreTemp, int multiOre, float dropInterval, OreEffect oreStrategies) {
        super(name, description, blockLayout, tier, itemValue);
        this.dropInterval = dropInterval;
        this.oreName = oreName;
        this.oreValue = oreVal;
        this.oreTemp = oreTemp;
        this.multiOre = multiOre;
        timeSinceLast = 0;
        this.oreEffect = oreStrategies;
        totalOreDropped = 0;

        initBlockConfiguration(blockLayout);
        setTexture(new Texture(Gdx.files.internal("Dropper.png")));
    }

    public Dropper(JsonValue jsonValue) {
        super(jsonValue);
        this.dropInterval = jsonValue.getFloat("dropInterval");
        this.oreName = jsonValue.getString("oreName");
        this.oreValue = jsonValue.getDouble("oreValue");
        this.oreTemp = jsonValue.getInt("oreTemp");
        this.multiOre = jsonValue.getInt("multiOre");
        this.oreEffect = loadViaReflection(jsonValue.get("oreStrategy"), "effectName");
        timeSinceLast = 0f;

        initBlockConfiguration(this.numberConfig);
        setTexture(new Texture(Gdx.files.internal("Dropper.png")));
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
        this.oreEffect = itemToClone.oreEffect;
        initBlockConfiguration(this.numberConfig);
        alignWith(itemToClone.direction);
    }

    //Check to see if we should produce an ore.
    public void update(float deltaTime) {
        timeSinceLast += deltaTime;
        while(timeSinceLast >= dropInterval) {
            dropOre();
            timeSinceLast -= dropInterval;
        }
    }

    private void dropOre() {
        for (Block[] blocks : blockConfig) {
            for (Block block : blocks) {
                if (block instanceof DropperBlock) {
                    ((DropperBlock) block).dropOre();
                }
            }
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
                        blockConfig[i][j] = new DropperBlock(this, oreName, oreValue, oreTemp, multiOre, ejectionSpeed, oreEffect);
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

    @Override
    public void logInfo() {
        String info = "\nName: " + getName() + " \tID: " + getID() + "\tTier: " + getTier();
        String stats = "Drop Interval: " + getDropInterval();
        String oreInfo = "Ore Name: " + getOreName() + "\tOre Value: " + oreValue + "\tOre Temperature: " + oreTemp + "\tMultiOre: " + multiOre;
        info += "\n" + stats + "\n" + oreInfo + "\n Ore Effect:" + oreEffect;
        Gdx.app.log("Dropper", Color.highlightString(info, Color.PINK));
    }

    public String getOreName() {
        return oreName;
    }

    public double getOreValue() {
        return oreValue;
    }

    public int getOreTemp() {
        return oreTemp;
    }

    public int getMultiOre() {
        return multiOre;
    }

    public OreEffect getOreEffects() {
        return oreEffect;
    }

    public float getDropInterval() {
        return dropInterval;
    }

    public void incrementTotalOreDropped() {
        totalOreDropped++;
    }

    public int getTotalOreDropped() {
        return totalOreDropped;
    }

}
