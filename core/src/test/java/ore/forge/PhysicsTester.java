package ore.forge;

import com.badlogic.gdx.math.Polygon;
import ore.forge.Experimental.PhysicsObject;
import ore.forge.Experimental.PhysicsWorld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PhysicsTester {
    PhysicsWorld physicsWorld;

    @BeforeEach
    void setUp() {
        physicsWorld = PhysicsWorld.getInstance();
    }

    @Test
    void testSingletonInstance() {
        PhysicsWorld instance1 = PhysicsWorld.getInstance();
        PhysicsWorld instance2 = PhysicsWorld.getInstance();
        assertSame(instance1, instance2, "PhysicsWorld should be a singleton");
    }

    @Test
    void testAddObject() {
        PhysicsObject obj = new PhysicsObject(new Polygon(new float[]{0, 0, 1, 0, 1, 1, 0, 1}));
        physicsWorld.addObject(obj);
        List<PhysicsObject> objectsAt00 = physicsWorld.getObjectsAt(0, 0);
        assertTrue(objectsAt00.contains(obj), "Object should be in the (0,0) cell");
    }

    @Test
    void testObjectSpanningMultipleCells() {
        PhysicsObject obj = new PhysicsObject(new Polygon(new float[]{0, 0, 2, 0, 2, 2, 0, 2}));
        physicsWorld.addObject(obj);

        // The object should span (0,0), (1,0), (0,1), (1,1)
        assertTrue(physicsWorld.getObjectsAt(0, 0).contains(obj));
        assertTrue(physicsWorld.getObjectsAt(1, 0).contains(obj));
        assertTrue(physicsWorld.getObjectsAt(0, 1).contains(obj));
        assertTrue(physicsWorld.getObjectsAt(1, 1).contains(obj));
    }

    @Test
    void testGetObjectsAtOutOfBounds() {
//        assertTrue(physicsWorld.getObjectsAt(-1, -1).isEmpty(), "Out-of-bounds lookup should return an empty list");
//        assertTrue(physicsWorld.getObjectsAt(PhysicsWorld.WORLD_WIDTH + 1, PhysicsWorld.WORLD_HEIGHT + 1).isEmpty(), "Out-of-bounds lookup should return an empty list");
    }

    @Test
    void testEmptyCell() {
        assertTrue(physicsWorld.getObjectsAt(10, 10).isEmpty(), "Empty cell should return an empty list");
    }

}
