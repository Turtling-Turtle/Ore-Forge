package ore.forge.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.*;
import ore.forge.Items.Blocks.Block;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

//@author Nathan Ulmen
public abstract class Item {
    public enum Tier {PINNACLE, EXOTIC, PRESTIGE, SPECIAL, EPIC, SUPER_RARE, RARE, UNCOMMON, COMMON}

    public enum UnlockMethod {SPECIAL_POINTS, PRESTIGE_LEVEL, QUEST, NONE}


    protected static final ItemMap ITEM_MAP = ItemMap.getSingleton();
    protected Block[][] blockConfig;
    protected int[][] numberConfig;
    //Example config
//    { 0, 1, 1, 0},
//    { 0, 2, 2, 0},
//    { 0, 1, 1, 0},
    protected Direction direction;

    //Tier could be used to identify how to load Acquisition info.
    protected Tier tier;
    private Texture itemTexture;
    protected Vector2 vector2;
    protected String name, description, id;

    protected boolean isPrestigeProof;
    protected float rarity; //Rarity of item. Only matters if item is prestige item.
    protected boolean isShopItem; //Denotes if the item can be purchased from the shop.
    protected double itemValue;
    protected Currency currencyBoughtWith; // The Currency the item is bought and sold with.
    protected UnlockMethod unlockMethod; //Denotes the unlock method.
    protected double unlockRequirements; // The prestige level or special point currency required to unlock item from shop.
    protected boolean isUnlocked; //Denotes if the item has been unlocked for purchase in the shop.
    protected boolean canBeSold;
    protected double sellPrice;


    public Item(String name, String description, int[][] blockLayout, Tier TIER, double itemValue, float rarity) {
        this.name = name;
        this.description = description;
        vector2 = new Vector2();
        this.numberConfig = blockLayout;
        blockConfig = new Block[blockLayout.length][blockLayout[0].length];
        this.tier = TIER;
        this.itemValue = itemValue;
        this.direction = Direction.NORTH;
        this.rarity = BigDecimal.valueOf(rarity).setScale(1, RoundingMode.HALF_UP).floatValue();
    }

