package ore.forge.Items.Strategies.UpgradeStrategies.PrimaryUPGS;

import ore.forge.Items.Strategies.UpgradeStrategies.AbstractUpgrade;
import ore.forge.Ore;

//Adds a modifier to a value.
public class AddUPG extends AbstractUpgrade {

    public AddUPG(double mod, AbstractUpgrade.ValueToModify val) {
        super(mod, val);
    }

    @Override
    public void applyTo(Ore ore) {
        switch (this.getValueToMod()) {
            case ORE_VALUE:
                ore.setOreValue(ore.getOreValue() + getModifier());
                break;
            case TEMPERATURE:
                ore.setTemp((int) (ore.getOreTemp() + getModifier()));
                break;
            case MULTIORE:
                ore.setMultiOre((int) (ore.getMultiOre() + getModifier()));
                break;
        }
    }

}
