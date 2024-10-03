package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;


public class QuestMode extends InputMode {

    @Override
    public void update(float deltaTime, OrthographicCamera camera, InputHandler context) {
        if (Gdx.input.isKeyJustPressed(Keys.J) || Gdx.input.isKeyJustPressed(Keys.F3) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            context.getUserInterface().stage.setKeyboardFocus(null);
            context.getQuestMenu().hide();
            context.setCurrentMode(context.getObserverMode());
        }
    }

    @Override
    public void setActive(InputHandler context) {
        context.getQuestMenu().show();

        Gdx.input.setInputProcessor(context.getUserInterface().stage);
    }
}
