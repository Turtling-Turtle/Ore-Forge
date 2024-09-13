package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import ore.forge.*;
import ore.forge.Input.*;
import ore.forge.Items.Conveyor;
import ore.forge.Items.Dropper;
import ore.forge.Items.Item;
import ore.forge.Player.Player;
import ore.forge.Screens.Widgets.ItemIcon;

import java.util.ArrayList;
import java.util.concurrent.*;


public class GameWorld extends CustomScreen {
    private ArrayList<Long> frameTimes;
    private final SpriteBatch batch;
    private final OreRealm oreRealm = OreRealm.getSingleton();
    private final ItemMap itemMap = ItemMap.getSingleton();
    private final Player player = Player.getSingleton();
    private final InputHandler inputHandler;
    BitmapFont font2 = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
    private final Stopwatch stopwatch = new Stopwatch(TimeUnit.MICROSECONDS);
    private float timeScalar;
    private ShaderProgram shader;


    private final UserInterface userInterface;
    private final Texture buildModeTexture = new Texture(Gdx.files.internal("PlayerSelect.png"));
    private final Texture blockTexture = new Texture(Gdx.files.internal("RockTile.png"));
    private final Texture oreTexture = new Texture(Gdx.files.internal("Ruby2.png"));

    private final ParticleEffect burning = new ParticleEffect();
    private final ParticleEffect frostbite = new ParticleEffect();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    public GameWorld(OreForge game, ItemManager itemManager) {
        super(game, itemManager);
//        frameTimes = new ArrayList<>((int) 2e8);
//            frostbite.load(Gdx.files.internal("Effects/Frostbite.p"),Gdx.files.internal("Effects"));
//            frostbite.start();
//            frostbite.scaleEffect(0.016f);
//        burning.load(Gdx.files.internal("Effects/BurningEffect.p"), Gdx.files.internal("Effects"));
//        burning.start();
//        burning.scaleEffect(0.016f);

        batch = new SpriteBatch(4000);
        inputHandler = new InputHandler(game);
        camera.zoom = 0.04f;
        userInterface = new UserInterface(game, inputHandler);

        for (ItemIcon icon : userInterface.getInventoryTable().getAllIcons()) {
            icon.setProcessor(inputHandler.getInventoryMode());
        }

        camera.position.set(Constants.GRID_DIMENSIONS / 2f, Constants.GRID_DIMENSIONS / 2f, 0f);

        timeScalar = 1f;

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        delta *= timeScalar();
        stopwatch.restart();
        viewport.apply();

        inputHandler.update(delta, camera);

        camera.update();
        batch.setProjectionMatrix(camera.combined);


        //Draw game
        batch.begin();

        drawWorldTiles(camera); //Draw World Tiles.

        drawBuildMode(); // Draw Build Mode Grid Lines.

        drawPlacedItems(delta); // Draw all placed Items.

        drawSelectedItem(); // If we are selecting an item then draw it.

        drawActiveOre(delta); //Draw all active ore and update them

        drawHighlightedOre(); // Draw selectedOre

        //Draw Held Item.
        drawHeldItem(); // Draw it that we are building with.


        batch.end();

        userInterface.draw(camera, batch, delta);

    }


    private void drawSelectedItem() {
//        if (inputHandler.isSelecting()) {
//            batch.setColor(.2f, 1, .2f, 0.5f);
//            batch.draw(inputHandler.selectedItem.getTexture(),
//                (int) inputHandler.selectedItem.getVector2().x,
//                (int) inputHandler.selectedItem.getVector2().y,
//                (inputHandler.selectedItem.getWidth() / 2f),
//                (inputHandler.selectedItem.getHeight() / 2f),
//                inputHandler.selectedItem.getWidth(),
//                inputHandler.selectedItem.getHeight(),
//                1,
//                1,
//                inputHandler.selectedItem.getDirection().getAngle(),
//                0,
//                0,
//                inputHandler.selectedItem.getTexture().getWidth(),
//                inputHandler.selectedItem.getTexture().getHeight(),
//                false,
//                false);
//            batch.setColor(1, 1, 1, 1f);
//        }
        if (inputHandler.getCurrentMode() instanceof SelectMode mode) {
            var selectedItem = mode.getSelectedItem();
            batch.setColor(.2f, 1, .2f, 0.5f);
            batch.draw(selectedItem.getTexture(),
                (int) selectedItem.getVector2().x,
                (int) selectedItem.getVector2().y,
                (selectedItem.getWidth() / 2f),
                (selectedItem.getHeight() / 2f),
                selectedItem.getWidth(),
                selectedItem.getHeight(),
                1,
                1,
                selectedItem.getDirection().getAngle(),
                0,
                0,
                selectedItem.getTexture().getWidth(),
                selectedItem.getTexture().getHeight(),
                false,
                false
            );
            batch.setColor(1, 1, 1, 1f);
        }
    }


