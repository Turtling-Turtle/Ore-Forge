package ore.forge.EventSystem.Events;

public record PrestigeEvent(Object object) implements Event{

    @Override
    public Object getSubject() {
        return null;
    }
}
