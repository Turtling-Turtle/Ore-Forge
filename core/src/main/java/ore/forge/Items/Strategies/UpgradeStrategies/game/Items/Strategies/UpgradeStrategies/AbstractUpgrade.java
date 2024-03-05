package ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies;

public abstract class AbstractUpgrade implements UpgradeStrategy {
   public enum ValueToModify {ORE_VALUE, TEMPERATURE, MULTIORE}
   private final double modifier;
   private final ValueToModify value;

   public AbstractUpgrade(double mod, ValueToModify val) {
      modifier = mod;
      value = val;
   }

   public double getModifier() {
       return modifier;
   }

   public ValueToModify getValueToMod() {
       return value;
   }

   public String toString() {
      return "VTM: " + value + "\tModifier: " + modifier;
   }

}
