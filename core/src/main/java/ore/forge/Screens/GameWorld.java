package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import ore.forge.*;
import ore.forge.Items.Conveyor;
import ore.forge.Items.Dropper;
import ore.forge.Items.Item;
import ore.forge.Player.InputHandler;
import ore.forge.Player.Player;

public class GameWorld extends CustomScreen{
    private final SpriteBatch batch;
    protected static OreRealm oreRealm = OreRealm.getSingleton();
    public static ItemMap itemMap = ItemMap.getSingleton();
    protected static Player player = Player.getSingleton();
    public Actor a;
    private final InputHandler inputHandler;
    BitmapFont font2 = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
    private final Label.LabelStyle L = new Label.LabelStyle(font2, Color.GREEN);
    Sprite spire;


    private final UserInterface userInterface;
    private final Texture buildModeTexture = new Texture(Gdx.files.internal("PlayerSelect.png"));
    private final Texture blockTexture = new Texture(Gdx.files.internal("RockTile.png"));
    private final Texture oreTexture = new Texture(Gdx.files.internal("Ruby2.png"));


    public GameWorld(OreForge game, ResourceManager resourceManager) {
        super(game, resourceManager);
        batch = new SpriteBatch(3_000);
        inputHandler = new InputHandler();
        camera.zoom = 0.04f;
        userInterface = new UserInterface(game, inputHandler.mouseWorld);
        spire = new Sprite();

        camera.position.set(Constants.GRID_DIMENSIONS/2f, Constants.GRID_DIMENSIONS/2f, 0f);

    }

    @Override
    public void render(float delta) {
        //updateMouse
        inputHandler.updateMouse(camera);
        //handleInput.
        inputHandler.handleInput(delta, camera, game);
        //update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //Draw game
        batch.begin();
        //Draw world tiles.
        drawWorldTiles(camera);
        //Draw placed items
        drawPlacedItems(delta);
        //Draw BuildMode grid Lines
        drawBuildMode();
        //Draw Active Ore
        drawActiveOre(delta);
        //Draw held item if build mode is active, needs some work.
        drawHeldItem();
        drawSelectedItem();

        batch.end();
        userInterface.draw(delta);
    }

    private void drawSelectedItem() {
        if (inputHandler.isSelecting()) {
            batch.setColor(.5f, 1, .5f, 0.5f);
            batch.draw(inputHandler.selectedItem.getTexture(),
                    (int)inputHandler.selectedItem.getVector2().x,
                    (int)inputHandler.selectedItem.getVector2().y,
                    (inputHandler.selectedItem.getWidth()/2f),
                    (inputHandler.selectedItem.getHeight()/2f),
                    inputHandler.selectedItem.getWidth(),
                    inputHandler.selectedItem.getHeight(),
                    1,
                    1,
                    inputHandler.selectedItem.getDirection().getAngle(),
                    0,
                    0,
                    inputHandler.selectedItem.getTexture().getWidth(),
                    inputHandler.selectedItem.getTexture().getHeight(),
                    false,
                    false);
            batch.setColor(1, 1, 1, 1f);
        }
    }

    private void drawHeldItem() {
        if (inputHandler.isBuilding()) {
            batch.setColor(.5f, 1, .5f, .6f);
                batch.draw(inputHandler.getHeldItem().getTexture(),
                    (int)(inputHandler.mouseWorld.x),
                    (int)(inputHandler.mouseWorld.y),
//                    inputHandler.mouseWorld.x,
//                    inputHandler.mouseWorld.y,
//                    MathUtils.round(inputHandler.mouseWorld.x/ 1.5f) * 1.5f,
//                    MathUtils.round(inputHandler.mouseWorld.y / 1.5f) * 1.5f,
                    (inputHandler.getHeldItem().getWidth()/2f),
                    (inputHandler.getHeldItem().getHeight()/2f),
                    inputHandler.getHeldItem().getWidth(),
                    inputHandler.getHeldItem().getHeight(),
                    1,
                    1,
                    inputHandler.getHeldItem().getDirection().getAngle(),
                    0,
                    0,
                    inputHandler.getHeldItem().getTexture().getWidth(),
                    inputHandler.getHeldItem().getTexture().getHeight(),
                    false,
                    false);
            batch.setColor(1, 1, 1, 1f);
        }
    }

    private void drawActiveOre(float delta) {
        for (Ore ore : oreRealm.getActiveOre()) {
            ore.act(delta);
            batch.draw(oreTexture, ore.getVector().x, ore.getVector().y, 1f, 1f);
        }
        oreRealm.updateActiveOre();
    }

    private void drawBuildMode() {
        if (inputHandler.isBuilding()) {
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
        Gdx.input.setInputProcessor(this.stage);
        game.fpsCounter.setPosition(camera.position.x, camera.position.y);
        stage.addActor(game.fpsCounter);
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
            } else if(item instanceof Conveyor) {
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
