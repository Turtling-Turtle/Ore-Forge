package ore.forge.QuestComponents;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Expressions.Condition;
import ore.forge.Ore;

public class SingleCondition implements QuestCondition {
    private final Condition condition;
    private final UpdateType updateType;
    private boolean isCompleted;

    public SingleCondition(JsonValue jsonValue) {
        this.condition = Condition.parseCondition(jsonValue.getString("condition"));
        this.updateType = UpdateType.valueOf(jsonValue.getString("updateType"));
    }

    public void checkCondition(Ore ore) {
        if (condition.evaluate(ore)) {
            isCompleted = true;
        }
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public void checkConditions(Ore ore) {

    }
}
