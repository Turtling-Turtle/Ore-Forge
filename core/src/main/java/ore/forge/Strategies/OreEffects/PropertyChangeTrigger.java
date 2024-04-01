package ore.forge.Strategies.OreEffects;

import ore.forge.Ore;

//@author Nathan Ulmen
public class PropertyChangeTrigger implements OreEffect {
    //what to do if true?
    //what to do if false?
    //what to evaluate (Currently only oreProperties, dont know about evaluating other effects besides Burning and Frostbite).
    //Comparison
    //Charges/number of times it will trigger.
    //Guard against infinite loops? (EX: don't want effects to trigger themselves over and over again.)


    public PropertyChangeTrigger() {

    }

    public PropertyChangeTrigger(PropertyChangeTrigger clone) {

    }


    @Override //might not need this?
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
