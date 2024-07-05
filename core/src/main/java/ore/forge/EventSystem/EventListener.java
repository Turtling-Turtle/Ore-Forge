package ore.forge.EventSystem;


import ore.forge.EventSystem.Events.Event;

public interface EventListener<E> {

    void handle(E event);

}