    public Item(JsonValue jsonValue) {
        //TODO: Handle errors when creating from Json.
        this.name = jsonValue.getString("name");
        this.id = jsonValue.getString("id");
        this.description = jsonValue.getString("description");
        this.numberConfig = parseBlockLayout(jsonValue.get("blockLayout"));
        this.blockConfig = new Block[numberConfig.length][numberConfig[0].length];
        this.tier = Tier.valueOf(jsonValue.getString("tier"));
        this.itemValue = jsonValue.getDouble("itemValue");
        this.vector2 = new Vector2();
        this.direction = Direction.NORTH;

        //Tier could be used to identify how to load Acquisition info.

//        isPrestigeProof = jsonValue.getBoolean("isPrestigeProof");

        try {
            switch (tier) {
                case PINNACLE -> {
                    rarity = -1;
                    unlockMethod = UnlockMethod.QUEST;
                    unlockRequirements = -1;
                    currencyBoughtWith = Currency.NONE;
                    canBeSold = false;
                    itemValue = -1;
                    sellPrice = -1;
                    isPrestigeProof = true;
                }
                case EXOTIC -> {
                    rarity = -1;
                    unlockMethod = UnlockMethod.QUEST;
                    currencyBoughtWith = Currency.SPECIAL_POINTS;
                    canBeSold = false;
                    itemValue = jsonValue.getDouble("itemValue");
                    sellPrice = jsonValue.getDouble("sellPrice"); // Should just be -1?
                    isPrestigeProof = true;
                }
                case PRESTIGE -> {
                    rarity = jsonValue.getFloat("rarity");
                    unlockMethod = UnlockMethod.valueOf(jsonValue.getString("unlockMethod"));
                    assert unlockMethod == UnlockMethod.QUEST || unlockMethod == UnlockMethod.PRESTIGE_LEVEL;
                    if (unlockMethod == UnlockMethod.PRESTIGE_LEVEL) {
                        unlockRequirements = jsonValue.getDouble("unlockRequirement");
                    } else {
                        unlockRequirements = -1;
                    }
                    currencyBoughtWith = Currency.PRESTIGE_POINTS;
                    canBeSold = true;
                    itemValue = jsonValue.getDouble("itemValue");
                    sellPrice = jsonValue.getDouble("sellPrice");
                    isPrestigeProof = true;
                }
                case SPECIAL -> {
                    rarity = -1;
                    unlockMethod = UnlockMethod.valueOf(jsonValue.getString("unlockMethod"));
                    switch (unlockMethod) {
                        case SPECIAL_POINTS, PRESTIGE_LEVEL ->
                            unlockRequirements = jsonValue.getDouble("unlockRequirement");
                        case QUEST, NONE -> unlockRequirements = -1;
                    }
                    currencyBoughtWith = Currency.SPECIAL_POINTS;
                    canBeSold = true;
                    itemValue = jsonValue.getDouble("itemValue");
                    sellPrice = jsonValue.getDouble("sellPrice");
                    isPrestigeProof = true;
                }
                case EPIC, SUPER_RARE, RARE, UNCOMMON, COMMON -> {
                    rarity = -1;
                    unlockMethod = UnlockMethod.valueOf(jsonValue.getString("unlockMethod"));
                    assert unlockMethod == UnlockMethod.NONE || unlockMethod == UnlockMethod.QUEST;
                    unlockRequirements = -1;
                    currencyBoughtWith = Currency.CASH;
                    canBeSold = true;
                    itemValue = jsonValue.getDouble("itemValue");
                    sellPrice = jsonValue.getDouble("sellPrice");
                    isPrestigeProof = false;
                }
            }

            if (unlockMethod == UnlockMethod.NONE) {
                isUnlocked = true;
            }
        } catch (Exception e) {
            Gdx.app.log("Item", Color.highlightString("Error Occurred While Loading Acquisition " + name + "s' Info: " + e, Color.YELLOW));
        }


//
//        try {
//            var tempRarity = BigDecimal.valueOf(jsonValue.getFloat("rarity"));
//            this.rarity = tempRarity.setScale(1, RoundingMode.HALF_UP).floatValue();
//            isShopItem = jsonValue.getBoolean("isShopItem");
//            if (!isShopItem) {
//                itemValue = 0;
//                currencyBoughtWith = Currency.NONE;
//            } else {
//                currencyBoughtWith = Currency.valueOf(jsonValue.getString("currencyBoughtWith"));
//                itemValue = jsonValue.getDouble("itemValue");
//            }
//
//            canBeSold = jsonValue.getBoolean("canBeSold");
//
//            if (canBeSold) {
//                sellPrice = jsonValue.getDouble("sellPrice");
//            } else {
//                sellPrice = 0;
//            }
//
//
//            unlockMethod = UnlockMethod.valueOf(jsonValue.getString("unlockMethod"));
//            if (unlockMethod == UnlockMethod.NONE) {
//                isUnlocked = true;
//                unlockRequirements = 0;
//            } else if (unlockMethod == UnlockMethod.QUEST) {
//                unlockRequirements = 0;
//                isUnlocked = false;
//            } else {
//                unlockRequirements = jsonValue.getDouble("unlockRequirements");
//                isUnlocked = false;
//            }
//        } catch (Exception e) {
//            Gdx.app.log("ITEM", Color.highlightString("Error Occurred while Loading Acquisition Info.", Color.YELLOW));
//        }


    }

    public Item(Item itemToClone) {
        vector2 = new Vector2();
        name = itemToClone.name;
        id = itemToClone.id;
        description = itemToClone.description;
        numberConfig = itemToClone.numberConfig;
        blockConfig = new Block[numberConfig.length][numberConfig[0].length];
        tier = itemToClone.tier;
        itemValue = itemToClone.itemValue;
        direction = Direction.NORTH;
        itemTexture = itemToClone.itemTexture;
        this.rarity = itemToClone.rarity;
        this.isShopItem = itemToClone.isShopItem;
        this.currencyBoughtWith = itemToClone.currencyBoughtWith;
        this.canBeSold = itemToClone.canBeSold;
        this.sellPrice = itemToClone.sellPrice;
        this.unlockMethod = itemToClone.unlockMethod;
        this.isUnlocked = itemToClone.isUnlocked;
        this.unlockRequirements = itemToClone.unlockRequirements;
    }

