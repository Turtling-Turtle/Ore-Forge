package ore.forge.EventSystem;

public interface EventListener<E> {

    public void handle(E event);

}
