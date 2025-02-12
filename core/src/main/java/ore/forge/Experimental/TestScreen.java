package ore.forge.Experimental;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import ore.forge.*;
import ore.forge.Screens.CustomScreen;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestScreen extends CustomScreen {
    private final ShapeRenderer shapeRenderer;
    private final Vector3 mouseWorld, mouseScreen;
    private final ArrayList<Ore> ores = new ArrayList<>();
    private final CoolDown coolDown;
    private static final int THREAD_COUNT = 4;
    private static final int RUN_CONFIG = 2;
    private static final int ORE_PER_UNIT = 0;
    private final Stopwatch stopwatch;
    private final ExecutorService executor;

    public TestScreen(OreForge game, ItemManager itemManager) {
        super(game, itemManager);
        executor = Executors.newFixedThreadPool(THREAD_COUNT);
        shapeRenderer = new ShapeRenderer();
        mouseWorld = new Vector3();
        mouseScreen = new Vector3();
        camera.zoom = 0.04f;
        camera.position.set(Constants.GRID_DIMENSIONS / 2f, Constants.GRID_DIMENSIONS / 2f, 0f);
        coolDown = new CoolDown(0.03f);
        stopwatch = new Stopwatch(TimeUnit.MILLISECONDS);

//        float[] VERTICES = {
//            0, 0, //bottom left
//            1, 0, //bottom right
//            1, 15,  //top right
//            0, 15, //top left
//        };
//        PhysicsObject wall = new PhysicsObject(new Polygon(VERTICES));
//        wall.setIsStatic(true);
//        wall.setCollisionEnabled(true);
//        wall.setDirection(0);
//        wall.setPosition(29, 0);
////        PhysicsWorld.getInstance().addObject(wall);

        for (int i = 3; i < PhysicsWorld.WORLD_WIDTH - 3; i++) {
            for (int j = 3; j < PhysicsWorld.WORLD_HEIGHT - 3; j++) {
                for (int k = 0; k < ORE_PER_UNIT; k++) {
                    var ore = new Ore();
                    ore.setPosition(i, j);
                    ores.add(ore);
                    PhysicsWorld.getInstance().addObject(ore);
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
//        stopwatch.restart();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        mouseScreen.x = Gdx.input.getX();
        mouseScreen.y = Gdx.input.getY();
        mouseWorld.set(camera.unproject(mouseScreen));

//        Gdx.app.log("Mouse Coords", mouseWorld.toString());
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            System.out.println("Fired1");
            var temp = new TestConveyor();
            temp.setPosition(mouseWorld);
            PhysicsWorld.getInstance().addObject(temp);
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && coolDown.update(delta)) {
            System.out.println("Fired2");
            var temp = new Ore();
            temp.setPosition(mouseWorld);
            ores.add(temp);
            Gdx.app.log("Ore", String.valueOf(ores.size()));
            PhysicsWorld.getInstance().addObject(temp);
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            System.out.println("Fired2");
            var temp = new Ore();
            temp.setPosition(mouseWorld);
            ores.add(temp);
            Gdx.app.log("Ore", String.valueOf(ores.size()));
            PhysicsWorld.getInstance().addObject(temp);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            var temp = new TestConveyor();
            temp.setPosition(mouseWorld);
            temp.setDirection(0);
            PhysicsWorld.getInstance().addObject(temp);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            var temp = new TestConveyor();
            temp.setPosition(mouseWorld);
            temp.setDirection(90);
            PhysicsWorld.getInstance().addObject(temp);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            var temp = new TestConveyor();
            temp.setPosition(mouseWorld);
            temp.setDirection(180);
            PhysicsWorld.getInstance().addObject(temp);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            var temp = new TestConveyor();
            temp.setPosition(mouseWorld);
            temp.setDirection(270);
            PhysicsWorld.getInstance().addObject(temp);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            System.out.println(PhysicsWorld.getInstance().getObjectsAt((int) mouseWorld.x, (int) mouseWorld.y));
            System.out.println(PhysicsWorld.getInstance().toString());
        }


        shapeRenderer.rect(0, 0, PhysicsWorld.WORLD_WIDTH, PhysicsWorld.WORLD_HEIGHT);
//        for (float x = 0; x <= 50; x += 1) {
//            shapeRenderer.line(x, 0, x, 50); // Vertical lines
//        }
//        for (float y = 0; y <= 50; y += 1) {
//            shapeRenderer.line(0, y, 50, y); // Horizontal lines
//        }

        switch (RUN_CONFIG) {
            case 0 -> {
                for (Ore ore : ores) {
                    ArrayList<PhysicsObject> temp = new ArrayList<>();
                    PhysicsWorld.getInstance().getObjects(temp, ore.getBody().getBoundingRectangle());
                    ore.update(delta, temp);
                }
            }
            case 1 -> {
                ArrayList<PhysicsObject> objects = new ArrayList<>();
                var rect = new Rectangle(0, 0, 25, 50);
                PhysicsWorld.getInstance().getObjects(objects, rect);
                var otherHalf = new ArrayList<PhysicsObject>();
                var otherRect = new Rectangle(25, 0, 25, 50);
                PhysicsWorld.getInstance().getObjects(otherHalf, otherRect);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (PhysicsObject object : otherHalf) {
                            if (object instanceof Ore) {
                                var temp = new ArrayList<PhysicsObject>();
                                PhysicsWorld.getInstance().getObjects(temp, object.getBody().getBoundingRectangle());
                                object.update(delta, temp);
                            }
                        }
                    }
                });

                for (PhysicsObject object : objects) {
                    if (object instanceof Ore) {
                        var temp = new ArrayList<PhysicsObject>();
                        PhysicsWorld.getInstance().getObjects(temp, object.getBody().getBoundingRectangle());
                        object.update(delta, temp);
                    }
                }
            }
            case 2 -> {
                threaded(delta);
            }
        }


        for (PhysicsObject object : PhysicsWorld.getInstance().getAllObjects()) {
            shapeRenderer.polygon(object.getBody().getTransformedVertices());
        }

        shapeRenderer.end();
//        stopwatch.stop();
//        Gdx.app.log("Frame-Time", stopwatch.toString());
    }

    private void threaded(float delta) {
//        var collisions = new ConcurrentLinkedDeque<Collision>();
        ores.parallelStream().forEach(element -> {
            var temp = element.checkCollision();
            if (temp != null) {
                for (Collision collision : temp) {
//                    if (collision != null) {
//                        collisions.add(collision);
//                    }
                    collision.resolve();
                }
            }
        });

//        collisions.parallelStream().forEach(Collision::resolve);

        for (Ore ore : ores) {
            ore.updatePosition(delta);
        }

    }


}