    private int[][] parseBlockLayout(JsonValue jsonValue) {
        //TODO: Add error handling.
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
        if (X > ITEM_MAP.mapTiles.length - 1 || X < 0 || Y > ITEM_MAP.mapTiles[0].length - 1 || Y < 0) return;
        int rows = blockConfig.length;
        int columns = blockConfig[0].length;
        //Coordinates of the Item. They are in the bottom left hand corner of it.
        this.vector2.x = X;
        this.vector2.y = Y;

        int xCoord, yCoord;//Coords for blocks in item.
        ITEM_MAP.add(this);
        for (int i = 0; i < rows; i++) {
            for (int j = columns - 1; j >= 0; j--) {
                xCoord = X + j;
                yCoord = (Y + rows - i - 1);
                if (ITEM_MAP.getBlock(xCoord, yCoord) != null && ITEM_MAP.getBlock(xCoord, yCoord).getParentItem() != this) {
                    ITEM_MAP.getBlock(xCoord, yCoord).getParentItem().removeItem();
                }
                blockConfig[i][j].setDirection(this.direction);//ensure direction is the same as parent item
                ITEM_MAP.setBlock(xCoord, yCoord, blockConfig[i][j].setVector2(xCoord, yCoord));
            }

        }
    }

    public boolean didPlace(Vector3 vector3, ArrayList<Item> previousItems) {
        //TODO: Re-write to not be so slow/bad.
        int X = (int) vector3.x;
        int Y = (int) vector3.y;
        if (X > ITEM_MAP.mapTiles.length - 1 || X < 0 || Y > ITEM_MAP.mapTiles[0].length - 1 || Y < 0) return false;
        //Coordinates of the item. They are in the bottom left hand corner of it.
        this.vector2.x = X;
        this.vector2.y = Y;

        int rows = blockConfig.length;
        int columns = blockConfig[0].length;

        int xCoord, yCoord;//Coordinates of the blocks in the item.

        //Check to make sure we won't "collide" with any items in previousItems and that it wont go out of bounds.
        for (int i = 0; i < rows; i++) {
            for (int j = columns - 1; j >= 0; j--) {
                xCoord = X + j;
                yCoord = (Y + rows - i - 1);
                if (xCoord > ITEM_MAP.mapTiles.length - 1 || xCoord < 0 || yCoord > ITEM_MAP.mapTiles[0].length - 1 || yCoord < 0)
                    return false;
                if (ITEM_MAP.getBlock(xCoord, yCoord) != null) {
                    if (previousItems.contains(ITEM_MAP.getItem(xCoord, yCoord))) {
                        return false;
                    }
                }
            }
        }

        //Now actually place the blocks down because we have determined that its "safe" to do so.
        ITEM_MAP.add(this);
        for (int i = 0; i < rows; i++) {
            for (int j = columns - 1; j >= 0; j--) {
                xCoord = X + j;
                yCoord = (Y + rows - i - 1);
                if (ITEM_MAP.getBlock(xCoord, yCoord) != null && ITEM_MAP.getBlock(xCoord, yCoord).getParentItem() != this && !previousItems.contains(ITEM_MAP.getItem(xCoord, yCoord))) {
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
            for (int j = 0; j < blockConfig[0].length; j++) {
                ITEM_MAP.removeBlock(X + j, Y + i);
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
                rotatedLayout[j][row - 1 - i] = blockConfig[i][j];
                rotatedLayout[j][row - 1 - i].setDirection(direction);
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

    public float getRarity() {
        return rarity;
    }

    public abstract void initBlockConfiguration(int[][] numberConfig);

    public abstract void logInfo();

    public Block[][] getBlockConfig() {
        return blockConfig;
    }

    public int getWidth() {
        return numberConfig.length; //Returns original width.
    }

    public int getHeight() {
        return numberConfig[0].length; //returns original height.
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

    public String getID() {
        return id;
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

    public boolean isFacingWest() {
        return direction == Direction.WEST;
    }

    public boolean isFacingSouth() {
        return direction == Direction.SOUTH;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean isUnlocked) {
        this.isUnlocked = isUnlocked;
    }

    public boolean isPrestigeProof() {
        return isPrestigeProof;
    }

    public Currency getCurrencyBoughtWith() {
        return currencyBoughtWith;
    }

    public UnlockMethod getUnlockMethod() {
        return unlockMethod;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public double getUnlockRequirements() {
        return unlockRequirements;
    }

//    @SuppressWarnings("unchecked")
//    protected <E> E loadViaReflection(JsonValue jsonValue, String field) {
//        try {
//            jsonValue.getString(field);
//        } catch (NullPointerException | IllegalArgumentException e) {
//            return null;
//        }
//        try {
//            Class<?> aClass = Class.forName(jsonValue.getString(field));
//            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
//            return (E) constructor.newInstance(jsonValue);
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
//                 ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public String toString() {
        return name + "\t" + description + "\tID: " + id + "\tposition:" + vector2.toString();
    }

    public boolean equals(Item item) {
        return this.id.equals(item.id);
    }

    public void setID(String string) {
        this.id = string;
    }

}
