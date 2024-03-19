package ore.forge.Strategies.UpgradeStrategies.PrimaryUPGS;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;
import ore.forge.Strategies.UpgradeStrategies.BasicUpgrade;

//Adds a modifier to a value.
public class AddUPG extends BasicUpgrade {

    public AddUPG(double mod, BasicUpgrade.ValueToModify val) {
        super(mod, val);
    }

    public AddUPG(JsonValue jsonValue) {
        super(jsonValue);
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
