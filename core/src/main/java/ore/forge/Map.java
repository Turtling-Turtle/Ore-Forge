package ore.forge;

import com.badlogic.gdx.math.Vector2;
import ore.forge.Items.Blocks.Block;
import ore.forge.Items.Blocks.Worker;
import ore.forge.Items.Item;

public class Map {
    protected static Map mapSingleton = new Map();
    public final Block[][] mapTiles = new Block[Constants.GRID_DIMENSIONS][Constants.GRID_DIMENSIONS];

    public static Map getSingleton() {
        if (mapSingleton == null) {
            mapSingleton = new Map();
            return mapSingleton;
        } else {
            return mapSingleton;
        }
    }

    public Map() {
        initializeMap();
    }

    private void initializeMap() {
        int index = 0;
        for (int i = 0; i < mapTiles.length; i++) {
            for (int j = 0; j < mapTiles[0].length; j++) {
               mapTiles[i][j] = null;
            }
        }
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
    public <E extends Worker> Worker getWorker(Vector2 target) {
        return (Worker) mapTiles[(int) target.x][(int) target.y];
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

}


