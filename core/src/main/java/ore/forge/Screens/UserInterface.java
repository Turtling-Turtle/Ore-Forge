package ore.forge.Screens;
//User interface will display wallet, Special Points, Prestige Level, Ore Limit, Inventory, and clicked item info?
//Label for Mouse Coordinates, Memory Usage, and FPS

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ore.forge.*;
import ore.forge.Input.InputHandler;
import ore.forge.Input.OreObserver;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;
import ore.forge.Player.Player;

import javax.swing.text.NumberFormatter;
import java.util.concurrent.TimeUnit;

//@author Nathan Ulmen
public class UserInterface {
    private static final OreRealm oreRealm = OreRealm.getSingleton();
    private static final ItemMap ITEM_MAP = ItemMap.getSingleton();
    private float updateInterval = 0;
    private final Runtime runtime = Runtime.getRuntime();
    private static final Player player = Player.getSingleton();
    private com.badlogic.gdx.scenes.scene2d.ui.ScrollPane scrollPane;
    public Stage stage;
    private Table table,  nodeTable;
    private ImageButton imageButton;//Icon for
    private final ProgressBar oreLimit;
    private OrthographicCamera camera;
    private Label fpsCounter, wallet, memoryUsage, specialPoints, mouseCoords, activeOre, itemOver;
    private Vector3 mouse;
    private NumberFormatter numberFormatter;
    private final Stopwatch stopwatch = new Stopwatch(TimeUnit.MICROSECONDS);
    private InventoryTable inventoryWidget;
    private ShopMenu shopWidget;
    private InputHandler inputHandler;
    private Label oreInfo;

    public UserInterface(Inventory inventory) {
        table = new Table();
        for (InventoryNode node : inventory.getInventoryNodes()) {
            Skin skin = new Skin();
            ImageTextButton imageTextButton = new ImageTextButton(node.getName(), skin);
            table.add(imageTextButton);
        }

        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        oreLimit = new ProgressBar(0, Constants.ORE_LIMIT, 1, false, style);
    }

    public UserInterface(OreForge game, Vector3 mouse, InputHandler handler) {
        numberFormatter = new NumberFormatter();

        this.inputHandler = handler;

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
        fpsCounter.setPosition(Gdx.graphics.getWidth() / 30f, Gdx.graphics.getHeight() * .95f);
        fpsCounter.setVisible(true);

        memoryUsage = new Label("", fpsStyle);
        memoryUsage.setFontScale(0.6f);
        memoryUsage.setPosition(Gdx.graphics.getWidth() / 30f, Gdx.graphics.getHeight() * 0.93f);
        memoryUsage.setVisible(true);

        mouseCoords = new Label("", fpsStyle);
        mouseCoords.setFontScale(0.6f);
        mouseCoords.setPosition(Gdx.graphics.getWidth() / 30f, Gdx.graphics.getHeight() * .91f);
        mouseCoords.setVisible(true);

        itemOver = new Label("", fpsStyle);
        itemOver.setFontScale(0.6f);
        itemOver.setPosition(Gdx.graphics.getWidth() / 30f, Gdx.graphics.getHeight() * .89f);
        itemOver.setVisible(true);



        specialPoints = new Label("", fpsStyle);
        specialPoints.setScale(1f);
        specialPoints.setPosition(Gdx.graphics.getWidth() * .55f, Gdx.graphics.getHeight() * .98f);


        stage = new Stage(viewport, game.getSpriteBatch());
        nodeTable = new Table();
        int count = 0;

        inventoryWidget = new InventoryTable(player.getInventory());

        shopWidget = new ShopMenu(player.getInventory());
        handler.setShopMenu(shopWidget);

        oreInfo = new Label("", fpsStyle);
        oreInfo.setPosition(Gdx.graphics.getWidth() * .85f, Gdx.graphics.getHeight() * .93f);

//        do {
//            stopwatch.restart();
//                player.getInventory().sortByStored();
//                updateInventory(player.getInventory().getInventoryNodes());
//                player.getInventory().sortByType();
//                updateInventory(player.getInventory().getInventoryNodes());
//                player.getInventory().sortByName();
//                updateInventory(player.getInventory().getInventoryNodes());
//                player.getInventory().sortByTier();
//                updateInventory(player.getInventory().getInventoryNodes());
//            stopwatch.stop();
//            count++;
//        } while (stopwatch.getElapsedTime() > 20 && count <= 9_999);
        count = 0;
//        nodeTable.clear();
//        for (InventoryNode node : player.getInventory().getInventoryNodes()) {
//            nodeTable.add(new ItemIcon(node)).pad(50).align(Align.topLeft);
//            if (count++ >= 5) {
//                nodeTable.row();
//                count = 0;
//            }
//        }
//
//        nodeTable.setPosition(900f, 700f);
//        stage.addActor(nodeTable);
        inventoryWidget.setPosition(Gdx.graphics.getWidth() *.7f, Gdx.graphics.getHeight() * .4f);
        inventoryWidget.setVisible(false);

        shopWidget.setPosition(0, Gdx.graphics.getHeight() * .4f);
        shopWidget.setVisible(false);
        stage.addActor(oreInfo);
        stage.addActor(shopWidget);
        stage.addActor(inventoryWidget);
        stage.addActor(fpsCounter);
        stage.addActor(memoryUsage);
        stage.addActor(mouseCoords);
        stage.addActor(itemOver);
        stage.addActor(specialPoints);
        createWallet(fpsStyle);
        createActiveOre(fpsStyle);

    }

