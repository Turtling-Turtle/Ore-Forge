package ore.forge.EventSystem.Events;

import ore.forge.FontColors;
import ore.forge.Items.Item;

public record ItemPlacedEvent(Item item) implements Event {

    @Override
    public Class getEventType() {
        return ItemPlacedEvent.class;
    }

    @Override
    public Object getSubject() {
        return Item.class;
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
