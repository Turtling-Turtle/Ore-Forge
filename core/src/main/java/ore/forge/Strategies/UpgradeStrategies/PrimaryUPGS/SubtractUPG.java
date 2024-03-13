package ore.forge.Strategies.UpgradeStrategies.PrimaryUPGS;


import ore.forge.Strategies.UpgradeStrategies.BasicUpgrade;
import ore.forge.Ore;

//Subtracts a modifier from a value.
public class SubtractUPG extends BasicUpgrade {

    public SubtractUPG(double mod, BasicUpgrade.ValueToModify val) {
        super(mod, val);
    }

    @Override
    public void applyTo(Ore ore) {
        switch (this.getValueToMod()) {
            case ORE_VALUE:
                ore.setOreValue(ore.getOreValue() - getModifier());
                break;
            case TEMPERATURE:
                ore.setTemp((int) (ore.getOreTemp() - getModifier()));
                break;
            case MULTIORE:
                ore.setMultiOre((int) (ore.getMultiOre() - getModifier()));
                break;
        }
    }

}
