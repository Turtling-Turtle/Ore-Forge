package ore.forge.Strategies.OreStrategies;


import ore.forge.Ore;

public class OnUpgrade implements OreStrategy, Cloneable{
    private float modifier;
    private float interval;
    private float current;

    public OnUpgrade(float modifier, float interval ) {
        this.modifier = modifier;
        this.interval = interval;
        current = 0f;
    }

    @Override
    public void activate(float deltaT, Ore ore) {
        current += deltaT;
        if (current >= interval) {
            ore.setOreValue(ore.getOreValue() * modifier);
            current = 0f;
        }
    }

    @Override
    public OreStrategy clone() {
        try {
            return (OreStrategy) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
