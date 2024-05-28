package ore.forge.QuestComponents.Rewards;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Items.Item;
import ore.forge.Player.Player;

public class ItemReward implements Reward{
    private final Player player = Player.getSingleton();
    private final String itemID;
    private final int count;

    public ItemReward(String reward, int count) {
        this.itemID = reward;
        this.count = count;
    }

    public ItemReward(JsonValue jsonValue) {
        this.itemID = jsonValue.getString("rewardID");
        this.count = jsonValue.getInt("rewardCount");
    }

    @Override
    public void grantReward() {
        player.getInventory().addItem(itemID,count);
    }
}
