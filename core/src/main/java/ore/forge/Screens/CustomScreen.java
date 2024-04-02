package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ore.forge.OreForge;
import ore.forge.ResourceManager;


public abstract class CustomScreen implements Screen {

    protected final OreForge game;
    protected final ResourceManager resourceManager;
    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected Stage stage;

    public CustomScreen(final OreForge game, final ResourceManager resourceManager) {
        this.game = game;
        this.resourceManager = resourceManager;


        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);
        viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        stage = new Stage(viewport, game.getSpriteBatch());
    }


    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }
    @Override
    public void resize(int width, int height) {}

    @Override
    public void show() {
        game.memoryCounter.setPosition(100, 100);
        stage.addActor(game.memoryCounter);
        screenFadeIn(0.1f);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        screenFadeOut(0.1f);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
    public void addToStage(Actor actor) {
        stage.addActor(actor);
    }
    //Call on screen Show
    public void screenFadeIn(float fadeInTime) {
        stage.getRoot().getColor().a = 0f;
        AlphaAction fadeIn = Actions.fadeIn(fadeInTime);
        stage.getRoot().addAction(fadeIn);
    }
    //Call on screen Hide
    public void screenFadeOut(float fadeOutTime) {
        stage.getRoot().getColor().a = 1f;
        AlphaAction fadeOut = Actions.fadeOut(fadeOutTime);
        stage.getRoot().addAction(fadeOut);
    }

}
