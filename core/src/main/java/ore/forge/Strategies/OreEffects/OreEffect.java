package ore.forge.Strategies.OreEffects;

import ore.forge.Ore;

@SuppressWarnings("unused")
public interface OreEffect {
    void activate(float deltaT, Ore ore);

    //Whenever an Ore Effect is applied to an ore the clone method is called.
    OreEffect cloneOreEffect();

}
