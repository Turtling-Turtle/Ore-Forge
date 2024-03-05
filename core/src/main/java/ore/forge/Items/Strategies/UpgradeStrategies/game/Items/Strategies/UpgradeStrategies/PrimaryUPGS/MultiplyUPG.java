package ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies.PrimaryUPGS;


import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies.AbstractUpgrade;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Ore;

//Multiplies a value by a modifier
public class MultiplyUPG extends AbstractUpgrade {
    public MultiplyUPG(double modifier, ValueToModify value) {
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
