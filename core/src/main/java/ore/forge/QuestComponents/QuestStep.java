package ore.forge.QuestComponents;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.QuestStepCompletedEvent;
import ore.forge.QuestComponents.Rewards.Reward;
import ore.forge.ReflectionLoader;

//A QuestStep holds a reward, and an array of conditions.
//The QuestStep is only complete
public class QuestStep {
    private final EventManager eventManager = EventManager.getSingleton();
    private Quest parent;
    private final String stepDescription, stepName;
    private final QuestCondition[] conditionArray;
    private Reward reward;
    private QuestState state;

    public QuestStep(String stepDescription, String stepName, QuestCondition... questCondition) {
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.conditionArray = new QuestCondition[questCondition.length];
        System.arraycopy(questCondition, 0, conditionArray, 0, questCondition.length);
    }

    public QuestStep(Quest parent, JsonValue jsonValue) {
        this.parent = parent;
        this.stepName = jsonValue.getString("stepName");
        this.stepDescription = jsonValue.getString("stepDescription");
        conditionArray = new QuestCondition[jsonValue.get("questConditions").size];
        var questConditions = jsonValue.get("questConditions");
        for (int i = 0; i < conditionArray.length; i++) {
            conditionArray[i] = new QuestCondition(this, questConditions.get(i));
        }

        this.state = QuestState.valueOf(jsonValue.getString("state"));
        if (jsonValue.has("reward")) {
            reward = ReflectionLoader.load(jsonValue.get("reward"), "rewardType");
        } else {
            reward = null;
        }


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
        eventManager.notifyListeners(new QuestStepCompletedEvent(this));
        grantReward();
        parent.checkForCompletion();
    }

    public QuestState getState() {
        return this.state;
    }

    public String getStepName() {
        return stepName;
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
