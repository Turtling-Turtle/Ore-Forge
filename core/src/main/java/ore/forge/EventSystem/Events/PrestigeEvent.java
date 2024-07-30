package ore.forge.EventSystem.Events;

import ore.forge.FontColors;
import ore.forge.Player.Player;

public record PrestigeEvent(Boolean result) implements Event<Boolean> {

    @Override
    public Class<?> getEventType() {
        return PrestigeEvent.class;
    }

    @Override
    public Boolean getSubject() {
        return result;
    }

    @Override
    public String getBriefInfo() {
        if (result) {
            return "Successfully prestiged to level " + (Player.getSingleton().getPrestigeLevel() + 1);
        }
        return "Failed to prestige.";
    }

    @Override
    public String getInDepthInfo() {
        return "";
    }

    @Override
    public String eventName() {
        return result ? "Successful Prestige" : "Failed Prestige";
    }

    @Override
    public FontColors getColor() {
        return result ? FontColors.SKY_BLUE : FontColors.RED;
    }
}
