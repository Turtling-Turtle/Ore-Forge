package ore.forge.Strategies.UpgradeStrategies.PrimaryUPGS;


import ore.forge.Strategies.UpgradeStrategies.AbstractUpgrade;
import ore.forge.Ore;

//Subtracts a modifier from a value.
public class SubtractUPG extends AbstractUpgrade {

    public SubtractUPG(double mod, AbstractUpgrade.ValueToModify val) {
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
