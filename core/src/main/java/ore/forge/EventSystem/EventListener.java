package ore.forge.EventSystem;


public interface EventListener<E> {
    void handle(E event);
    Class<?> getEventType();

}
