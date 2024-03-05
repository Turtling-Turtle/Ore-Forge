package ore.forge.Strategies.UpgradeStrategies.PrimaryUPGS;


import ore.forge.Strategies.UpgradeStrategies.AbstractUpgrade;
import ore.forge.Ore;

//Multiplies a value by a modifier
public class MultiplyUPG extends AbstractUpgrade {
    public MultiplyUPG(double modifier, AbstractUpgrade.ValueToModify value) {
        super(modifier, value);
    }

    @Override
    public void applyTo(Ore ore) {
        switch (this.getValueToMod()) {
            case ORE_VALUE:
                ore.setOreValue(ore.getOreValue()* getModifier());
                break;
            case TEMPERATURE:
                ore.setTemp((int) (ore.getOreTemp() * getModifier()));
                break;
            case MULTIORE:
                ore.setMultiOre((int) (ore.getMultiOre() * getModifier()));
                break;
        }
    }

}
