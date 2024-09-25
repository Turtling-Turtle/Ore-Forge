package ore.forge.Input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import ore.forge.ItemMap;
import ore.forge.Screens.InventoryTable;
import ore.forge.Screens.ShopMenu;

import static com.badlogic.gdx.Input.Keys.*;


public class DefaultState implements InputProcessor, GameplayController {
    private InputManager inputManager;
    private final static ItemMap ITEM_MAP = ItemMap.getSingleton();
    private boolean mouseHeld;
    private InventoryTable inventory;
    private ShopMenu shop;

    public DefaultState() {

    }

    @Override
    public void update(OrthographicCamera camera) {
        cameraController.updateCamera(camera);
        if (mouseHeld && inputManager.isCoordsValid() && ITEM_MAP.getBlock(inputManager.mouseWorld()) != null) {
            //Transition to Selecting Mode.

        }
    }

    @Override
    public boolean keyDown(int i) {
        if (inventory.isSearching() || shop.isSearching()) {
            return false;
        }
        return InputManager.handleKey(i, true, cameraController);
    }

    @Override
    public boolean keyUp(int i) {
        if (inventory.isSearching() || shop.isSearching()) {
            return false;
        } //return as disabled.

        boolean returnValue = switch (i) {
            case F1, I -> {
                inventory.show();
                yield true;
            }
            case F2, B, F -> {
                shop.show();
                yield true;
            }
            case F3, J -> {
                //Activate questTab
                yield true;
            }
            default -> false;
        };

        return InputManager.handleKey(i, false, cameraController) || returnValue;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        if (inventory.isVisible() || shop.isVisible()) {
            return false;
        }
        return switch (i) {
            case LEFT -> {
                mouseHeld = true;
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        if (inventory.isVisible() || shop.isVisible()) {
            return false;
        }
        return switch (i) {
            case LEFT -> {
                mouseHeld = false;
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }


}
