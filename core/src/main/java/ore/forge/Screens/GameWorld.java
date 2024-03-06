package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    protected static ItemTracker itemTracker = ItemTracker.getSingleton();
    protected static OreRealm oreRealm = OreRealm.getSingleton();
    protected static Map gameWorld = Map.getSingleton();
    protected static Player player = Player.getSingleton();
    public Actor a;
    private final InputHandler inputHandler;
    BitmapFont font2 = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
    private final Label.LabelStyle L = new Label.LabelStyle(font2, Color.GREEN);

    private final Label money = new Label("", L);

    private final UserInterface userInterface;
    private final Texture buildModeTexture = new Texture(Gdx.files.internal("PlayerSelect.png"));
    private final Texture blockTexture = new Texture(Gdx.files.internal("RockTile.png"));
    private final Texture oreTexture = new Texture(Gdx.files.internal("Ruby2.png"));


    public GameWorld(TheTycoonGame game, ResourceManager resourceManager) {
        super(game, resourceManager);
        batch = new SpriteBatch(3_000);
        inputHandler = new InputHandler();
        camera.zoom = 0.04f;
        userInterface = new UserInterface(game, inputHandler.mouseWorld);



        camera.position.set(Constants.GRID_DIMENSIONS/2f, Constants.GRID_DIMENSIONS/2f, 0f);

    }

    @Override
    public void render(float delta) {
        //handle input
        inputHandler.updateMouse(camera);
//        game.fpsCounter.setPosition(camera.position.x, camera.position.y);
//        Gdx.app.log("Mouse Coords", mouseWorld.toString());
        inputHandler.handleInput(delta, camera, game);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //Draw game
        batch.begin();


        //Draw world tiles.
        for (int i = 0; i < gameWorld.mapTiles.length; i++) {
            for (int j = 0; j < gameWorld.mapTiles[0].length; j++) {
                batch.draw(blockTexture, i, j, 1, 1);
            }
        }

        //Draw placed items
        for (Item item : itemTracker.getPlacedItems()) {
            if (item instanceof Dropper) {
                ((Dropper) item).update(delta);
            } else if(item instanceof Conveyor) {
                ((Conveyor) item).update();//Might use this.
            }

            batch.draw(item.getTexture(),
                    item.getVector2().x,
                    item.getVector2().y,
                    item.getWidth()/2f,
                    item.getHeight()/2f,
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

        //Draw BuildMode grid Lines
        if (inputHandler.isBuilding()) {
            batch.setColor(1f, 1, 1f, 0.9f);
            for (int i = 0; i < gameWorld.mapTiles.length; i++) {
                for (int j = 0; j < gameWorld.mapTiles[0].length; j++) {
                    batch.draw(buildModeTexture, i, j, 1, 1);
                }
            }
            batch.setColor(1, 1, 1, 1f);
        }

        //Draw Active Ore
        for (Ore ore : oreRealm.activeOre) {
            ore.move(delta);
            batch.draw(oreTexture, ore.getVector().x, ore.getVector().y, 1, 1);
        }
        oreRealm.updateActiveOre();


        //Draw held item if build mode is active, needs some work.
        if (inputHandler.isBuilding()) {
            batch.setColor(.5f, 1, .5f, 0.5f);
            batch.draw(inputHandler.getHeldItem().getTexture(),
                    (int)inputHandler.mouseWorld.x,
                    (int)inputHandler.mouseWorld.y,
                    inputHandler.getHeldItem().getWidth()/2f,
                    inputHandler.getHeldItem().getHeight()/2f,
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

        if (inputHandler.isSelecting()) {
            batch.setColor(.5f, 1, .5f, 0.5f);
            batch.draw(inputHandler.selectedItem.getTexture(),
                    (int)inputHandler.selectedItem.getVector2().x,
                    (int)inputHandler.selectedItem.getVector2().y,
                    inputHandler.selectedItem.getWidth()/2f,
                    inputHandler.selectedItem.getHeight()/2f,
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
//        System.out.println(oreRealm.activeOre.size());

        batch.end();

        userInterface.draw(delta);

//        stage.draw();
//        stage.act(delta);

//        Gdx.app.log("Active Ore:", String.valueOf(oreRealm.activeOre.size()));
//        Gdx.app.log("Player Wallet", String.valueOf(player.getWallet()));
//        Gdx.app.log("Special Points", String.valueOf(player.getSpecialPoints()));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
        game.fpsCounter.setPosition(camera.position.x, camera.position.y);
        stage.addActor(game.fpsCounter);
    }

}
