package ore.forge.EventSystem.Events;


import ore.forge.EventSystem.EventType;
import ore.forge.FontColors;
import ore.forge.Items.Dropper;
import ore.forge.Ore;

public record OreDroppedEvent(Ore ore, Dropper dropper) implements Event {

    @Override
    public EventType getType() {
        return EventType.ORE_DROPPED_EVENT;
    }

    @Override
    public String getBriefInfo() {
        return ore.getName() + " dropped by " + dropper.getName();
    }

    @Override
    public String getInDepthInfo() {
        return "Unimplemented";
    }

    @Override
    public String eventName() {
        return "Ore Dropped Event";
    }

    @Override
    public FontColors getColor() {
        return FontColors.TURQUOISE;
    }

}
