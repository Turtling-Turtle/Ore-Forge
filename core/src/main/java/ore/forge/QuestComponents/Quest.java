package ore.forge.QuestComponents;

import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

public class Quest {
    private boolean isActive;
    private final ArrayList<QuestStep> questSteps;
    private final String id, name, description;

    private Quest(JsonValue jsonValue) {
        this.id = jsonValue.getString("id");
        this.name = jsonValue.getString("name");
        this.description = jsonValue.getString("description");
        this.questSteps = new ArrayList<>();
        /*
        * for each step in questSteps {
        * QuestStep questStep = loadViaReflection(jsonValue);
        * questSteps.add(questStep);
        * }
        *
        * */
    }

}