    private void drawHeldItem() {
        if (inputHandler.getCurrentMode() instanceof BuildMode mode) {
            var selectedItem = mode.getHeldItem();
            batch.setColor(.2f, 1, .2f, .6f);
            if (selectedItem.getDirection() == Direction.NORTH || selectedItem.getDirection() == Direction.SOUTH) {
                batch.draw(selectedItem.getTexture(),
                    MathUtils.floor(inputHandler.mouseWorld.x) - xOffset(selectedItem.getWidth(), selectedItem.getHeight()),
                    MathUtils.floor(inputHandler.mouseWorld.y) - yOffset(selectedItem.getWidth(), selectedItem.getHeight()),
//                    selectedItem.getWidth() != selectedItem.getHeight() ? MathUtils.floor(inputHandler.mouseWorld.x) -1f : MathUtils.floor(inputHandler.mouseWorld.x),
//                    selectedItem.getWidth() != selectedItem.getHeight() ? MathUtils.floor(inputHandler.mouseWorld.y) +1f : MathUtils.floor(inputHandler.mouseWorld.y),
                    (selectedItem.getWidth() / 2f),
                    (selectedItem.getHeight() / 2f),
                    selectedItem.getWidth(),
                    selectedItem.getHeight(),
                    1,
                    1,
                    selectedItem.getDirection().getAngle(),
                    0,
                    0,
                    selectedItem.getTexture().getWidth(),
                    selectedItem.getTexture().getHeight(),
                    false,
                    false
                );
            } else {
                batch.draw(selectedItem.getTexture(),
                    MathUtils.floor(inputHandler.mouseWorld.x),
                    MathUtils.floor(inputHandler.mouseWorld.y),
                    (selectedItem.getWidth() / 2f),
                    (selectedItem.getHeight() / 2f),
                    selectedItem.getWidth(),
                    selectedItem.getHeight(),
                    1,
                    1,
                    selectedItem.getDirection().getAngle(),
                    0,
                    0,
                    selectedItem.getTexture().getWidth(),
                    selectedItem.getTexture().getHeight(),
                    false,
                    false
                );
            }


            batch.setColor(1, 1, 1, 1f);
        }
    }

    private void drawHighlightedOre() {
        if (inputHandler.getCurrentMode() instanceof OreObserver mode) {
            var ore = mode.getHighlightedOre();
            batch.setColor(.2f, 1f, .2f, 1f);
            batch.draw(oreTexture, ore.getVector().x, ore.getVector().y, 1f, 1f);
            batch.setColor(1, 1, 1, 1f);
        }
    }

    private void updateOre(float delta) {
        oreRealm.getActiveOre().forEach(ore -> ore.act(delta));
        synchronized (oreRealm.getActiveOre()) {
            oreRealm.updateActiveOre();
        }
    }

    private void drawActiveOre(float delta) {

        if (inputHandler.getCurrentMode() instanceof OreObserver mode) {
            batch.setColor(1, 1, 1, .5f);
        }

        for (Ore ore : oreRealm.getActiveOre()) {
            ore.act(delta);
        }

        for (Ore ore : oreRealm.getActiveOre()) {
            batch.draw(oreTexture, ore.getVector().x, ore.getVector().y, 1f, 1f);
        }
        batch.setColor(1, 1, 1, 1f);
        oreRealm.updateActiveOre();
    }

