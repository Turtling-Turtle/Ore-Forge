package ore.forge.EventSystem.Events;

import ore.forge.FontColors;
import ore.forge.Items.Item;

public record ObtainedGameEvent(Item item, int count) implements GameEvent {

    @Override
    public Class getEventType() {
        return ObtainedGameEvent.class;
    }

    @Override
    public Object getSubject() {
        return null;
    }

    @Override
    public String getBriefInfo() {
        return "Obtained " + count + " " + item.getName();
    }

    @Override
    public String getInDepthInfo() {
        return "";
    }

    @Override
    public String eventName() {
        return "Item Obtained";
    }

    @Override
    public FontColors getColor() {
        return FontColors.PALE_TURQUOISE;
    }
}
