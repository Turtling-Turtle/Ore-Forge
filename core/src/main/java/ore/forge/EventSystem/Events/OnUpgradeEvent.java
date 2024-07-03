package ore.forge.EventSystem.Events;


import ore.forge.EventSystem.EventType;
import ore.forge.FontColors;

public record OnUpgradeEvent() implements Event{

    @Override
    public EventType getType() {
        return null;
    }

    @Override
    public String getBriefInfo() {
        return "";
    }

    @Override
    public String getInDepthInfo() {
        return "";
    }

    @Override
    public String eventName() {
        return "";
    }

    @Override
    public FontColors getColor() {
        return null;
    }
}
