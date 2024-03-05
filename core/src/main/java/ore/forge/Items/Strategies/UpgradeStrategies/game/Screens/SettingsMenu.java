package ore.forge.game.Items.Strategies.UpgradeStrategies.game.Screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.ButtonHelper;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.ResourceManager;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.TheTycoonGame;

//TODO:
//SetResolution, Enable VSYNC, Show FPS, AntiAliasing?, Sound options for music and sfx.

public class SettingsMenu extends CustomScreen {
    // private ButtonHelper buttonHelper = new ButtonHelper();
    private Table table;
    private final CheckBox checkBox;
    private final CheckBox fpsCheckBox;

    private final TextButton backButton;
    private final Label vsync;
    private final Label fpsCounterLabel;

    public Screen previousScreen;


//    private MainMenuScreen menuScreen;
//    private SelectBox<String> selectBox = new SelectBox<>();


    public SettingsMenu(final TheTycoonGame game, final ResourceManager resourceManager) {
        super(game, resourceManager);
        //VSYNC TOGGLE
        checkBox = ButtonHelper.createCheckBox("", Color.BLACK, 128, 128);
        checkBox.setPosition(Gdx.graphics.getWidth()/3f, 1000);
        checkBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ButtonHelper.getButtonClickSound().play();
                if (checkBox.isChecked()){
                    Gdx.graphics.setVSync(true);
                    vsync.setText("Vsync: Enabled");
                    vsync.setColor(Color.GREEN);
                } else {
                    Gdx.graphics.setVSync(false);
                    vsync.setText("Vsync: Disabled");
                    vsync.setColor(Color.RED);
                }
            }
        });


        BitmapFont font2 = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
        Label.LabelStyle fpsStyle = new Label.LabelStyle(font2, Color.WHITE);

        //FPS COUNTER OPTION
        fpsCheckBox = ButtonHelper.createCheckBox("", Color.BLACK, 128, 128);
        fpsCheckBox.setPosition(Gdx.graphics.getWidth()/3f, 850);
        fpsCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ButtonHelper.getButtonClickSound().play();
                if (fpsCheckBox.isChecked()) {
                    game.fpsCounter.setVisible(true);
                    fpsCounterLabel.setText("FPS Counter: Enabled");
                    fpsCounterLabel.setColor(Color.GREEN);
                } else {
                    game.fpsCounter.setVisible(false);
                    fpsCounterLabel.setText("FPS Counter: Disabled");
                    fpsCounterLabel.setColor(Color.RED);
                }
            }
        });

        backButton = ButtonHelper.createRoundTextButton("<------", Color.RED, 256, 128);
        backButton.setPosition(0, Gdx.graphics.getHeight()/1.1f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ButtonHelper.getButtonClickSound().play();
                game.setScreen(previousScreen);
            }
        });


        //Vsync button description
        vsync = new Label("Vsync: Disabled", fpsStyle);
        vsync.setPosition(checkBox.getX()+checkBox.getWidth(), checkBox.getY()+checkBox.getHeight()*0.5f);
        vsync.setColor(Color.RED);
        //FpsButton Description
        fpsCounterLabel = new Label("FPS Counter: Disabled", fpsStyle);
        fpsCounterLabel.setPosition(fpsCheckBox.getX()+fpsCheckBox.getWidth(), fpsCheckBox.getY()+checkBox.getHeight()*0.5f);
        fpsCounterLabel.setColor(Color.RED);


//        SelectBox resolution = new SelectBox();
        //Add all elements to stage
        stage.addActor(vsync);
        stage.addActor(fpsCounterLabel);
        stage.addActor(backButton);
//        stage.addActor(game.fpsCounter);
        stage.addActor(checkBox);
        stage.addActor(fpsCheckBox);
    }

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        screenFadeIn(0.2f);
        Gdx.input.setInputProcessor(this.stage);
        game.fpsCounter.setPosition(100, 100);
        stage.addActor(game.fpsCounter);
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        if(Gdx.input.isKeyPressed(Input.Keys.FORWARD_DEL)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ButtonHelper.getButtonClickSound().play();
            game.setScreen(previousScreen);
        }
        stage.act(delta);
        stage.draw();
    }

    /**
     * @param width
     * @param height
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {
        screenFadeOut(0.1f);
    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }



}

