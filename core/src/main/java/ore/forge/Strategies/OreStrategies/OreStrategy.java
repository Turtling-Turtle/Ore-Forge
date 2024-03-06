package ore.forge.Strategies.OreStrategies;

import ore.forge.Ore;

public interface OreStrategy {
    void activate(float deltaT, Ore ore);

    OreStrategy clone();


}
