package ore.forge.EventSystem.Events;

import ore.forge.FontColors;

public interface Event<E> {
    Class<?> getEventType();

    E getSubject();

    String getBriefInfo();

    String getInDepthInfo();

    String eventName();

    FontColors getColor();
}
