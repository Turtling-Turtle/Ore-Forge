package ore.forge.Enums;

import ore.forge.ItemMap;
import ore.forge.OreRealm;
import ore.forge.Player.Player;



//Unknown if this will be used.
public enum ValueOfInfluence implements KeyValue {
//    ORE_VALUE {
//        @Override
//        public double getAssociatedValue(Ore ore) {
//            return ore.getOreValue();
//        }
//    },
//    ORE_TEMPERATURE {
//        @Override
//        public double getAssociatedValue(Ore ore) {
//            return ore.getOreTemp();
//        }
//    },
//    MULTIORE {
//        @Override
//        public double getAssociatedValue(Ore ore) {
//            return ore.getOreValue();
//        }
//    },
//    UPGRADE_COUNT{
//        @Override
//        public double getAssociatedValue(Ore ore) {
//            return ore.getMultiOre();
//        }
//    },
    ACTIVE_ORE {
        public double getAssociatedValue() {
            return oreRealm.getActiveOre().size();
        }
    },
    PLACED_ITEMS {
        public double getAssociatedValue() {
            return itemMap.getPlacedItems().size();
        }
    },
    SPECIAL_POINTS {
        public double getAssociatedValue() {
            return player.getSpecialPoints();
        }
    },
    WALLET {
        public double getAssociatedValue() {
            return player.getWallet();
        }
    },
    PRESTIGE_LEVEL {
        public double getAssociatedValue() {
            return player.getPrestigeLevel();
        }
    };

    private static final OreRealm oreRealm = OreRealm.getSingleton();
    private static final Player player = Player.getSingleton();
    private static final ItemMap itemMap = ItemMap.getSingleton();
    public abstract double getAssociatedValue();

    ValueOfInfluence() {

    }

}
