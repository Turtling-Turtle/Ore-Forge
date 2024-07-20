package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ore.forge.ButtonHelper;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.PrestigeEvent;
import ore.forge.Player.Player;

public class PrestigeMenu extends Table {
    private final static Skin buttonAtlas = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/UIButtons.atlas")));
    private static final String roundFull = "128xRoundFull";

    private final TextButton prestigeButton;

    public PrestigeMenu() {
        prestigeButton = ButtonHelper.createRoundTextButton("Prestige", Color.SKY);
        prestigeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EventManager.getSingleton().notifyListeners(new PrestigeEvent(Player.getSingleton().canPrestige()));
            }

        });

        this.add(prestigeButton);


    }


}
