package ore.forge.EventSystem.Events;

import ore.forge.EventSystem.EventType;
import ore.forge.FontColors;
import ore.forge.Items.Item;

public record ItemPlacedEvent(Item item) implements Event {
    @Override
    public EventType getType() {
        return null;
    }

    @Override
    public String getBriefInfo() {
        return item.getName() + " placed at " + item.getVector2();
    }

    @Override
    public String getInDepthInfo() {
        return "";
    }

    @Override
    public String eventName() {
        return "Item Placed";
    }

    @Override
    public FontColors getColor() {
        return FontColors.CHOCOLATE;
    }
}
