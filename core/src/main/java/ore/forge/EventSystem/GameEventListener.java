package ore.forge.EventSystem;


public interface GameEventListener<E> {
    void handle(E event);

    Class<?> getEventType();

}
