package ore.forge.EventSystem;


import ore.forge.EventSystem.Events.Event;
import ore.forge.Screens.EventLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**@author Nathan Ulmen
 *
 *
 *
 * */
public class EventManager {
    private static EventManager eventManager = new EventManager();
    private final HashMap<Class<?>, ArrayList<EventListener>> subscribers;
    private EventLogger eventLogger;
    private final Stack<EventListener> removalStack, additionStack;

    private EventManager() {
        subscribers = new HashMap<>();
        removalStack = new Stack<>();
        additionStack = new Stack<>();
    }

    public static EventManager getSingleton() {
        if (eventManager == null) {
            eventManager = new EventManager();
        }
        return eventManager;
    }

    public void registerListener(Class<?> eventType, EventListener<?> listener) {
        System.out.println(eventType);
        assert Event.class.isAssignableFrom(eventType);
        if (!subscribers.containsKey(eventType)) {
            subscribers.put(eventType, new ArrayList<>());
            subscribers.get(eventType).add(listener);
        } else {
            subscribers.get(eventType).add(listener);
        }

    }

    public void unregisterListener(EventListener<?> listener) {
        removalStack.push(listener);
    }

    public void notifyListeners(Event event) {
        this.eventLogger.logEvent(event);
        ArrayList<EventListener> listeners = subscribers.get(event.getEventType());
        if (listeners != null && !listeners.isEmpty()) {
            for (int i = 0; i < listeners.size(); i++) { //For i to evade concurrent modification, not the smartest...
                listeners.get(i).handle(event);
            }
        }

        while (!additionStack.isEmpty()) {
            var listener = additionStack.pop();
        }

        if (listeners == null) {
            return;
        }
        while (!removalStack.isEmpty()) {
            listeners.remove(removalStack.pop());
            if (listeners.isEmpty()) {
                subscribers.remove(event.getEventType());
            }
        }


    }

    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

}
