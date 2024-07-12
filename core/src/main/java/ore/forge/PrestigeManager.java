package ore.forge;

import ore.forge.Player.Player;

public class PrestigeManager {
    private final LootTable lootTable;
    private final static Player player = Player.getSingleton();
    private final static ItemMap itemMap = ItemMap.getSingleton();

    public PrestigeManager(ItemManager itemManager) {
        lootTable = new LootTable(itemManager);
    }

    public boolean canPrestige() {
        return false;
    }

    public void prestige() {
        player.setPrestigeLevel(player.getPrestigeLevel() + 1);
        //Event Manager Notifies of a prestige Event
        //Quests check/update their requirements.
        /*
         * Quests/Achievements
         * */
        awardItem();
        player.addPrestigeCurrency(3);


        //reset Item Map and "pick up" all items from map.
        itemMap.reset(player.getInventory());
        player.getInventory().prestigeReset(); //Reset Inventory

    }

    private void awardItem() {
        var reward = lootTable.getRandomItem();
        player.getInventory().addItem(reward, 1);
    }


}
