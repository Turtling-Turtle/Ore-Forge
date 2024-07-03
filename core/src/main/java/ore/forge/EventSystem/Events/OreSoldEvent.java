package ore.forge.EventSystem.Events;


import ore.forge.EventSystem.EventType;
import ore.forge.FontColors;
import ore.forge.Items.Furnace;
import ore.forge.Ore;

public record OreSoldEvent(Ore ore, Furnace item) implements Event {


    @Override
    public EventType getType() {
        return null;
    }

    @Override
    public String getBriefInfo() {
        return ore.getName() + " sold by " + item.getName();
    }

    @Override
    public String getInDepthInfo() {
        var info = "";
        info += " Name: " + ore.getName() + " Value: " + ore.getOreValue() + " Temperature: " + ore.getOreTemp() + " Multiore: " + ore.getMultiOre();
        info += "\nSold By: " + item.getName();
        return info;
    }

    @Override
    public String eventName() {
        return "Ore Sold";
    }

    @Override
    public FontColors getColor() {
        return FontColors.DARK_SEA_GREEN;
    }
}
