package ore.forge.Expressions;

import ore.forge.ItemMap;
import ore.forge.Ore;
import ore.forge.OreRealm;
import ore.forge.Player.Player;

import java.util.Comparator;


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
    },
    AVG_ORE_VALUE,
    MEDIAN_ORE_VALUE,;
    /*
    * Average, Median, Min, Max, values for the different types of numerical Ore properties.
    * */

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
        try {
            valueOf(value);
            return true;
        } catch (Exception e) {
            return false;

        }
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
            case AVG_ORE_VALUE -> () -> {
                double total = 0;
                for (Ore ore : oreRealm.getActiveOre()) {
                    total += ore.getOreValue();
                }
                return total / oreRealm.getActiveOre().size();
            };
            case MEDIAN_ORE_VALUE -> () -> {
                //This might be too expensive as is, might need to look into ways of calculating it.
//               https://en.wikipedia.org/wiki/Quickselect
                var activeOre = oreRealm.getUniqueActiveOre();
                activeOre.sort(Comparator.comparingDouble(Ore::getOreValue));
                if (activeOre.size() % 2 == 0) {
                    var ore1 = activeOre.get(activeOre.size() / 2);
                    var ore2 = activeOre.get(activeOre.size() / 2 + 1);
                    return (ore1.getOreValue() + ore2.getOreValue()) / 2;
                } else {
                    return activeOre.get(oreRealm.getActiveOre().size()/2).getOreValue();
                }
            };
        };
    }

}
