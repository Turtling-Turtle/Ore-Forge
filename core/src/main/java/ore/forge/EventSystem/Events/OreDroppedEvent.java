package ore.forge.EventSystem.Events;


import ore.forge.FontColors;
import ore.forge.Items.Dropper;
import ore.forge.Ore;

public record OreDroppedEvent(Ore ore, Dropper dropper) implements Event<Ore> {

    @Override
    public Class<?> getEventType() {
        return OreDroppedEvent.class;
    }

    @Override
    public Ore getSubject() {
        return ore;
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
