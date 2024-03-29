package ore.forge.Strategies.DropperStrategies;

import ore.forge.Items.Dropper;
import ore.forge.Ore;

public class Influenced implements DropperStrategy{

    public Influenced() {

    }

    @Override
    public Ore applyTo(Ore ore, Dropper parentDropper) {
        ore.setOreValue(parentDropper.getOreValue() * parentDropper.getTotalOreDropped());//Dropped Ore have their value increased based on totalOreDropped
        return ore;
    }






}
