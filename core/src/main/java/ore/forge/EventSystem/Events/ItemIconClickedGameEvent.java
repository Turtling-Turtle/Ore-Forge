package ore.forge.EventSystem.Events;

import ore.forge.FontColors;
import ore.forge.Screens.Widgets.ItemIcon;

public record ItemIconClickedGameEvent(ItemIcon itemIcon) implements GameEvent {
    @Override
    public Class<?> getEventType() {
        return ItemIconClickedGameEvent.class;
    }

    @Override
    public Object getSubject() {
        return itemIcon;
    }

    @Override
    public String getBriefInfo() {
        return "Click on " + itemIcon.getName();
    }

    @Override
    public String getInDepthInfo() {
        return "";
    }

    @Override
    public String eventName() {
        return "Building with " + itemIcon.getName();
    }

    @Override
    public FontColors getColor() {
        return FontColors.GHOST_WHITE;
    }
}
