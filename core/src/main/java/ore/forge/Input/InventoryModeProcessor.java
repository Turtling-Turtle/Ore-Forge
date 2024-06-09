package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import ore.forge.Player.InventoryNode;
import ore.forge.Screens.ItemIcon;
import ore.forge.Screens.UserInterface;

public class InventoryModeProcessor implements InputMode {
    private final InputHandler handler;
    private final Vector3 mouseWorld, mouseScreen;
    private InventoryNode selectedNode;
    private UserInterface userInterface;

    public InventoryModeProcessor(InputHandler handler) {
        this.handler = handler;
        mouseWorld = handler.mouseWorld;
        mouseScreen = handler.mouseScreen;
    }

    @Override
    public void update(float delta, OrthographicCamera camera) {
        if (Gdx.input.getInputProcessor() != userInterface.stage) {
            Gdx.input.setInputProcessor(userInterface.stage);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            handler.exitMode();
            userInterface.getInventoryTable().setVisible(false);
            return;
        }
        updateMouse(camera);
    }

    public InventoryNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(InventoryNode selectedNode) {
        System.out.println(selectedNode);
        this.selectedNode = selectedNode;
    }

    public void setUserInterface(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public void handleClicked(ItemIcon icon) {
        Gdx.app.log("InventoryModeProcessor", icon.toString());
        setSelectedNode(icon.getNode());
        if (selectedNode.getStored() > 0) {
            userInterface.getInventoryTable().setVisible(false);
            handler.setMode(InputHandler.Mode.BUILDING);
        }
    }

    private void updateMouse(OrthographicCamera camera) {
        mouseScreen.x = Gdx.input.getX();
        mouseScreen.y = Gdx.input.getY();
        mouseWorld.set(camera.unproject(mouseScreen));
    }

    public void activate() {
        userInterface.getInventoryTable().setVisible(true);
    }

    public String toString() {
        return "InventoryModeProcessor";
    }



}
