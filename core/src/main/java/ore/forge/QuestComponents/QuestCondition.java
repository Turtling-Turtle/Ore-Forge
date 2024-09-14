package ore.forge.QuestComponents;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.EventSystem.GameEventListener;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.GameEvent;
import ore.forge.Expressions.Condition;
import ore.forge.Listener;
import ore.forge.Ore;

import java.util.ArrayList;

public class QuestCondition implements GameEventListener<GameEvent<?>> {
    private final Class<?> eventType;
    private final EventManager eventManager = EventManager.getSingleton();
    private final QuestStep parent;
    private final Condition condition;
    private QuestStatus state;
    private final String description;
    private final ArrayList<Listener> listeners;

    public QuestCondition(QuestStep parent, JsonValue jsonValue) {
        listeners = new ArrayList<>(5);
        this.parent = parent;
        condition = Condition.parseCondition(jsonValue.getString("condition"));
        state = QuestStatus.valueOf(jsonValue.getString("state"));
        eventType = getEvent(jsonValue, "eventType");
        description = jsonValue.getString("description");
        assert eventType != null;

    }

    public void checkCondition(Ore ore) {
        if (condition.evaluate(ore)) {
            state = QuestStatus.COMPLETED;
            this.unregister();
            parent.checkState();
        }
    }

    public void register() {
        eventManager.registerListener(this);
    }

    @Override
    public Class<?> getEventType() {
        return eventType;
    }

    public void unregister() {
        eventManager.unregisterListener(this);
    }

    public QuestStatus getState() {
        return this.state;
    }

    public String toString() {
        return eventType + "\t" + condition;
    }

    public Condition getCondition() {
        return condition;
    }

    /**
     * @return returns the description of the quest condition.
     *
     * */
    public String getDescription() {
        return description;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void handle(GameEvent<?> event) {
//        assert event.getSubject().getClass() == Ore.class;
        if (event.getSubject() instanceof Ore) {
            checkCondition((Ore) event.getSubject());
        } else {
            checkCondition(null);
        }

    }

    private Class<?> getEvent(JsonValue jsonValue, String fieldName) {
        Class<?> aClass;
        try {
            try {
                aClass = Class.forName(jsonValue.getString(fieldName));
            } catch (NullPointerException e) {
                return null;
            }
            return aClass;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e + "\nJson value:" + jsonValue.toString() + "\t" + fieldName);
        }
    }


}

