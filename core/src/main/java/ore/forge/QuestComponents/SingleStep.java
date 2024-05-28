package ore.forge.QuestComponents;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Expressions.Condition;
import ore.forge.Ore;
import ore.forge.QuestComponents.Rewards.Reward;

public class SingleStep implements QuestStep {
    private final QuestType type;
    private final String stepDescription;
    private final Condition condition;
    private Reward reward;
    private boolean isCompleted;

    public SingleStep(QuestType type, String stepDescription, Condition condition) {
        this.type = type;
        this.stepDescription = stepDescription;
        this.condition = condition;
    }

    private SingleStep(JsonValue jsonValue) {
        this.type = QuestType.valueOf(jsonValue.getString("type"));
        this.stepDescription = jsonValue.getString("description");
//        this.reward = loadViaReflection(jsonValue);
        this.condition = Condition.parseCondition(jsonValue.getString("condition"));
        this.isCompleted = jsonValue.getBoolean("completed");
    }

    @Override
    public QuestType getStepType() {
        return type;
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public void checkCondition(Ore ore) {
        if (condition.evaluate(ore)) {
            isCompleted = true;
        }
    }

    @Override
    public void grantReward() {
        reward.grantReward();
    }


}
