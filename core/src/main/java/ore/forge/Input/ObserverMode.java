package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import ore.forge.ItemMap;

public class ObserverMode extends InputMode {
    protected static final ItemMap itemMap = ItemMap.getSingleton();

    public ObserverMode() {
    }

    @Override
    public void update(float delta, OrthographicCamera camera, InputHandler handler) {
        updateCameraZoom(delta, camera);
        updateCameraPosition(delta, camera);
        checkModes(handler);
    }

    @Override
    public void setActive(InputHandler context) {

    }

    public void checkModes(InputHandler handler) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            handler.pauseGame();
            return;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F1) || Gdx.input.isKeyJustPressed(Input.Keys.I)) {
           handler.setCurrentMode(handler.getInventoryMode());
           return;
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && handler.isCoordsValid() && itemMap.getBlock(handler.getMouseWorldX(), handler.getMouseWorldY()) != null) {
//            handler.setMode(InputHandler.Mode.SELECTING);
            handler.setHeldItem(itemMap.getItem(handler.getMouseWorld()));
            handler.setCurrentMode(handler.getSelectMode());
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            handler.setCurrentMode(handler.getOreObserver());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            var shop = handler.getShopMenu();
            if (shop.isVisible()) {
                shop.hide();
            } else {
                shop.show();
            }
//            handler.getShopMenu().setVisible(!handler.getShopMenu().isVisible());
        }
    }

    public String toString() {
        return "ObserverModeProcessor";
    }


}
