package ore.forge.game.Items.Strategies.UpgradeStrategies.game.Screens;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class CustomPopUpMenu extends Table {
    final float ANIMATION_TIME = 7;

    public abstract void create();

    public void slideAnimation(int finalX, int finalY) {
        this.addAction(Actions.moveTo(finalX, finalY, ANIMATION_TIME));
    }

}
