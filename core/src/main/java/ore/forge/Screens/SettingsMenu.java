package ore.forge.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ore.forge.ButtonHelper;
import ore.forge.ItemManager;
import ore.forge.OreForge;

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


    public SettingsMenu(final OreForge game, final ItemManager itemManager) {
        super(game, itemManager);
        //VSYNC TOGGLE
        checkBox = ButtonHelper.createCheckBox("", Color.BLACK, Gdx.graphics.getWidth() / 10f, Gdx.graphics.getWidth() / 10f);
        checkBox.setPosition(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 1.5f);
        checkBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ButtonHelper.getButtonClickSound().play();
                if (checkBox.isChecked()) {
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
        fpsCheckBox = ButtonHelper.createCheckBox("", Color.BLACK, Gdx.graphics.getWidth() / 10f, Gdx.graphics.getHeight() / 10f);
        fpsCheckBox.setPosition(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 2f);
        fpsCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ButtonHelper.getButtonClickSound().play();
                if (fpsCheckBox.isChecked()) {
                    game.memoryCounter.setVisible(true);
                    fpsCounterLabel.setText("FPS Counter: Enabled");
                    fpsCounterLabel.setColor(Color.GREEN);
                } else {
                    game.memoryCounter.setVisible(false);
                    fpsCounterLabel.setText("FPS Counter: Disabled");
                    fpsCounterLabel.setColor(Color.RED);
                }
            }
        });

        backButton = ButtonHelper.createRoundTextButton("<------", Color.RED, Gdx.graphics.getWidth() / 10f, Gdx.graphics.getHeight() / 10f);
        backButton.setPosition(0, Gdx.graphics.getHeight() * .9f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ButtonHelper.getButtonClickSound().play();
                game.setScreen(previousScreen);
            }
        });


        //Vsync button description
        vsync = new Label("Vsync: Disabled", fpsStyle);
        vsync.setPosition(checkBox.getX() + checkBox.getWidth(), checkBox.getY() + checkBox.getHeight() * 0.5f);
        vsync.setColor(Color.RED);
        //FpsButton Description
        fpsCounterLabel = new Label("FPS Counter: Disabled", fpsStyle);
        fpsCounterLabel.setPosition(fpsCheckBox.getX() + fpsCheckBox.getWidth(), fpsCheckBox.getY() + fpsCheckBox.getHeight() * 0.5f);
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
        game.memoryCounter.setPosition(100, 100);
        stage.addActor(game.memoryCounter);
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
        if (Gdx.input.isKeyPressed(Input.Keys.FORWARD_DEL)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ButtonHelper.getButtonClickSound().play();
            game.setScreen(previousScreen);
        }
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

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

