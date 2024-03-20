package ore.forge.Strategies.UpgradeStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;

import java.util.function.DoubleBinaryOperator;

public class BasicUpgrade implements UpgradeStrategy {
   public enum ValueToModify {ORE_VALUE, TEMPERATURE, MULTIORE}
    //More VTMS: effect Duration, Speed,
   public enum Operation {ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO}
   private double modifier;
   private ValueToModify valueToModify;
   private final DoubleBinaryOperator operation;
   private final Operation operator;


   public BasicUpgrade(double mod, Operation operationType, ValueToModify valueToModify) {
       operator = operationType;
       modifier = mod;
       operation = switch (operationType) {
           case ADD -> (x,y) -> x + y;
           case SUBTRACT -> (x,y) -> x - y;
           case MULTIPLY -> (x,y) -> x * y;
           case DIVIDE -> (x,y) -> x / y;
           case MODULO -> (x, y) -> x % y;
       };
       this.valueToModify = valueToModify;
   }


    public BasicUpgrade(JsonValue jsonValue) {
       modifier = jsonValue.getDouble("modifier");
       valueToModify = ValueToModify.valueOf(jsonValue.getString("valueToModify"));
       operator = Operation.valueOf(jsonValue.getString("operation"));
       operation = switch (Operation.valueOf(jsonValue.getString("operation"))) {
           case ADD -> (x, y) -> x + y;
           case SUBTRACT -> (x, y) -> x - y;
           case MULTIPLY -> (x, y) -> x * y;
           case DIVIDE -> (x, y) -> x / y;
           case MODULO -> (x, y) -> x % y;
       };
    }

    @Override
    public void applyTo(Ore ore) {
        switch (valueToModify) {
            case ORE_VALUE -> ore.setOreValue(operation.applyAsDouble(ore.getOreValue(), modifier));
            case TEMPERATURE -> ore.setTemp((float) operation.applyAsDouble(ore.getOreTemp(), modifier));
            case MULTIORE -> ore.setMultiOre((int) operation.applyAsDouble(ore.getMultiOre(), modifier));
        }
    }

    public void setModifier(double newVal) {
       modifier = newVal;
    }

   public void setValueToModify(ValueToModify vtm) {
       valueToModify = vtm;
   }


   public double getModifier() {
       return modifier;
   }

   public ValueToModify getValueToMod() {
       return valueToModify;
   }

   public String toString() {
      return "Type: " + getClass().getSimpleName() + "\tVTM: " + valueToModify + "\tOperator: " + operator + "\tModifier: " + modifier;
   }

}
