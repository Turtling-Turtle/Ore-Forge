package ore.forge.Experimental;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class PhysicsWorld {
    private final static int CELL_SIZE = 2;
    public final static int WORLD_WIDTH = 50, WORLD_HEIGHT = 50;
    public final static int cellWidth = WORLD_WIDTH / CELL_SIZE;
    public final static int cellHeight = WORLD_HEIGHT / CELL_SIZE;
    public static final PhysicsWorld instance = new PhysicsWorld();
    private final List<PhysicsObject>[][] objects;
    private int objectCount;

    @SuppressWarnings("unchecked")
    private PhysicsWorld() {
        objectCount = 0;
        objects = new ArrayList[cellHeight][cellWidth];
        for (int i = 0; i < cellHeight; i++) {
            for (int j = 0; j < cellWidth; j++) {
                objects[i][j] = new ArrayList<>();
            }
        }
    }

    public static PhysicsWorld getInstance() {
        assert instance != null;
        return instance;
    }

    /**
     *
     */
    public List<PhysicsObject> getObjectsAt(int x, int y) {
        return objects[y / CELL_SIZE][x / CELL_SIZE];
    }

    /**
     *
     */
    public void addObject(PhysicsObject object) {
        objectCount++;
        Rectangle boundingRect = object.getBody().getBoundingRectangle();
        int minX = MathUtils.floor((boundingRect.x) / CELL_SIZE);
        int maxX = MathUtils.ceil((boundingRect.x + boundingRect.width) / CELL_SIZE);
        int minY = MathUtils.floor((boundingRect.y) / CELL_SIZE);
        int maxY = MathUtils.ceil((boundingRect.y + boundingRect.height) / CELL_SIZE);

        for (int r = minY; r < maxY; r++) {
            for (int c = minX; c < maxX; c++) {
                objects[r][c].add(object);
            }
        }

    }

    public void updateObject(PhysicsObject object, Rectangle oldBox) {
        removeObject(object, oldBox);
        addObject(object);
    }

    public boolean cellChange(PhysicsObject object, Vector2 previousPosition) {
        float width = object.getBody().getBoundingRectangle().getWidth();
        float height = object.getBody().getBoundingRectangle().getHeight();
        var position = object.getPosition();
        return computeGridMin(previousPosition.x) != computeGridMin(position.x)
            || computeGridMax(previousPosition.x, width) != computeGridMax(position.x, width)
            || computeGridMin(previousPosition.y) != computeGridMin(position.y)
            || computeGridMax(previousPosition.y, height) != computeGridMax(position.y, height);
    }

    /**
     *
     */
    public void removeObject(PhysicsObject object, Rectangle oldBox) {
        objectCount--;
        int minX = computeGridMin(oldBox.x);
        int maxX = computeGridMax(oldBox.x, oldBox.width);
        int minY = computeGridMin(oldBox.y);
        int maxY = computeGridMax(oldBox.y, oldBox.height);

        for (int r = minY; r < maxY; r++) {
            for (int c = minX; c < maxX; c++) {
                objects[r][c].remove(object);
            }
        }
    }

    /**
     *
     */
    public void removeObject(PhysicsObject object) {
        removeObject(object, object.getBody().getBoundingRectangle());
    }


    /**
     * returns all objects in the area of a bounding rectangle
     */
    public void getObjects(List<PhysicsObject> collection, Rectangle boundingRect) {
        int minX = computeGridMin(boundingRect.x);
        int maxX = computeGridMax(boundingRect.x, boundingRect.width);
        int minY = computeGridMin(boundingRect.y);
        int maxY = computeGridMax(boundingRect.y, boundingRect.height);

        var uniqueObjects = new HashSet<>(500, .75f);
        for (int r = minY; r < maxY; r++) {
            for (int c = minX; c < maxX; c++) {
                for (PhysicsObject object : objects[r][c]) {
                    if (uniqueObjects.add(object)) {
                        collection.add(object);
                    }
                }
            }
        }
    }

    public void getObjects(PriorityQueue<PhysicsObject> queue, Rectangle boundingRect) {
        int minX = computeGridMin(boundingRect.x);
        int maxX = computeGridMax(boundingRect.x, boundingRect.width);
        int minY = computeGridMin(boundingRect.y);
        int maxY = computeGridMax(boundingRect.y, boundingRect.height);

        HashSet<PhysicsObject> uniqueObjects = new HashSet<>();
        for (int r = minY; r < maxY; r++) {
            for (int c = minX; c < maxX; c++) {
                for (PhysicsObject object : objects[r][c]) {
                    if (uniqueObjects.add(object)) {
                        queue.add(object);
                    }
                }
            }
        }
    }

    public Collection<PhysicsObject> getAllObjects() {
        HashSet<PhysicsObject> uniqueObjects = new HashSet<>();
        for (int r = 0; r < WORLD_HEIGHT / CELL_SIZE; r++) {
            for (int c = 0; c < WORLD_WIDTH / CELL_SIZE; c++) {
                uniqueObjects.addAll(objects[r][c]);
            }
        }

        return uniqueObjects;
    }

    public int computeGridMin(float position) {
        return MathUtils.floor(position / CELL_SIZE);
    }

    public int computeGridMax(float position, float dimension) {
        return MathUtils.ceil((position + dimension) / CELL_SIZE);
    }

    @Override
    public String toString() {
        String s = "Unique Objects: " + objectCount + "\n";
        int references = 0;
        int maxObjects = 0;
        int trimmedAvg = 0;
        int t = 0;
        for (int i = 0; i < cellHeight; i++) {
            for (int j = 0; j < cellWidth; j++) {
                int objectsInCell = objects[i][j].size();
                if (objectsInCell > 0) {
                    trimmedAvg += objectsInCell;
                    t++;
                }
                references += objectsInCell;
                maxObjects = Math.max(maxObjects, objectsInCell);
            }
        }
        s += "References: " + references + "\n";
        s += "Max Objects in Cell: " + maxObjects + "\n";
        s += "Average Objects in Cell: " + (objectCount / (cellWidth * cellHeight)) + "\n";
        s += "Average Objects in Cell (non-empty cells): " + (trimmedAvg / t) + "\n";
        s += "Cell count: " + (cellHeight * cellWidth) + "\n";
        return s;
    }

    public void removeAllObjects() {
        objectCount = 0;
        for (int i = 0; i < cellHeight; i++) {
            for (int j = 0; j < cellWidth; j++) {
                objects[i][j].clear();
            }
        }
    }

}