    public InventoryTable getInventoryTable() {
        return inventoryWidget;
    }

    public ProgressBar getOreLimit() {
        return oreLimit;
    }

    public void draw(float deltaT) {
        updateInterval += deltaT;
//        showInventory();
        if (updateInterval > 0.1f) {
            camera.update();
            fpsCounter.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
            if (ITEM_MAP.getItem(mouse) != null) {
                itemOver.setText("Item: " + ITEM_MAP.getItem(mouse).getName());
            } else {
                itemOver.setText("Item: " + null);
            }

            if(inputHandler.getCurrentMode() instanceof OreObserver mode) {
                oreInfo.setText("Ore: " + mode.getHighlightedOre().getName() + "\nOre Value: " + mode.getHighlightedOre().getOreValue() + "\nOre Temperature: " + mode.getHighlightedOre().getOreTemp());
            } else {
                oreInfo.setText("");
            }
//
            memoryUsage.setText(((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024) + " MB");
            this.mouseCoords.setText("X: " + (int) mouse.x + " Y: " + (int) mouse.y);
            wallet.setText("$ " + String.format("%.2e", player.getWallet()));
            specialPoints.setText("SP: " + player.getSpecialPoints());
            activeOre.setText("Active Ore: " + oreRealm.getActiveOre().size());
            updateInterval = 0f;
        }
        stage.act(deltaT);
        stage.draw();
    }

    private void createWallet(Label.LabelStyle style) {
        wallet = new Label("", style);
        wallet.setScale(1f);
        wallet.setPosition(Gdx.graphics.getWidth() * .45f, Gdx.graphics.getHeight() * .98f);
        wallet.setVisible(true);
        stage.addActor(wallet);
    }


    private void createActiveOre(Label.LabelStyle style) {
        activeOre = new Label("", style);
        activeOre.setScale(1f);
        activeOre.setPosition(Gdx.graphics.getWidth() * .3f, Gdx.graphics.getHeight() * .98f);
        activeOre.setVisible(true);
        stage.addActor(activeOre);
    }

    public void showInventory() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.INSERT)) {
            inventoryWidget.setVisible(!inventoryWidget.isVisible());
        }
        if (inventoryWidget.isVisible()) {
            Gdx.input.setInputProcessor(this.stage);
        } else {
            stage.setKeyboardFocus(null);
        }
    }



}
