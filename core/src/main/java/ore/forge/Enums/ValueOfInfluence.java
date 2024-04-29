package ore.forge.Enums;

import ore.forge.ItemMap;
import ore.forge.Ore;
import ore.forge.OreRealm;
import ore.forge.Player.Player;
import ore.forge.Strategies.NumericOperand;


//Unknown if this will be used.
public enum ValueOfInfluence implements NumericOperand {
    ACTIVE_ORE {
    },
    PLACED_ITEMS {
    },
    SPECIAL_POINTS {
    },
    WALLET {
    },
    PRESTIGE_LEVEL {
    };

    @Override
    public double calculate(Ore ore) {
        return doubleSupplier.getValue();
    }

    private interface DoubleSupplier {
        double getValue();
    }

    private final OreRealm oreRealm = OreRealm.getSingleton();
    private final Player player = Player.getSingleton();
    private final ItemMap itemMap = ItemMap.getSingleton();
    private final DoubleSupplier doubleSupplier;

    public static boolean isValue(String value) {
        return switch (value) {
            case "ACTIVE_ORE", "PLACED_ITEMS", "SPECIAL_POINTS", "WALLET", "PRESTIGE_LEVEL" -> true;
            default -> false;
        };
    }

    public double getAssociatedValue() {
        return doubleSupplier.getValue();
    }

    ValueOfInfluence() {
        doubleSupplier = switch (this) {
            case ACTIVE_ORE -> () -> oreRealm.getActiveOre().size();
            case PLACED_ITEMS -> () -> itemMap.getPlacedItems().size();
            case SPECIAL_POINTS -> player::getSpecialPoints;
            case WALLET -> player::getWallet;
            case PRESTIGE_LEVEL -> player::getPrestigeLevel;
        };
    }

}
