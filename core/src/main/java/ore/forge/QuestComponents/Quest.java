package ore.forge.QuestComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Queue;
import ore.forge.Color;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.QuestCompletedGameEvent;
import ore.forge.Listener;

import java.util.ArrayList;

/*
 * The Quest is a collection of QuestSteps and is responsible for keeping track of which step is currently active.
 * and registering the steps to the EventManager.
 *
 * */
public class Quest {
    private final EventManager eventManger = EventManager.getSingleton();
    private QuestStatus state;
    private boolean isActive;
    private final String id, name, description;
    private final Queue<QuestStep> incompleteSteps;
    private final ArrayList<QuestStep> completedSteps, questSteps;
    private final ArrayList<Listener<Quest>> listeners;

    public Quest(JsonValue jsonValue) {
        listeners = new ArrayList<>();
        this.id = jsonValue.getString("id");
        this.name = jsonValue.getString("name");
        this.description = jsonValue.getString("description");
        this.questSteps = new ArrayList<>();
        this.completedSteps = new ArrayList<>();
        this.incompleteSteps = new Queue<>();
        this.state = QuestStatus.valueOf(jsonValue.getString("state"));

        for (JsonValue step : jsonValue.get("steps")) {
            var questStep = new QuestStep(this, step);
            incompleteSteps.addLast(questStep);
            questSteps.add(questStep);
        }


        while (!incompleteSteps.isEmpty() && incompleteSteps.first().getState() == QuestStatus.COMPLETED) {
            completedSteps.add(incompleteSteps.removeFirst());
        }


        if (this.state == QuestStatus.COMPLETED) {
            return;
        } else {
            incompleteSteps.first();
        }

    }

    public QuestStatus getStatus() {
        return this.state;
    }

    public boolean isActive() {
        return this.state == QuestStatus.COMPLETED;
    }

//    public void nextStep() {
//        assert this.currentStep.isCompleted();
//        completedSteps.add(incompleteSteps.removeFirst());
//
//        if (incompleteSteps.isEmpty()) {
//            this.complete();
//        }
//
//        currentStep = incompleteSteps.first();
//        currentStep.registerConditions();
//    }


//    public void update(QuestCondition[] conditions) {
//        completedSteps.add(incompleteSteps.removeFirst());
//
//        if (!incompleteSteps.isEmpty()) {
//            currentStep = incompleteSteps.first();
//            for (QuestCondition condition : currentStep.getConditionArray()) {
//                eventManger.registerListener(condition);
//            }
//        } else {
//            complete();
//        }
//    }

    public void checkForCompletion() {
        Gdx.app.log("Quest","Called");
//        assert questSteps.size() == (completedSteps.size() + incompleteSteps.size);

        if (!incompleteSteps.isEmpty()) {
            assert incompleteSteps.first().isCompleted();
        }

//        Gdx.app.log("Quest - Incomplete Steps","Step:" + incompleteSteps.first().toString());
//        Gdx.app.log("Quest","Incomplete Steps Size: " + incompleteSteps.size);

        if (!incompleteSteps.isEmpty()) {
            completedSteps.add(incompleteSteps.removeFirst());
        } else {
            Gdx.app.log("Quest", Color.highlightString("Bug Triggered", Color.YELLOW));
        }

        if (!incompleteSteps.isEmpty()) {
            incompleteSteps.first().registerConditions();
//            currentStep = incompleteSteps.first();
//            currentStep.registerConditions();
        } else {
            complete();
        }
    }

    public int getStepCount() {
        return questSteps.size();
    }

    public void start() {
        assert this.state == QuestStatus.LOCKED;
        this.state = QuestStatus.IN_PROGRESS;
        initialize();
    }

    public void complete() {
        assert incompleteSteps.isEmpty();
        this.state = QuestStatus.COMPLETED;
        eventManger.notifyListeners(new QuestCompletedGameEvent(this));
    }

    public void initialize() {
        incompleteSteps.first().registerConditions();
//        currentStep.registerConditions();
    }

    public void addListener(Listener<Quest> listener) {
        this.listeners.add(listener);
    }

    public void notifyListeners() {
        for (Listener<Quest> listener : listeners) {
            listener.update(this);
        }
    }

    public QuestStep getCurrentStep() {
        return this.incompleteSteps.first();
    }

    public int stepOf() {
        return questSteps.indexOf(incompleteSteps.first()) + 1;
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
