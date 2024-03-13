package ore.forge.Strategies.UpgradeStrategies;

public abstract class BasicUpgrade implements UpgradeStrategy {
   public enum ValueToModify {ORE_VALUE, TEMPERATURE, MULTIORE}
   private double modifier;
   private ValueToModify value;

   public BasicUpgrade(double mod, ValueToModify val) {
      modifier = mod;
      value = val;
   }

   public void setModifier(double newVal) {
       modifier = newVal;
   }

   public void setValueToModify(ValueToModify vtm) {
       value = vtm;
   }


   public double getModifier() {
       return modifier;
   }

   public ValueToModify getValueToMod() {
       return value;
   }

   public String toString() {
      return "Type: " + getClass().getSimpleName() + "\tVTM: " + value + "\tModifier: " + modifier;
   }

}
