package ore.forge.Strategies.OreStrategies;

import ore.forge.Ore;

public abstract class OreEffect implements OreStrategy{

    @Override
    public void activate(float deltaT, Ore ore) {

    }

    @Override
    public OreEffect clone() {
        try {
            return (OreEffect) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
