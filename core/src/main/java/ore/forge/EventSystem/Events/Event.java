package ore.forge.EventSystem.Events;

import ore.forge.EventSystem.EventType;
import ore.forge.FontColors;

public interface Event {
    EventType getType();

    String getBriefInfo();

    String getInDepthInfo();

    String eventName();

    FontColors getColor();
}
