package ore.forge.Items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import ore.forge.Direction;
import ore.forge.ItemTracker;
import ore.forge.Items.Blocks.Block;
import ore.forge.Map;

//@author Nathan Ulmen
public abstract class Item {
    public enum Tier {PINNACLE, SPECIAL, EXOTIC, PRESTIGE,EPIC, SUPER_RARE, RARE, UNCOMMON, COMMON}

    protected static final Map map = Map.getSingleton();

    protected static final ItemTracker itemTracker = ItemTracker.getSingleton();

    protected Block[][] blockConfig;
    protected int[][] numberConfig;
    //Example config
//    { 0, 1, 1, 0},
//    { 0, 2, 2, 0},
//    { 0, 1, 1, 0},
//    protected int width, height;
    protected Direction direction;
    protected double itemValue;
    protected Tier tier;
    private Texture itemTexture;
    protected Vector2 vector2;
    protected String name, description;

    public Item(String name, String description, int[][]blockLayout, Tier tier, double itemValue) {
        this.name = name;
        this.description = description;
        vector2 = new Vector2();
        this.numberConfig = blockLayout;
        blockConfig = new Block[blockLayout.length][blockLayout[0].length];
        this.tier = tier;
        this.itemValue = itemValue;
        this.direction = Direction.NORTH;
    }

    public Item(Item itemToClone) {
        vector2 = new Vector2();
        name = itemToClone.name;
        description = itemToClone.description;
        numberConfig = itemToClone.numberConfig;
        blockConfig = new Block[numberConfig.length][numberConfig[0].length];
        tier = itemToClone.tier;
        itemValue = itemToClone.itemValue;
        direction = Direction.NORTH;
        itemTexture = itemToClone.itemTexture;
    }

    public void placeItem(int X, int Y) {
        if (X > map.mapTiles.length || X < 0 || Y > map.mapTiles[0].length || Y < 0) return;

        int rows = blockConfig.length;
        int columns = blockConfig[0].length;

        int xCoord, yCoord;//vector coords

        //item coords
        this.vector2.x = X;
        this.vector2.y = Y;
        itemTracker.add(this);

        for (int i = 0; i < rows; i++) {
            for (int j = columns-1; j >= 0; j--) {
                xCoord = X + j;
                yCoord = (Y + rows -i -1);
                if (map.getBlock(xCoord, yCoord) != null && map.getBlock(xCoord, yCoord).getParentItem()!= this) {
                    map.getBlock(xCoord, yCoord).getParentItem().removeItem();
                }
                blockConfig[i][j].setDirection(this.direction);//ensure direction is the same as parent item
                map.setBlock(xCoord, yCoord, blockConfig[i][j].setVector2(xCoord, yCoord));
            }

        }
    }

    public void removeItem() {
        itemTracker.remove(this);
        int X = (int) vector2.x;
        int Y = (int) vector2.y;
        for (int i = 0; i < blockConfig.length; i++) {
            for (int j = 0; j < blockConfig[0].length ; j++) {
                map.removeBlock( X+j, Y+i);
            }
        }
    }

    protected void alignWith(Direction direction) {
        while (this.direction != direction) {
            this.rotateClockwise();
        }
    }

    public void rotateClockwise() {//Instead of reordering the array and making new memory could just use direction to determine the order the array is read in.
        switch (direction) {
            case NORTH:
                direction = Direction.EAST;
                break;
            case EAST:
                direction = Direction.SOUTH;
                break;
            case SOUTH:
                direction = Direction.WEST;
                break;
            case WEST:
                direction = Direction.NORTH;
                break;
        }

        int row = blockConfig.length;
        int column = blockConfig[0].length;
        Block[][] rotatedLayout = new Block[column][row];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                rotatedLayout[j][row - 1 -i] = blockConfig[i][j];
                rotatedLayout[j][row - 1 -i].setDirection(direction);
            }
        }

        blockConfig = rotatedLayout;

    }

    public void printBlockConfig() {
        for (Block[] blocks : blockConfig) {
            for (int j = 0; j < blockConfig[0].length; j++) {
                System.out.print(blocks[j].toString() + " ");
            }
            System.out.println();
        }
    }

    public abstract void initBlockConfiguration(int[][] numberConfig);

    public Block[][] getBlockConfig(){
        return blockConfig;
    }

    public int getWidth() {
        //return width
        return blockConfig.length;
    }

    public int getHeight() {
        //return height
        return blockConfig[0].length;
    }

    public String getDescription() {
        return description;
    }

    public int[][] getNumberConfig() {
        return numberConfig;
    }

    public double getItemValue() {
        return itemValue;
    }

    public Texture getTexture() {
        return itemTexture;
    }

    public void setTexture(Texture texture) {
        itemTexture = texture;
    }

    public Vector2 getVector2() {
        return vector2;
    }

    public String getName() {
        return name;
    }

    public Tier getTier() {
        return tier;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String toString() {
        return name + "\t" +description + vector2.toString();
    }

}