    private void drawBuildMode() {
        if (inputHandler.getCurrentMode() instanceof BuildMode) {
            batch.setColor(1f, 1, 1f, 0.9f);
            for (int i = 0; i < Constants.GRID_DIMENSIONS; i++) {
                for (int j = 0; j < Constants.GRID_DIMENSIONS; j++) {
                    batch.draw(buildModeTexture, i, j, 1, 1);
                }
            }
            batch.setColor(1, 1, 1, 1f);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(userInterface.stage);
        game.memoryCounter.setPosition(camera.position.x, camera.position.y);
        stage.addActor(game.memoryCounter);
    }

    @Override
    public void resume() {
        Gdx.input.setInputProcessor(userInterface.stage);
    }

    private void drawWorldTiles(Camera camera) {
        //TODO: Only draw what camera can see.
        for (int i = 0; i < Constants.GRID_DIMENSIONS; i++) {
            for (int j = 0; j < Constants.GRID_DIMENSIONS; j++) {
                batch.draw(blockTexture, i, j, 1, 1);
            }
        }
    }

    @Override
    public void hide() {
        if (frameTimes == null) {
            return;
        }
        long avg = 0;
        long biggest = 0;
        for (Long value : frameTimes) {
            avg += value;
            if (value > biggest) {
                biggest = value;
            }
        }
        avg /= frameTimes.size();
        frameTimes.clear();
        avg = TimeUnit.MICROSECONDS.convert(avg, TimeUnit.NANOSECONDS);
        biggest = TimeUnit.MICROSECONDS.convert(biggest, TimeUnit.NANOSECONDS);
        Gdx.app.log("GAME WORLD", "Average Frame Time: " + avg);
        Gdx.app.log("GAME WORLD", "Max Frame Time: " + biggest);
    }

    private void drawPlacedItems(float deltaTime) {
        //TODO: Only Draw what camera can see.
        //Use math.floor or math.round to fix drawing items.
        for (Item item : itemMap.getPlacedItems()) {
            if (item instanceof Dropper) {
                ((Dropper) item).update(deltaTime);
            } else if (item instanceof Conveyor) {
                ((Conveyor) item).update();//Might use this.
            }

            if (item.getDirection() == Direction.NORTH || item.getDirection() == Direction.SOUTH) {
                batch.draw(item.getTexture(),
                    MathUtils.floor(item.getVector2().x) - xOffset(item.getWidth(), item.getHeight()),
                    MathUtils.floor(item.getVector2().y) - yOffset(item.getWidth(), item.getHeight()),
//                    item.getWidth() != item.getHeight() ? MathUtils.floor(item.getVector2().x) +1f : MathUtils.floor(item.getVector2().x),
//                    item.getWidth() != item.getHeight() ? MathUtils.floor(item.getVector2().y) -1f : MathUtils.floor(item.getVector2().y),
                    (item.getWidth() / 2f),
                    (item.getHeight() / 2f),
                    item.getWidth(),
                    item.getHeight(),
                    1,
                    1,
                    item.getDirection().getAngle(),
                    0,
                    0,
                    item.getTexture().getWidth(),
                    item.getTexture().getHeight(),
                    false,
                    false
                );
            } else {
                batch.draw(item.getTexture(),
                    MathUtils.floor(item.getVector2().x),
                    MathUtils.floor(item.getVector2().y),
                    (item.getWidth() / 2f),
                    (item.getHeight() / 2f),
                    item.getWidth(),
                    item.getHeight(),
                    1,
                    1,
                    item.getDirection().getAngle(),
                    0,
                    0,
                    item.getTexture().getWidth(),
                    item.getTexture().getHeight(),
                    false,
                    false
                );
            }
        }
    }

    private float timeScalar() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            timeScalar -= 0.1f;
            if (timeScalar < 0.1) {
                timeScalar = 0.1f;
            }
            Gdx.app.log("GAME WORLD", "Set Time Scalar to: " + timeScalar);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            timeScalar += 0.2f;
            if (timeScalar > 3f) {
                timeScalar = 3f;
            }
            Gdx.app.log("GAME WORLD", "Set Time Scalar to: " + timeScalar);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            timeScalar = 1f;
        }

        return timeScalar;
    }

    private float xOffset(int width, int height) {
        return (((width * 0.5f) - 2) + ((height * -0.5f) + 2));
    }

    private float yOffset(int width, int height) {
        return (((width * -0.5f) + 2) + ((height * 0.5f) - 2));
    }


}
