package ore.forge.EventSystem.Events;

import ore.forge.FontColors;
import ore.forge.Player.InventoryNode;

public record NodeGameEvent(InventoryNode node) implements GameEvent<InventoryNode> {
    @Override
    public Class<?> getEventType() {
        return NodeGameEvent.class;
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
