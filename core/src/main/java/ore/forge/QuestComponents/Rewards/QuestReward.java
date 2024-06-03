package ore.forge.QuestComponents.Rewards;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.QuestComponents.QuestManager;

public class QuestReward implements Reward {
    private final String questID;
    private final QuestManager manager;

    public QuestReward(QuestManager manager, String questId) {
        this.manager = manager;
        this.questID = questId;
    }

    public QuestReward(QuestManager manager, JsonValue jsonValue) {
        this.manager = manager;
        this.questID = jsonValue.getString("questID");
    }

    @Override
    public void grantReward() {
        manager.activateQuest(questID);
    }

}
