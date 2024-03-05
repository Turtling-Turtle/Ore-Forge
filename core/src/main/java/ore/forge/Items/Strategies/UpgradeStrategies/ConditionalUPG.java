package ore.forge.Items.Strategies.UpgradeStrategies;


import ore.forge.Ore;

public class ConditionalUPG implements UpgradeStrategy{
    public enum Condition{VALUE, UPGRADE_COUNT, TEMPERATURE, MULTIORE} //Condition to be evaluated
    public enum Comparison {GREATER_THAN, LESS_THAN, EQUAL_TO} //Type of comparison
    private final Condition condition;
    private Comparison comparison;
    private final UpgradeStrategy ifModifier;
    private final UpgradeStrategy elseModifier;
    private final double threshold;

    public ConditionalUPG(UpgradeStrategy ifMod, UpgradeStrategy elseMod, Condition condition, double threshold, Comparison comparison) {
        ifModifier = ifMod;
        elseModifier = elseMod;
        this.threshold = threshold;
        this.condition = condition;
        this.comparison = comparison;
    }

    @Override
    public void applyTo(Ore ore) {
        switch (condition) {
            case VALUE:
                valueComp(ore);
                break;
            case TEMPERATURE:
                tempComp(ore);
                break;
            case MULTIORE:
                multiOreComp(ore);
                break;
            case UPGRADE_COUNT:
                upgradeCountComp(ore);
                break;
        }
    }

    private void valueComp(Ore ore) {
        switch (comparison) {
            case GREATER_THAN:
                if(ore.getOreValue() >=threshold) {
                   ifModifier.applyTo(ore);
                } else {
                   elseModifier.applyTo(ore);
                }
                break;
            case LESS_THAN:
                if (ore.getOreValue()<= threshold) {
                    ifModifier.applyTo(ore);
                } else {
                    elseModifier.applyTo(ore);
                }
                break;
            case EQUAL_TO:
                if (ore.getOreValue()== threshold) {
                    ifModifier.applyTo(ore);
                } else {
                    elseModifier.applyTo(ore);
                }
                break;
        }
    }

    private void tempComp(Ore ore) {
        switch (comparison) {
            case GREATER_THAN:
                if(ore.getOreTemp() >=threshold) {
                    ifModifier.applyTo(ore);
                } else {
                    elseModifier.applyTo(ore);
                }
            break;
            case LESS_THAN:
                if (ore.getOreTemp()<= threshold) {
                    ifModifier.applyTo(ore);
                } else {
                    elseModifier.applyTo(ore);
                }
            break;
            case EQUAL_TO:
                if (ore.getOreTemp()== threshold) {
                    ifModifier.applyTo(ore);
                } else {
                    elseModifier.applyTo(ore);
                }
            break;
        }
    }

    private void multiOreComp(Ore ore) {
        switch (comparison) {
            case GREATER_THAN:
                if(ore.getMultiOre() >=threshold) {
                    ifModifier.applyTo(ore);
                } else {
                    elseModifier.applyTo(ore);
                }
            break;
            case LESS_THAN:
                if (ore.getMultiOre()<= threshold) {
                    ifModifier.applyTo(ore);
                } else {
                    elseModifier.applyTo(ore);
                }
            break;
            case EQUAL_TO:
                if (ore.getMultiOre()== threshold) {
                    ifModifier.applyTo(ore);
                } else {
                    elseModifier.applyTo(ore);
                }
            break;
        }
    }

    private void upgradeCountComp(Ore ore) {
        switch (comparison) {
            case GREATER_THAN:
                if( ore.getUpgradeCount()>=threshold) {
                    ifModifier.applyTo(ore);
                } else {
                    elseModifier.applyTo(ore);
                }
            break;
            case LESS_THAN:
                if (ore.getUpgradeCount()<= threshold) {
                    ifModifier.applyTo(ore);
                } else {
                    elseModifier.applyTo(ore);
                }
            break;
            case EQUAL_TO:
                if (ore.getUpgradeCount()== threshold) {
                    ifModifier.applyTo(ore);
                } else {
                    elseModifier.applyTo(ore);
                }
            break;
        }
    }

}
