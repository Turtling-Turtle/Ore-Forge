package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.*;
import ore.forge.*;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.PrestigeGameEvent;
import ore.forge.Input.InputHandler;
import ore.forge.Input.OreObserver;
import ore.forge.Player.Player;
import ore.forge.QuestComponents.QuestManager;
import ore.forge.Screens.Widgets.QuestIcon;


/**
 * @author Nathan Ulmen
 */
public class UserInterface {
    private static final OreRealm oreRealm = OreRealm.getSingleton();
    private static final ItemMap ITEM_MAP = ItemMap.getSingleton();
    private float updateInterval = 0;
    private final Runtime runtime = Runtime.getRuntime();
    private static final Player player = Player.getSingleton();
    public final Stage stage;
    private final Label fpsCounter;
    private Label wallet;
    private final Label memoryUsage;
    private final Label specialPoints;
    private final Label mouseCoords;
    private Label activeOre;
    private final Label itemOver;
    private final InventoryTable inventoryWidget;
    private final ShopMenu shopWidget;
    private InputHandler inputHandler;
    private final Label oreInfo;
    private final ScreenViewport uiViewport;
    private final QuestManager questManager;

    public UserInterface(OreForge game, InputHandler handler, QuestManager questManager) {
        this.questManager = questManager;

        this.inputHandler = handler;

        uiViewport = new ScreenViewport();
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
//        Skin skin = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/UIButtons.atlas")));
//        style.knobBefore = skin.getDrawable("128xVeryRoundFull");
//        style.background = skin.getDrawable("128xCircleEmpty");
//        progressBar = new ProgressBar(0, 2500f, 1, false, style);

//        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        camera.setToOrtho(false);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/ebrimabd.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 40;
        param.genMipMaps = true;
        param.minFilter = Texture.TextureFilter.MipMapLinearNearest;
        param.magFilter = Texture.TextureFilter.MipMapLinearNearest;


        BitmapFont font2 = generator.generateFont(param);
        font2.getData().markupEnabled = true;

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

//        stage = new Stage(uiViewport, game.getSpriteBatch());
        stage = new Stage(uiViewport);
        int count = 0;

        inventoryWidget = new InventoryTable(player.getInventory());

        shopWidget = new ShopMenu(player.getInventory());
        handler.setUserInterface(this);

        oreInfo = new Label("", fpsStyle);
        oreInfo.setPosition(Gdx.graphics.getWidth() * .85f, Gdx.graphics.getHeight() * .93f);


        EventLogger eventLogger = new EventLogger();
        eventLogger.setPosition(Gdx.graphics.getWidth() * 0.01f, Gdx.graphics.getHeight() * .01f);
        EventManager.getSingleton().setEventLogger(eventLogger);


        var prestigeButton = ButtonHelper.createRoundTextButton("Prestige", Color.SKY);
        prestigeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EventManager.getSingleton().notifyListeners(new PrestigeGameEvent(Player.getSingleton().canPrestige()));
            }

        });
        prestigeButton.setPosition(Gdx.graphics.getWidth() * 0.9f, Gdx.graphics.getHeight() * .8f);
        prestigeButton.setSize(Gdx.graphics.getWidth() * 0.08f, Gdx.graphics.getHeight() * 0.04f);


        inventoryWidget.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * .1f);
        inventoryWidget.setVisible(false);

        shopWidget.setPosition(0, Gdx.graphics.getHeight() * .1f);
        shopWidget.setVisible(false);
        stage.addActor(eventLogger);
        stage.addActor(oreInfo);
        stage.addActor(shopWidget);
        stage.addActor(inventoryWidget);
        stage.addActor(fpsCounter);
        stage.addActor(memoryUsage);
        stage.addActor(mouseCoords);
        stage.addActor(itemOver);
        stage.addActor(specialPoints);
        stage.addActor(prestigeButton);
//        stage.addActor(progressBar);
        createWallet(fpsStyle);
//        fpsStyle.fontColor = Color.WHITE;
        createActiveOre(fpsStyle);
        stage.addActor(new QuestIcon(questManager.getQuest("Test Quest 1.0")));
//        createActiveOreProgressBar();
    }

    public InventoryTable getInventoryTable() {
        return inventoryWidget;
    }

    public ShopMenu getShopUI() {
        return shopWidget;
    }


    public void draw(OrthographicCamera camera, SpriteBatch batch, float deltaT) {
        updateInterval += deltaT;
        uiViewport.apply();
//        progressBar.setValue(OreRealm.getSingleton().getActiveOre().size());

//        showInventory();

        if (updateInterval > 0.1f) {
            camera.update();
            fpsCounter.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
            if (ITEM_MAP.getItem(inputHandler.getMouseWorld()) != null) {
                itemOver.setText("Item: " + ITEM_MAP.getItem(inputHandler.getMouseWorld()).getName());
            } else {
                itemOver.setText("Item: " + null);
            }

            if (inputHandler.getCurrentMode() instanceof OreObserver mode) {
                oreInfo.setText("Ore: " + mode.getHighlightedOre().getName() + "\nOre Value: " + mode.getHighlightedOre().getOreValue() + "\nOre Temperature: " + mode.getHighlightedOre().getOreTemp());
            } else {
                oreInfo.setText("");
            }
//
            memoryUsage.setText(((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024) + " MB");
            this.mouseCoords.setText("X: " + (int) inputHandler.getMouseWorld().x + " Y: " + (int) inputHandler.getMouseWorld().y);
            wallet.setText(FontColors.highlightString("$ " + String.format("%.2e", player.getWallet()), FontColors.LIME_GREEN));
            specialPoints.setText(FontColors.highlightString("SP: " + player.getSpecialPoints(), FontColors.CORAL));
            activeOre.setText(FontColors.highlightString("Active Ore: " + oreRealm.getActiveOre().size(), FontColors.SANDY_BROWN));
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

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

//    private void createActiveOreProgressBar() {
//        Table table = new Table();
//        Stack activeOreStack = new Stack();
//        progressBar.setSize(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.15f);
//        progressBar.setDebug(true);
//        table.setPosition(Gdx.graphics.getWidth() * .3f, Gdx.graphics.getHeight() * .97f);
//        activeOreStack.add(progressBar);
//        activeOreStack.add(activeOre);
////        activeOreStack.setDebug(true);
//
//        table.add(activeOreStack);
//
//        stage.addActor(table);
//    }

    private void createInventoryButton() {
        TextButton inventory = ButtonHelper.createRoundTextButton("Inventory", Color.LIGHT_GRAY);
        inventory.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inputHandler.setCurrentMode(inputHandler.getInventoryMode());
                inventory.setVisible(false);
            }
        });
    }

}
