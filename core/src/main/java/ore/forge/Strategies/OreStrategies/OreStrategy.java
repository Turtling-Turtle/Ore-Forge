package ore.forge.Strategies.OreStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;

public interface OreStrategy {
    void activate(float deltaT, Ore ore);

    OreStrategy clone();

    boolean isEndStepEffect();

    //Strategies, Burning, Frozen, Immunity to specific status effects, invincibility, radiation??, Upgrade over time effects/modify value over time,

    //Burning:
    //Ore is lit on fire. While on fire its temperature increases by X. Destroyed after certain Time?

    //Frozen:
    //Ore is frozen. While frozen ore temperature decreases by X. Moves slower?

    //Ore can only be cannot be burning and frozen at the same time.

    //Immunity:
    //Immune to specific effects like fire or frozen. Charges???

    //Invincibility:
    //Before death/destruction from another effect or upgrade? will consume a charge to prevent death. X charges.
        //Maybe have a version where ore is invincible under specific conditions?

    //Over time effects that modify Ore Properties:
        //Implementation idea 1: Over time effects will take an upgrade effect. Upgrade effect will be activated on an interval, similar to how droppers produce ore.

    //EFFECT THAT CHECKS TO SEE IF A SPECIFIC PROPERTY IS MODIFIED.
        //EX: IF ORE IS UPGRADED BY X UPGRADER DO X.
        //IMPLEMENTATION IDEA: UPDATE ON INTERVAL? EVERY X SECONDS CHECK TO SEE IF PROPERTY IS MODIFIED. IF CONDITION IS TRUE DO X ELSE DO Y.

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
