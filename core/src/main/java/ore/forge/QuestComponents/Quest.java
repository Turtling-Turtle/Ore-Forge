package ore.forge.QuestComponents;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Queue;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.QuestCompletedEvent;
import ore.forge.EventSystem.Events.QuestStepCompletedEvent;

import java.util.ArrayList;

/*
 * The Quest is a collection of QuestSteps and is responsible for keeping track of which step is currently active.
 * and registering the steps to the EventManager.
 *
 * */
public class Quest {
    private final EventManager eventManger = EventManager.getSingleton();
    private QuestState state;
    private boolean isActive;
    private QuestStep currentStep;
    private final String id, name, description;
    private final Queue<QuestStep> incompleteSteps;
    private final ArrayList<QuestStep> completedSteps, questSteps;

    public Quest(JsonValue jsonValue) {
        this.id = jsonValue.getString("id");
        this.name = jsonValue.getString("name");
        this.description = jsonValue.getString("description");
        this.questSteps = new ArrayList<>();
        this.completedSteps = new ArrayList<>();
        this.incompleteSteps = new Queue<>();
        this.state = QuestState.valueOf(jsonValue.getString("state"));

        for (JsonValue step : jsonValue.get("steps")) {
            var questStep = new QuestStep(this, step);
            incompleteSteps.addLast(questStep);
            questSteps.add(questStep);
        }


        while (!incompleteSteps.isEmpty() && incompleteSteps.first().getState() == QuestState.COMPLETED) {
            completedSteps.add(incompleteSteps.removeFirst());
        }


        if (this.state == QuestState.COMPLETED) {
            return;
        } else {
            this.currentStep = incompleteSteps.first();
        }
    }

    public QuestState getState() {
        return this.state;
    }

    public boolean isActive() {
        return this.state == QuestState.COMPLETED;
    }

    public void nextStep() {
        assert this.currentStep.isCompleted();
        completedSteps.add(incompleteSteps.removeFirst());

        if (incompleteSteps.isEmpty()) {
            this.complete();
        }

        currentStep = incompleteSteps.first();
        currentStep.registerConditions();
    }


    public void update(QuestCondition[] conditions) {
        completedSteps.add(incompleteSteps.removeFirst());

        if (!incompleteSteps.isEmpty()) {
            currentStep = incompleteSteps.first();
            for (QuestCondition condition : currentStep.getConditionArray()) {
                eventManger.registerListener(condition.getEventType(), condition);
            }
        } else {
            complete();
        }
    }

    public void checkForCompletion() {
        assert incompleteSteps.first().isCompleted();

        completedSteps.add(incompleteSteps.removeFirst());
        if (!incompleteSteps.isEmpty()) {
            currentStep = incompleteSteps.first();
            currentStep.registerConditions();
        } else {
            complete();
        }
    }

    public void start() {
        assert this.state == QuestState.LOCKED;
        this.state = QuestState.IN_PROGRESS;
        initialize();
    }

    public void complete() {
        assert incompleteSteps.isEmpty();
        this.state = QuestState.COMPLETED;
        eventManger.notifyListeners(new QuestCompletedEvent(this));
    }

    public void initialize() {
        currentStep.registerConditions();
    }

    public QuestStep getCurrentStep() {
        return this.currentStep;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
