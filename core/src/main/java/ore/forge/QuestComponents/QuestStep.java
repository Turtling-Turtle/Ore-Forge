package ore.forge.QuestComponents;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.EventSystem.EventManager;
import ore.forge.QuestComponents.Rewards.Reward;

//A QuestStep holds a reward, and an array of conditions.
//The QuestStep is only complete
public class QuestStep {
    private final EventManager eventManager = EventManager.getSingleton();
    private Quest parent;
    private final String stepDescription;
    private final QuestCondition[] conditionArray;
    private Reward reward;
    private QuestState state;

    public QuestStep(String stepDescription, QuestCondition... questCondition) {
        this.stepDescription = stepDescription;
        this.conditionArray = new QuestCondition[questCondition.length];
        System.arraycopy(questCondition, 0, conditionArray, 0, questCondition.length);
    }

    public QuestStep(Quest parent, JsonValue jsonValue) {
        this.parent = parent;
        this.stepDescription = jsonValue.getString("description");
        conditionArray = new QuestCondition[jsonValue.get("condition").size];
        for (int i = 0; i < jsonValue.size; i++) {
            conditionArray[i] = new QuestCondition(this, jsonValue.get("condition").get(i));
        }
        this.state = QuestState.valueOf(jsonValue.getString("state"));
    }

    public void registerConditions() {
        for (QuestCondition condition : conditionArray) {
            condition.register();
        }
    }

    public String getStepDescription() {
        return stepDescription;
    }

    public QuestCondition[] getConditionArray() {
        return conditionArray;
    }

    /*
    * Whenever a QuestCondition is completed this
    * */
    public void checkState() {
        for (QuestCondition questCondition : conditionArray) {
            if (questCondition.getState() != QuestState.COMPLETED) {
                return;
            }
        }
        this.state = QuestState.COMPLETED;
        grantReward();
        parent.checkForCompletion();
    }

    public QuestState getState() {
        return this.state;
    }

    public boolean isCompleted() {
        return state == QuestState.COMPLETED;
    }

    public void grantReward() {
        if (reward != null) {
            reward.grantReward();
        }
    }

    public String toString() {
        java.lang.StringBuilder s = new StringBuilder();
        for (QuestCondition questCondition : conditionArray) {
            s.append("\n").append(questCondition.toString());
        }
        return s.toString();
    }

}
