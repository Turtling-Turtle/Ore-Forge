package ore.forge.Items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Direction;
import ore.forge.Items.Blocks.Block;
import ore.forge.ItemMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

//@author Nathan Ulmen
public abstract class Item {
    public enum Tier {PINNACLE, SPECIAL, EXOTIC, PRESTIGE,EPIC, SUPER_RARE, RARE, UNCOMMON, COMMON}

    protected static final ItemMap ITEM_MAP = ItemMap.getSingleton();


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

    public Item(String name, String description, int[][]blockLayout, Tier TIER, double itemValue) {
        this.name = name;
        this.description = description;
        vector2 = new Vector2();
        this.numberConfig = blockLayout;
        blockConfig = new Block[blockLayout.length][blockLayout[0].length];
        this.tier = TIER;
        this.itemValue = itemValue;
        this.direction = Direction.NORTH;
    }

    public Item(JsonValue jsonValue) {
       this.name = jsonValue.getString("name");
       this.description = jsonValue.getString("description");
       this.numberConfig = parseBlockLayout(jsonValue.get("blockLayout"));
       this.blockConfig = new Block[numberConfig.length][numberConfig[0].length];
       this.tier = Tier.valueOf(jsonValue.getString("tier"));
       this.itemValue = jsonValue.getDouble("itemValue");
       this.vector2 = new Vector2();
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

    private int[][] parseBlockLayout(JsonValue jsonValue) {
        int rows = jsonValue.size;
        int columns = jsonValue.get(0).size;
        int[][] blockLayout = new int[rows][columns];

        for (int i = 0; i < rows; i++) {//each row in json blockLayout
            JsonValue colum = jsonValue.get(i);//Json array representing current row
            for (int j = 0; j < columns; j++) {//each element in current row
                blockLayout[i][j] = colum.get(j).asInt();
            }
        }

        return blockLayout;
    }

    public void placeItem(int X, int Y) {
        if (X > ITEM_MAP.mapTiles.length || X < 0 || Y > ITEM_MAP.mapTiles[0].length || Y < 0) return;
        int rows = blockConfig.length;
        int columns = blockConfig[0].length;
        int xCoord, yCoord;//vector coords
        //item coords
        this.vector2.x = X;
        this.vector2.y = Y;
        ITEM_MAP.add(this);
        for (int i = 0; i < rows; i++) {
            for (int j = columns-1; j >= 0; j--) {
                xCoord = X + j;
                yCoord = (Y + rows -i -1);
                if (ITEM_MAP.getBlock(xCoord, yCoord) != null && ITEM_MAP.getBlock(xCoord, yCoord).getParentItem()!= this) {
                    ITEM_MAP.getBlock(xCoord, yCoord).getParentItem().removeItem();
                }
                blockConfig[i][j].setDirection(this.direction);//ensure direction is the same as parent item
                ITEM_MAP.setBlock(xCoord, yCoord, blockConfig[i][j].setVector2(xCoord, yCoord));
            }

        }
    }

    public void placeItem(Vector3 vector) {
        int X = (int) vector.x;
        int Y = (int) vector.y;

        if (X > ITEM_MAP.mapTiles.length || X < 0 || Y > ITEM_MAP.mapTiles[0].length || Y < 0) return;

        int rows = blockConfig.length;
        int columns = blockConfig[0].length;
        int xCoord, yCoord;//vector coords
        //item coords
        this.vector2.x = X;
        this.vector2.y = Y;
        ITEM_MAP.add(this);
        for (int i = 0; i < rows; i++) {
            for (int j = columns-1; j >= 0; j--) {
                xCoord = X + j;
                yCoord = (Y + rows -i -1);
                if (ITEM_MAP.getBlock(xCoord, yCoord) != null && ITEM_MAP.getBlock(xCoord, yCoord).getParentItem()!= this) {
                    ITEM_MAP.getBlock(xCoord, yCoord).getParentItem().removeItem();
                }
                blockConfig[i][j].setDirection(this.direction);//ensure direction is the same as parent item
                ITEM_MAP.setBlock(xCoord, yCoord, blockConfig[i][j].setVector2(xCoord, yCoord));
            }

        }
    }

    public boolean placeItem(Vector3 vector3, ArrayList<Item> previousItems) { //THis is scuffed AF.
        int X = (int) vector3.x;
        int Y = (int) vector3.y;

        if (X > ITEM_MAP.mapTiles.length || X < 0 || Y > ITEM_MAP.mapTiles[0].length || Y < 0) return false;
        if (ITEM_MAP.getItem(X, Y) != null && previousItems.contains(ITEM_MAP.getItem(X, Y))) return false;
        int rows = blockConfig.length;
        int columns = blockConfig[0].length;
        int xCoord, yCoord;//vector coords
        //item coords
        this.vector2.x = X;
        this.vector2.y = Y;

        //Check to make sure we wont "collide" with any items in previousItems
        for (int i = 0; i < rows; i++) {
            for (int j = columns-1; j >= 0; j--) {
                xCoord = X + j;
                yCoord = (Y + rows -i -1);
                if (ITEM_MAP.getBlock(xCoord, yCoord) != null) {
                    if (previousItems.contains(ITEM_MAP.getItem(xCoord, yCoord))) {
                        return false;
                    }
                }
            }
        }

        ITEM_MAP.add(this);
        for (int i = 0; i < rows; i++) {
            for (int j = columns-1; j >= 0; j--) {
                xCoord = X + j;
                yCoord = (Y + rows -i -1);
                if (ITEM_MAP.getBlock(xCoord, yCoord) != null && ITEM_MAP.getBlock(xCoord, yCoord).getParentItem()!= this && !previousItems.contains(ITEM_MAP.getItem(xCoord, yCoord))) {
                    ITEM_MAP.getBlock(xCoord, yCoord).getParentItem().removeItem();
                }
                blockConfig[i][j].setDirection(this.direction);//ensure direction is the same as parent item
                ITEM_MAP.setBlock(xCoord, yCoord, blockConfig[i][j].setVector2(xCoord, yCoord));
            }
        }
        return true;
    }


    public void removeItem() {
        ITEM_MAP.remove(this);
        int X = (int) vector2.x;
        int Y = (int) vector2.y;
        for (int i = 0; i < blockConfig.length; i++) {
            for (int j = 0; j < blockConfig[0].length ; j++) {
                ITEM_MAP.removeBlock( X+j, Y+i);
            }
        }
    }

    public void alignWith(Direction direction) {
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

    protected <E> E loadViaReflection(JsonValue jsonValue, String field) {
        try {
            jsonValue.getString(field);
        } catch (NullPointerException e) {
            return null;
        }
        try {
            Class<?> aClass = Class.forName(jsonValue.getString(field));
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            return (E) constructor.newInstance(jsonValue);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return name + "\t" +description + vector2.toString();
    }

}
