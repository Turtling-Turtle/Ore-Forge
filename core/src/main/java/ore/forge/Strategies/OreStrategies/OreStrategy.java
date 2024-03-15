package ore.forge.Strategies.OreStrategies;

import ore.forge.Ore;

public interface OreStrategy {
    void activate(float deltaT, Ore ore);

    OreStrategy clone();

    //Strategies, Burning, Frozen, Immunity to specific status effects, invincibility, radiation??, Upgrade over time effects/modify value over time,

    //Burning:
    //Ore is lit on fire. While on fire its temperature increases by X. Destroyed after certain Time?

    //Frozen:
    //Ore is frozen. While frozen ore temperature decreases by X. Moves slower?

    //Immunity:
    //Immune to specific effects like fire or frozen. Charges???

    //Invincibility:
    //Before death/destruction from another effect or upgrade? will consume a charge to prevent death. X charges.
        //Maybe have a version where ore is invincible under specific conditions?

    //Over time effects that modify Ore Properties:
        //Implementation idea 1: Over time effects will take an upgrade effect. Upgrade effect will be activated on an interval, similar to how droppers produce ore.

    //Effect that checks to see if a specific property is modified.
        //EX: if Ore is upgraded by X upgrader do X.
        //Implementation Idea: Update on Interval? Every X seconds check to see if property is modified. If condition is true do X else do Y.

// ----------------------------------------------------------------------------------

    //Way Out there Ideas:

    //Radiation: Ore that is irradiated can spread it to other ore?? Contamination?
        //Incorporate a Decay element maybe?

    //Adaptability: Ore becomes can either become more resistant or receptive/susceptible to specific effects over time?

    //Linking two Ore together for bonus? doing one thing to the ore applies it to the other? similar to the court of Oryx encounter from destiny.

    //Auras? Ore that provides effects to ore within a specific radius of it.
        //EX: all ore within 4 units of this ore are immune to fire.
        //EX: all ore within 10 units of this ore have their value increase by 1.05x every 20 seconds.

}
