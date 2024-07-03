package ore.forge.EventSystem.Events;

import ore.forge.EventSystem.EventType;
import ore.forge.FontColors;
import ore.forge.Items.Item;

public record RewardEvent(Item reward, int count) implements Event{

    @Override
    public EventType getType() {
        return null;
    }

    @Override
    public String getBriefInfo() {
        return "Unimplemented REWARD Event";
    }

    @Override
    public String getInDepthInfo() {
        return "Unimplemented Reward Event";
    }

    @Override
    public String eventName() {
        return "Reward";
    }

    @Override
    public FontColors getColor() {
        return null;
    }
}
