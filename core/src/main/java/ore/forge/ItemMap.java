package ore.forge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import ore.forge.Items.*;
import ore.forge.Items.Blocks.Block;
import ore.forge.Items.Blocks.Worker;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;

//@author Nathan Ulmen
//The ItemMap is responsible for keeping track of placed items and retrieving the positions of their blocks
public class ItemMap {
    protected static ItemMap itemMapSingleton = new ItemMap();
    public final Block[][] mapTiles;
    private final ArrayList<Item> placedItems;

    private ItemMap() {
        mapTiles = new Block[Constants.GRID_DIMENSIONS][Constants.GRID_DIMENSIONS];
        placedItems = new ArrayList<>(50);
    }

    public static ItemMap getSingleton() {
        if (itemMapSingleton == null) {
            itemMapSingleton = new ItemMap();
        }
        return itemMapSingleton;
    }

    public Block getBlockInFront(Vector2 target, Direction direction) {
        int x = (int) target.x;
        int y = (int) target.y;
        switch (direction) {
            case NORTH:
                y++;
                break;
            case SOUTH:
                y--;
                break;
            case EAST:
                x++;
                break;
            case WEST:
                x--;
                break;
        }
        return getBlock(x, y);
    }

    public Block getBlock(Vector2 target) {

//        return mapTiles[(int) Math.floor(target.x)][(int) Math.floor(target.y)];
        return mapTiles[(int) target.x][(int) target.y];
    }

    public Block getBlock(float X, float Y) {
        return mapTiles[(int) X][(int) Y];
//        int x = (int) Math.floor(X);
//        int y = (int) Math.floor(Y);
//        return mapTiles[x][y];
    }

    public void setBlock(float X, float Y, Block block) {
        mapTiles[(int) X][(int) Y] = block;
    }

    public void removeBlock(int X, int Y) {
        mapTiles[X][Y] = null;
    }

    public Item getItem(int X, int Y) {
//        if (mapTiles[X][Y] != null) {
//            return mapTiles[X][Y].getParentItem();
//        }
//        return null;
        return mapTiles[X][Y] != null ? mapTiles[X][Y].getParentItem() : null;
    }

    public Item getItem(Vector3 vector3) {
        if (!isInvalid(vector3) && mapTiles[(int) vector3.x][(int) vector3.y] != null) {
            return mapTiles[(int) vector3.x][(int) vector3.y].getParentItem() ;
        }
        return null;
    }

    public <E extends Worker> Worker getWorker(Vector2 target) {
        return (Worker) mapTiles[(int) target.x][(int) target.y];
    }

    public void saveState() {
        ArrayList<MapData> mapData = new ArrayList<>(placedItems.size());
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        for (Item item : placedItems) {
           //print Item ID, direction, and position.
            mapData.add(new MapData(item.getID(), item.getDirection(), item.getVector2()));
        }
        String jsonOutput = json.prettyPrint(mapData);
        FileHandle fileHandle = Gdx.files.local(Constants.BASE_LAYOUT_FP);
        fileHandle.writeString(jsonOutput, false);
    }

    private void asyncSave(ArrayList<Item> copiedList) {

    }

    public void loadState(ResourceManager resourceManager) {
        //TODO: Need to make sure that this goes through the players inventory so that they cant place items they dont have.
//        System.out.println("Made it to beginning of load State!");
        HashMap<String, Item> allItems = resourceManager.getAllItems();
        JsonReader jsonReader = new JsonReader();
        JsonValue fileContents;
        try {
        fileContents = jsonReader.parse(Gdx.files.local(Constants.BASE_LAYOUT_FP));
        } catch (SerializationException e) {
            return;//Nothing to load so no we just leave the function.
        }

        String itemName;
        Item itemToPlace;
        for (JsonValue jsonValue : fileContents) {
            itemName = jsonValue.getString("itemName");
//            System.out.println(itemName);
//            System.out.println(allItems.get(itemName));
            //Create the Item based on the Stored Version in All Items.
            itemToPlace = switch (allItems.get(itemName)) {
                case Upgrader ignored -> new Upgrader((Upgrader) allItems.get(itemName));
                case Furnace ignored -> new Furnace((Furnace) allItems.get(itemName));
                case Dropper ignored -> new Dropper((Dropper) allItems.get(itemName));
                case Conveyor ignored -> new Conveyor((Conveyor) allItems.get(itemName));
                default -> throw new IllegalStateException("Unexpected value: " + allItems.get(itemName));
            };
            //read in the items coordinates and its Direction.
            int x = jsonValue.get("position").getInt("x");
            int y = jsonValue.get("position").getInt("y");
            itemToPlace.alignWith(Direction.valueOf(jsonValue.getString("direction")));
            //place the item
            itemToPlace.placeItem(x,y);
            System.out.println(itemToPlace);
        }
    }

    private boolean isInvalid(Vector3 vector3) {
        return vector3.x > this.mapTiles.length - 1 || vector3.x < 0 || vector3.y > this.mapTiles[0].length - 1 || vector3.y < 0;
    }

    public void add(Item item) {
        placedItems.add(item);
    }

    public void remove(Item item2) {
        placedItems.remove(item2);
    }

    public ArrayList<Item> getPlacedItems() {
        return placedItems;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Block[] mapTile : mapTiles) {
            for (int j = 0; j < mapTiles[0].length; j++) {
                if (mapTile[j] != null) {
                    s.append(mapTile[j].toString());
                }
            }
        }
        return s.toString();
    }

    private class MapData {
        private final String itemName;
        private final Direction direction;
        private final Vector2 position;

        public MapData(String itemName, Direction direction, Vector2 position) {
            this.itemName = itemName;
            this.direction = direction;
            this.position = position;
        }


    }



}


