package ore.forge.Enums;

import ore.forge.ItemMap;
import ore.forge.Ore;
import ore.forge.OreRealm;
import ore.forge.Player.Player;


//Unknown if this will be used.
public enum ValueOfInfluence {
    ORE_VALUE {
        @Override
        public double getAssociatedValue(Ore ore) {
            return ore.getOreValue();
        }
    },
    ORE_TEMPERATURE {
        @Override
        public double getAssociatedValue(Ore ore) {
            return ore.getOreTemp();
        }
    },
    MULTIORE {
        @Override
        public double getAssociatedValue(Ore ore) {
            return ore.getOreValue();
        }
    },
    UPGRADE_COUNT{
        @Override
        public double getAssociatedValue(Ore ore) {
            return ore.getMultiOre();
        }
    },
    ACTIVE_ORE {
        @Override
        public double getAssociatedValue(Ore ore) {
            return oreRealm.getActiveOre().size();
        }
    },
    PLACED_ITEMS {
        @Override
        public double getAssociatedValue(Ore ore) {
            return itemMap.getPlacedItems().size();
        }
    },
    SPECIAL_POINTS {
        @Override
        public double getAssociatedValue(Ore ore) {
            return player.getSpecialPoints();
        }
    },
    WALLET {
        @Override
        public double getAssociatedValue(Ore ore) {
            return player.getWallet();
        }
    },
    PRESTIGE_LEVEL {
        @Override
        public double getAssociatedValue(Ore ore) {
            return player.getPrestigeLevel();
        }
    };

    private static final OreRealm oreRealm = OreRealm.getSingleton();
    private static final Player player = Player.getSingleton();
    private static final ItemMap itemMap = ItemMap.getSingleton();
    public abstract double getAssociatedValue(Ore ore);

}
