package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import ore.forge.Screens.ItemIcon;
import ore.forge.Screens.UserInterface;

public class InventoryMode extends InputMode {
    private UserInterface userInterface;
    private final InputHandler handler;
    private boolean isKeyboardActive;

    public InventoryMode(InputHandler handler) {
        this.handler = handler;
        isKeyboardActive = false;
    }

    @Override
    public void update(float deltaTime, OrthographicCamera camera, InputHandler context) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            userInterface.stage.setKeyboardFocus(null);
            userInterface.getInventoryTable().setVisible(false);
            context.setCurrentMode(context.getObserverMode());
        }
    }

    @Override
    public void setActive(InputHandler context) {
        userInterface.getInventoryTable().setVisible(true);
        Gdx.input.setInputProcessor(userInterface.stage);
    }

    public void setUserInterface(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public void handleClicked(ItemIcon icon) {
        Gdx.app.log("InventoryModeProcessor", icon.toString());
        handler.setHeldItem(icon.getNode().getHeldItem());
        handler.setInventoryNode(icon.getNode());
        if (handler.getHeldNode().getStored() > 0) {
            userInterface.getInventoryTable().setVisible(false);
            userInterface.stage.setKeyboardFocus(null);
            handler.setCurrentMode(handler.getBuildMode());
        }
    }

    public String toString() {
        return "InventoryModeProcessor";
    }



}
