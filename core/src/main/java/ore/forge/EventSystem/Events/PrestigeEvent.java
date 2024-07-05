package ore.forge.EventSystem.Events;

import ore.forge.FontColors;

public record PrestigeEvent(Object object) implements Event{

    @Override
    public Class getEventType() {
        return null;
    }

    @Override
    public Object getSubject() {
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
        return "Prestige";
    }

    @Override
    public FontColors getColor() {
        return FontColors.PALE_VIOLET_RED;
    }
}
