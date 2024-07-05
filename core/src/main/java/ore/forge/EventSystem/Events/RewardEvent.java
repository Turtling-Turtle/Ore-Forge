package ore.forge.EventSystem.Events;

import ore.forge.FontColors;
import ore.forge.Items.Item;

public record RewardEvent(Item reward, int count) implements Event{

    @Override
    public Class getEventType() {
        return RewardEvent.class;
    }

    @Override
    public Object getSubject() {
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
