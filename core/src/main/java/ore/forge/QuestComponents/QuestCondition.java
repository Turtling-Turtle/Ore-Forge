package ore.forge.QuestComponents;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.EventSystem.EventListener;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.Event;
import ore.forge.Expressions.Condition;
import ore.forge.Ore;

public class QuestCondition implements EventListener<Event<?>> {
    private final Class<?> eventType;
    private final EventManager eventManager = EventManager.getSingleton();
    private final QuestStep parent;
    private final Condition condition;
    private QuestStatus state;
    private final String description;

    public QuestCondition(QuestStep parent, JsonValue jsonValue) {
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

    public String getDescription() {
        return description;
    }

    @Override
    public void handle(Event<?> event) {
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

