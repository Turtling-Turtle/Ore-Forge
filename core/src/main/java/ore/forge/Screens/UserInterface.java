package ore.forge.Screens;
//User interface will display wallet, Special Points, Prestige Level, Ore Limit, Inventory, and clicked item info?
//Label for Mouse Coordinates, Memory Usage, and FPS

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ore.forge.Constants;
import ore.forge.OreRealm;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;
import ore.forge.Player.Player;
import ore.forge.TheTycoonGame;

import javax.swing.text.NumberFormatter;
import java.awt.TextField;
import java.util.ArrayList;

//@author Nathan Ulmen
public class UserInterface {
    private static OreRealm oreRealm = OreRealm.getSingleton();
    private float updateInterval = 0;
    private Runtime runtime = Runtime.getRuntime();
    private static Player player = Player.getSingleton();
    private com.badlogic.gdx.scenes.scene2d.ui.ScrollPane scrollPane;
    public Stage stage;
    private Table table;
    private ImageButton imageButton;//Icon for
    private TextField textField;//Search Bar;
    private ArrayList<InventoryNode> inventoryNodes;
    private ProgressBar oreLimit;
    private OrthographicCamera camera;
    private Label fpsCounter, wallet, memoryUsage, specialPoints, mouseCoords, activeOre;
    private Vector3 mouse;
    private NumberFormatter numberFormatter;

    public UserInterface(Inventory inventory) {
        table = new Table();
        for (InventoryNode node: inventory.getInventoryNodes()) {
            Skin skin = new Skin();
            ImageTextButton imageTextButton = new ImageTextButton(node.getName(), skin);
            table.add(imageTextButton);
        }

        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        oreLimit = new ProgressBar(0, Constants.ORE_LIMIT, 1, false, style);
    }

    public UserInterface(TheTycoonGame game, Vector3 mouse) {
        numberFormatter = new NumberFormatter();

        this.mouse = mouse;
        camera = new OrthographicCamera();
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        oreLimit = new ProgressBar(0, Constants.ORE_LIMIT, 1, false, style);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);
        Viewport viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        BitmapFont font2 = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
        Label.LabelStyle fpsStyle = new Label.LabelStyle(font2, Color.WHITE);

        fpsCounter = new Label("", fpsStyle);
        fpsCounter.setFontScale(0.6f);
        fpsCounter.setPosition(Gdx.graphics.getWidth()/30f, Gdx.graphics.getHeight()*.95f);
        fpsCounter.setVisible(true);

        memoryUsage = new Label("", fpsStyle);
        memoryUsage.setFontScale(0.6f);
        memoryUsage.setPosition(Gdx.graphics.getWidth()/30f, Gdx.graphics.getHeight()*0.93f);
        memoryUsage.setVisible(true);

        mouseCoords = new Label("", fpsStyle);
        mouseCoords.setFontScale(0.6f);
        mouseCoords.setPosition(Gdx.graphics.getWidth()/30f, Gdx.graphics.getHeight()*.91f);
        mouseCoords.setVisible(true);

        specialPoints = new Label("", fpsStyle);
        specialPoints.setScale(1f);
        specialPoints.setPosition(Gdx.graphics.getWidth()*.55f, Gdx.graphics.getHeight()*.98f);


        stage = new Stage(viewport, game.getSpriteBatch());
        stage.addActor(fpsCounter);
        stage.addActor(memoryUsage);
        stage.addActor(mouseCoords);
        stage.addActor(specialPoints);
        createWallet(fpsStyle);
        createActiveOre(fpsStyle);
    }

    public Table getInventoryTable() {
        return table;
    }

    public void setInventoryNodes(ArrayList<InventoryNode> nodes) {
        inventoryNodes = nodes;
    }

    public ProgressBar getOreLimit() {
        return oreLimit;
    }

    public void draw(float deltaT) {
        camera.update();
        fpsCounter.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        memoryUsage.setText(((runtime.totalMemory() - runtime.freeMemory())/1024/1024) + " MB");
        this.mouseCoords.setText("X: " + (int)mouse.x + " Y: " + (int)mouse.y);
        wallet.setText("$ " + player.getWallet());
        specialPoints.setText("SP: " + player.getSpecialPoints());
        activeOre.setText("Active Ore: " + oreRealm.activeOre.size());
        stage.draw();
        updateInterval = 0f;
    }

    private void createWallet(Label.LabelStyle style) {
        wallet = new Label("", style);
        wallet.setScale(1f);
        wallet.setPosition(Gdx.graphics.getWidth()*.45f, Gdx.graphics.getHeight()*.98f);
        wallet.setVisible(true);
        stage.addActor(wallet);
    }


    private void createActiveOre(Label.LabelStyle style) {
        activeOre = new Label("", style);
        activeOre.setScale(1f);
        activeOre.setPosition(Gdx.graphics.getWidth()*.3f, Gdx.graphics.getHeight()*.98f);
        activeOre.setVisible(true);
        stage.addActor(activeOre);
    }

}
