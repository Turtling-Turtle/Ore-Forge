package ore.forge.Enums;

import ore.forge.Ore;
import ore.forge.Strategies.Operand;


public enum OreProperty implements KeyValue, Operand {
    ORE_VALUE,
    TEMPERATURE,
    MULTIORE,
    SPEED,
    UPGRADE_COUNT;

    public final double getAssociatedValue(Ore ore) {
        return supplier.getValue(ore);
    }

    @Override
    public double calculate(Ore ore) {
        return  supplier.getValue(ore);
    }

    private interface ValueGetter {
        double getValue(Ore ore);
    }

    public static boolean isProperty(String property) {
        return switch (property) {
            case "ORE_VALUE", "TEMPERATURE", "MULTIORE", "SPEED", "UPGRADE_COUNT" -> true;
            default -> false;
        };
    }

    private final ValueGetter supplier;

    OreProperty() {
        supplier = switch (this) {
            case ORE_VALUE -> (Ore::getOreValue);
            case TEMPERATURE -> (Ore::getOreTemp);
            case MULTIORE -> (Ore::getMultiOre);
            case SPEED -> (Ore::getSpeedScalar);
            case UPGRADE_COUNT -> (Ore::getUpgradeCount);
        };
    }
}
