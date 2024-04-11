package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Enums.Operator;
import ore.forge.Enums.OreProperty;
import ore.forge.Ore;

import java.util.function.Consumer;

//@author Nathan Ulmen
public class BasicUpgrade implements UpgradeStrategy {
    //More VTMS: effect, Duration.
    private double modifier;
    private final OreProperty valueToModify;
    private final Consumer<Ore> upgradeFunction;
    private final Operator operator;


    public BasicUpgrade(double mod, Operator operatorType, OreProperty valueToModify) {
        this.operator = operatorType;
        this.modifier = mod;
        this.valueToModify = valueToModify;
        upgradeFunction = configureUpgradeFunction();
    }

    public BasicUpgrade(JsonValue jsonValue) {
        //TODO: Introduce error handling for jsonValues.
        try {
            modifier = jsonValue.getDouble("modifier");
        } catch (IllegalArgumentException e) {
            modifier = 1;
        }
        valueToModify = OreProperty.valueOf(jsonValue.getString("valueToModify"));
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
            case UPGRADE_COUNT -> throw new RuntimeException("Upgrade Count is not a valid value to Modify.");
        };
    }

    public void setModifier(double newVal) {
        modifier = newVal;
    }

    public double getModifier() {
        return modifier;
    }

    public Operator getOperator() {
        return operator;
    }

    public OreProperty getValueToModify() {
        return valueToModify;
    }

    public String toString() {
        return "[" + getClass().getSimpleName() + "]" +
            "\tValue To Modify: " + valueToModify +
            ", Operator: " + operator +
            ", Modifier: " + modifier;
    }

}
