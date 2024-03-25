package ore.forge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import ore.forge.Items.*;
import ore.forge.Items.Blocks.Block;
import ore.forge.Items.Blocks.Worker;

import java.util.ArrayList;
import java.util.HashMap;

//@author Nathan Ulmen
//The ItemMap is responsible for keeping track of placed items and retrieving the positions of their blocks
public class ItemMap {
    protected static ItemMap itemMapSingleton = new ItemMap();
    public final Block[][] mapTiles = new Block[Constants.GRID_DIMENSIONS][Constants.GRID_DIMENSIONS];
    private final ArrayList<Item> placedItems = new ArrayList<>();

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
        return mapTiles[(int) target.x][(int) target.y];
    }

    public Block getBlock(float X, float Y) {
        return mapTiles[(int) X][(int) Y];
    }

    public void setBlock(float X, float Y, Block block) {
        mapTiles[(int) X][(int) Y] = block;
    }

    public void removeBlock(int X, int Y) {
        mapTiles[X][Y] = null;
    }

    public Item getItem(int X, int Y) {
        if (mapTiles[X][Y] != null) {
            return mapTiles[X][Y].getParentItem();
        }
        return null;
    }

    public Item getItem(Vector3 vector3) {
        if (mapTiles[(int) vector3.x][(int) vector3.y] != null) {
            return mapTiles[(int) vector3.x][(int) vector3.y].getParentItem() ;
        }
        return null;
    }

    public <E extends Worker> Worker getWorker(Vector2 target) {
        return (Worker) mapTiles[(int) target.x][(int) target.y];
    }

    public void saveState() {
        ArrayList<MapData> mapData = new ArrayList<>(50);
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        for (Item item : placedItems) {
           //print Item name, direction, and position.
            mapData.add(new MapData(item.getName(), item.getDirection(), item.getVector2()));
        }

        String jsonOutput = json.prettyPrint(mapData);
        FileHandle fileHandle = Gdx.files.local(Constants.BASE_LAYOUT_FP);
        fileHandle.writeString(jsonOutput, false);
    }

    public void loadState(ResourceManager resourceManager) {
        //TODO: Need to make sure that this goes through the players inventory so that they cant place items they dont have.
        System.out.println("Made it to beginning of load State!");
        HashMap<String, Item> allItems = resourceManager.getAllItems();
        JsonReader jsonReader = new JsonReader();
        JsonValue fileContents = jsonReader.parse(Gdx.files.local(Constants.BASE_LAYOUT_FP));
        String itemName;
        Item itemToPlace;
        for (JsonValue jsonValue : fileContents) {
            itemName = jsonValue.getString("itemName");
            System.out.println(itemName);
            System.out.println(allItems.get(itemName));
            itemToPlace = switch (allItems.get(itemName)) {
                case Upgrader ignored -> new Upgrader((Upgrader) allItems.get(itemName));
                case Furnace ignored -> new Furnace((Furnace) allItems.get(itemName));
                case Dropper ignored -> new Dropper((Dropper) allItems.get(itemName));
                case Conveyor ignored -> new Conveyor((Conveyor) allItems.get(itemName));
                default -> throw new IllegalStateException("Unexpected value: " + allItems.get(itemName));
            };
            int x = jsonValue.get("position").getInt("x");
            int y = jsonValue.get("position").getInt("y");
            itemToPlace.alignWith(Direction.valueOf(jsonValue.getString("direction")));
            itemToPlace.placeItem(x,y);
            System.out.println(itemToPlace);
        }
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
        public String itemName;
        public Direction direction;
        public Vector2 position;

        public MapData(String itemName, Direction direction, Vector2 position) {
            this.itemName = itemName;
            this.direction = direction;
            this.position = position;
        }


    }



}


