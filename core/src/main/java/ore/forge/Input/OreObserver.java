package ore.forge.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import ore.forge.CoolDown;
import ore.forge.Ore;
import ore.forge.OreRealm;

public class OreObserver extends InputMode {
    private boolean isHeld;
    private int index;
    private final static OreRealm oreRealm = OreRealm.getSingleton();
    private Ore highlightedOre;
    private final CoolDown coolDown;

    public OreObserver() {
        coolDown = new CoolDown(.1f);
    }

    @Override
    public void update(float deltaTime, OrthographicCamera camera, InputHandler context) {
        updateCameraZoom(deltaTime, camera);
        if (!highlightedOre.isActive() || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            context.setCurrentMode(context.getObserverMode());
            return;
        }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                if (coolDown.update(deltaTime)) {
                    nextOre();
                }

            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                previousOre();
            }
        shiftCamera(deltaTime, camera);
    }

    @Override
    public void setActive(InputHandler context) {
        if (index < oreRealm.getActiveOre().size()) {
            highlightedOre = oreRealm.getActiveOre().get(index);
        } else {
            context.setCurrentMode(context.getObserverMode());
        }
    }

    public Ore getHighlightedOre() {
        return highlightedOre;
    }

    public void nextOre() {
        index++;
        if (index > oreRealm.getActiveOre().size() - 1) {
            index = 0;
        }
        highlightedOre = oreRealm.getActiveOre().get(index);
    }

    public void previousOre() {
        index--;
        if (index < 0) {
            index = oreRealm.getActiveOre().size() - 1;
        }
        highlightedOre = oreRealm.getActiveOre().get(index);
    }

    public void shiftCamera(float deltaTime, OrthographicCamera camera) {
        float speed = highlightedOre.getMoveSpeed() * 1.5f * deltaTime;
        camera.position.x = smoothMove(camera.position.x, highlightedOre.getVector().x, speed);
        camera.position.y = smoothMove(camera.position.y, highlightedOre.getVector().y, speed);
    }

    private float smoothMove(float cameraPos, float orePos, float speed) {
        if (Math.abs(cameraPos - orePos) < 0.1f) {
            return orePos;
        }

        return cameraPos + (orePos - cameraPos) * speed;
    }


}
