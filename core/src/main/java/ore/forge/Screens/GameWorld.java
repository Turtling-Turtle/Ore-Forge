package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ore.forge.*;
import ore.forge.Input.*;
import ore.forge.Items.Conveyor;
import ore.forge.Items.Dropper;
import ore.forge.Items.Item;
import ore.forge.Player.Player;

import java.util.concurrent.*;


public class GameWorld extends CustomScreen {
    private final SpriteBatch batch;
    protected static OreRealm oreRealm = OreRealm.getSingleton();
    public static ItemMap itemMap = ItemMap.getSingleton();
    protected static Player player = Player.getSingleton();
    public Actor a;
    private final InputHandler inputHandler;
    BitmapFont font2 = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
    private final Stopwatch stopwatch = new Stopwatch(TimeUnit.MICROSECONDS);


    private final UserInterface userInterface;
    private final Texture buildModeTexture = new Texture(Gdx.files.internal("PlayerSelect.png"));
    private final Texture blockTexture = new Texture(Gdx.files.internal("RockTile.png"));
    private final Texture oreTexture = new Texture(Gdx.files.internal("Ruby2.png"));


    public GameWorld(OreForge game, ResourceManager resourceManager) {
        super(game, resourceManager);
        batch = new SpriteBatch(4000);
        inputHandler = new InputHandler(game);
        camera.zoom = 0.04f;
        userInterface = new UserInterface(game, inputHandler.mouseWorld);
        inputHandler.setMode(InputHandler.Mode.INVENTORY);
        InventoryModeProcessor currentMode = (InventoryModeProcessor) inputHandler.getCurrentMode();
        currentMode.setUserInterface(userInterface);


        for (ItemIcon icon : userInterface.getInventoryTable().getAllIcons()) {
            icon.setProcessor(currentMode);
        }

        inputHandler.exitMode();
        camera.position.set(Constants.GRID_DIMENSIONS / 2f, Constants.GRID_DIMENSIONS / 2f, 0f);


    }

    @Override
    public void render(float delta) {
//        stopwatch.restart();
        //updateMouse
//        inputHandler.updateMouse(camera);

        //handleInput.
//        inputHandler.handleInput(delta, camera, game);
        inputHandler.update(delta, camera);
        //update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //Draw game
        batch.begin();
        batch.disableBlending();
        //Draw world tiles.
        drawWorldTiles(camera);
        batch.enableBlending();
        //Draw BuildMode grid Lines
        drawBuildMode();

        //Draw placed items
        drawPlacedItems(delta);
        //Draw selected Item
        drawSelectedItem();
        //Draw active ore
//        stopwatch.start();
        // Wait for the ore processing to complete
        drawActiveOre(delta);
//        stopwatch.stop();
        //Draw Held Item.
        drawHeldItem();
        //Draw held item if build mode is active, needs some work.


        batch.end();
//        Gdx.app.log("Render Calls", String.valueOf(batch.renderCalls));
        userInterface.draw(delta);
//        Gdx.app.log("Frame Time", stopwatch.toString());

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
        if (inputHandler.getCurrentMode() instanceof SelectModeProcessor mode) {
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
//        if (inputHandler.isBuilding()) {
//            batch.setColor(.2f, 1, .2f, .6f);
//            batch.draw(inputHandler.getHeldItem().getTexture(),
//                (int) (inputHandler.mouseWorld.x),
//                (int) (inputHandler.mouseWorld.y),
////                    inputHandler.mouseWorld.x,
////                    inputHandler.mouseWorld.y,
////                    MathUtils.round(inputHandler.mouseWorld.x/ 1.5f) * 1.5f,
////                    MathUtils.round(inputHandler.mouseWorld.y / 1.5f) * 1.5f,
//                (inputHandler.getHeldItem().getWidth() / 2f),
//                (inputHandler.getHeldItem().getHeight() / 2f),
//                inputHandler.getHeldItem().getWidth(),
//                inputHandler.getHeldItem().getHeight(),
//                1,
//                1,
//                inputHandler.getHeldItem().getDirection().getAngle(),
//                0,
//                0,
//                inputHandler.getHeldItem().getTexture().getWidth(),
//                inputHandler.getHeldItem().getTexture().getHeight(),
//                false,
//                false);
//            batch.setColor(1, 1, 1, 1f);
//        }
//
        if (inputHandler.getCurrentMode() instanceof BuildModeProcessor mode) {
            var selectedItem = mode.getHeldItem();
            batch.setColor(.2f, 1, .2f, .6f);


            batch.draw(selectedItem.getTexture(),
                (int) inputHandler.mouseWorld.x,
                (int) inputHandler.mouseWorld.y,
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

    private void updateOre(float delta) {
        oreRealm.getActiveOre().parallelStream().forEach(ore -> ore.act(delta));
    }

    private void drawActiveOre(float delta) {
        for (Ore ore : oreRealm.getActiveOre()) {
            ore.act(delta);
            batch.draw(oreTexture, ore.getVector().x, ore.getVector().y, 1f, 1f);
        }
        oreRealm.updateActiveOre();
    }

    private void drawBuildMode() {
        if (inputHandler.getCurrentMode() instanceof BuildModeProcessor) {
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

    private void drawWorldTiles(Camera camera) {
        //TODO: Only draw what camera can see.
        for (int i = 0; i < Constants.GRID_DIMENSIONS; i++) {
            for (int j = 0; j < Constants.GRID_DIMENSIONS; j++) {
                batch.draw(blockTexture, i, j, 1, 1);
            }
        }
    }

    private void drawPlacedItems(float deltaTime) {
        //TODO: Only Draw what camera can see.
        //Solution to not drawing rectangular sprites correctly is the use Math.floor or Math.Round.
        for (Item item : itemMap.getPlacedItems()) {
            if (item instanceof Dropper) {
                ((Dropper) item).update(deltaTime);
            } else if (item instanceof Conveyor) {
                ((Conveyor) item).update();//Might use this.
            }
            batch.draw(item.getTexture(),
                item.getVector2().x,
                item.getVector2().y,
                (float) (item.getWidth() / 2f),
                (float) (item.getHeight() / 2f),
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
                false);
        }
    }


}
