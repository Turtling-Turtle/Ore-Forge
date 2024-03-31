package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Enums.Operator;
import ore.forge.Ore;

import java.util.function.Consumer;

//@author Nathan Ulmen
public class BasicUpgrade implements UpgradeStrategy {
    public enum ValueToModify {ORE_VALUE, TEMPERATURE, MULTIORE, SPEED}
    //More VTMS: effect Duration, Speed,
    private double modifier;
    private final ValueToModify valueToModify;
    private final Consumer<Ore> upgradeFunction;
    private final Operator operator;


    public BasicUpgrade(double mod, Operator operatorType, ValueToModify valueToModify) {
       this.operator = operatorType;
       modifier = mod;
       this.valueToModify = valueToModify;
       upgradeFunction = configureUpgradeFunction();
    }

    public BasicUpgrade(JsonValue jsonValue) {
       //TODO: Introduce error handling for jsonValues.
       modifier = jsonValue.getDouble("modifier");
       valueToModify = ValueToModify.valueOf(jsonValue.getString("valueToModify"));
       operator = Operator.valueOf(jsonValue.getString("operation"));
       upgradeFunction = configureUpgradeFunction();
    }

    @Override
    public void applyTo(Ore ore) {
       upgradeFunction.accept(ore);//applies the function to the ore
    }

    //Determines/sets the behavior of the upgradeFunction.
    private Consumer<Ore> configureUpgradeFunction() {
       return switch (valueToModify) {
            case ORE_VALUE -> (Ore ore) -> ore.setOreValue(operator.apply(ore.getOreValue(), modifier));
            case TEMPERATURE -> (Ore ore) -> ore.setTemp((float) Math.round(operator.apply(ore.getOreTemp(), modifier)));
            case MULTIORE -> (Ore ore) -> ore.setMultiOre((int) Math.round(operator.apply(ore.getOreTemp(), modifier)));
            case SPEED -> (Ore ore) -> ore.setSpeedScalar((float) operator.apply(ore.getSpeedScalar(), modifier));
       };
    }

    public void setModifier(double newVal) {
       modifier = newVal;
    }

    public double getModifier() {
       return modifier;
    }

    public String toString() {
        return "Type: " + getClass().getSimpleName() + "\tVTM: " + valueToModify + "\tOperator: " + operator + "\tModifier: " + modifier;
    }

}
