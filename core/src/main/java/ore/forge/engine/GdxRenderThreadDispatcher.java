package ore.forge.engine;

import com.badlogic.gdx.Gdx;

public class GdxRenderThreadDispatcher implements RenderThreadDispatcher {

    @Override
    public void post(Runnable runnable) {
        Gdx.app.postRunnable(runnable);
    }

    @Override
    public boolean isRenderThread() {
        return true;
    }
    
}
