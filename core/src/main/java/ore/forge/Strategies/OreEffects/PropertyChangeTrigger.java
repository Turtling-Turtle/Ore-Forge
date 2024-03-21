package ore.forge.Strategies.OreEffects;

import ore.forge.Ore;

//@author Nathan Ulmen
public class PropertyChangeTrigger implements OreEffect {
    //what to do if true.
    //what to do if false
    //what to evaluate
    //Comparison
    //


    public PropertyChangeTrigger() {

    }

    public PropertyChangeTrigger(PropertyChangeTrigger clone) {

    }


    @Override
    public void activate(float deltaT, Ore ore) {

    }

    @Override
    public OreEffect clone() {
        try {
            return (OreEffect) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEndStepEffect() {
        return false;
    }
}
