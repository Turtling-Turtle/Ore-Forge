package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import ore.forge.Screens.ItemIcon;
import ore.forge.Screens.UserInterface;

public class InventoryMode extends InputMode {
    private final InputHandler handler;

    public InventoryMode(InputHandler handler) {
        this.handler = handler;
    }

    @Override
    public void update(float deltaTime, OrthographicCamera camera, InputHandler context) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            handler.getUserInterface().stage.setKeyboardFocus(null);
//            userInterface.getInventoryTable().setVisible(false);
            handler.getInventoryUI().hide();
            context.setCurrentMode(context.getObserverMode());
        }
    }

    @Override
    public void setActive(InputHandler context) {
        handler.getInventoryUI().show();
//        userInterface.getInventoryTable().setVisible(true);
        Gdx.input.setInputProcessor(handler.getUserInterface().stage);
    }

    public void handleClicked(ItemIcon icon) {
        Gdx.app.log("InventoryModeProcessor", icon.toString());
        handler.setHeldItem(icon.getNode().getHeldItem());
        handler.setInventoryNode(icon.getNode());
        if (handler.getHeldNode().getStored() > 0) {
            handler.getInventoryUI().hide();
//            userInterface.getInventoryTable().setVisible(false);
            handler.getUserInterface().stage.setKeyboardFocus(null);
            handler.setCurrentMode(handler.getBuildMode());
        }
    }

    public String toString() {
        return "InventoryModeProcessor";
    }



}
