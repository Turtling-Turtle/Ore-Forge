package ore.forge.engine;

public interface RenderThreadDispatcher {

    void post(Runnable runnable);

    boolean isRenderThread();
    
}
