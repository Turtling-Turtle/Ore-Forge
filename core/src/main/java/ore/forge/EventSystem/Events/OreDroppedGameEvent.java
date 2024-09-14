package ore.forge.EventSystem.Events;


import ore.forge.FontColors;
import ore.forge.Items.Dropper;
import ore.forge.Ore;

public record OreDroppedGameEvent(Ore ore, Dropper dropper) implements GameEvent<Ore> {

    @Override
    public Class<?> getEventType() {
        return OreDroppedGameEvent.class;
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
