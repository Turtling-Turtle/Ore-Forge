package ore.forge.Strategies.OreStrategies;

import ore.forge.Ore;

public interface OreStrategy {
    void activate(float deltaT, Ore ore);

    OreStrategy clone();

    //Strategies, Burning, Frozen, Immunity to specific status effects, invincibility, radiation??, Upgrade over time effects/modify value over time,

}
