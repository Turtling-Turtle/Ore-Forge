package ore.forge.EventSystem.Events;

import ore.forge.EventSystem.EventType;
import ore.forge.FontColors;
import ore.forge.Items.Item;

public record ObtainedEvent(Item item, int count) implements Event {
    @Override
    public EventType getType() {
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
