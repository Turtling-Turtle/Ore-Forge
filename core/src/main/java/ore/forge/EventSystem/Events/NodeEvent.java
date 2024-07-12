package ore.forge.EventSystem.Events;

import ore.forge.FontColors;
import ore.forge.Player.InventoryNode;

public record NodeEvent(InventoryNode node) implements Event<InventoryNode>{
    @Override
    public Class<?> getEventType() {
        return NodeEvent.class;
    }

    @Override
    public InventoryNode getSubject() {
        return node;
    }

    @Override
    public String getBriefInfo() {
        return node.getName() + " updated.";
    }

    @Override
    public String getInDepthInfo() {
        return "";
    }

    @Override
    public String eventName() {
        return "Inventory Node Event";
    }

    @Override
    public FontColors getColor() {
        return FontColors.YELLOW;
    }
}
