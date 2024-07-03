package ore.forge.QuestComponents;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.EventSystem.EventListener;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.EventType;
import ore.forge.EventSystem.Events.Event;
import ore.forge.Expressions.Condition;
import ore.forge.Ore;

public class QuestCondition implements EventListener<Event> {
    private final EventManager eventManager = EventManager.getSingleton();
    private final QuestStep parent;
    private final Condition condition;
    private final EventType eventType;
    private QuestState state;

    public QuestCondition(QuestStep parent, JsonValue jsonValue) {
        this.parent = parent;
        condition = Condition.parseCondition(jsonValue.getString("condition"));
        eventType = EventType.valueOf(jsonValue.getString("eventType"));
        state = QuestState.valueOf(jsonValue.getString("state"));
    }

    public void checkCondition(Ore ore) {
        if (condition.evaluate(ore)) {
            state = QuestState.COMPLETED;
            this.unregister();
            parent.checkState();
        }
    }

    public void register() {
        eventManager.registerListener(eventType, this);
    }

    public void unregister() {
        eventManager.unregisterListener(eventType, this);
    }

    public QuestState getState() {
        return this.state;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String toString() {
        return eventType + "\t" + condition;
    }

    @Override
    public void handle(Event event) {
        //TODO
        checkCondition(null);
    }
}

