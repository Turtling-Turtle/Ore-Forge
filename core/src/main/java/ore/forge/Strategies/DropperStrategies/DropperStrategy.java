package ore.forge.Strategies.DropperStrategies;


import ore.forge.Items.Dropper;
import ore.forge.Ore;

@SuppressWarnings("unused")
public interface DropperStrategy {
    Ore applyTo(Ore ore, Dropper parentItem);
}
