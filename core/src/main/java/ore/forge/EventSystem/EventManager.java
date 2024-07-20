package ore.forge.EventSystem;


import ore.forge.EventSystem.Events.Event;
import ore.forge.Screens.EventLogger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;


/**@author Nathan Ulmen
 *
 *
 *
 * */
public class EventManager {
    private static EventManager eventManager = new EventManager();
    private final HashMap<Class<?>, ArrayList<EventListener>> subscribers;
    private EventLogger eventLogger;
//    private final Deque<EventListener<?>> removalStack, additionStack;
    private boolean isNotifying;

    public EventManager() {
        subscribers = new HashMap<>();
//        removalStack = new ArrayDeque<>();
//        additionStack = new ArrayDeque<>();
        isNotifying = false;
    }

    public static EventManager getSingleton() {
        if (eventManager == null) {
            eventManager = new EventManager();
        }
        return eventManager;
    }

    public void registerListener(EventListener<?> listener) {
//        if (isNotifying) {
//            additionStack.push(listener);
//        } else {
            addListener(listener);
//        }
    }

    private void addListener(EventListener<?> eventListener) {
        var eventType = eventListener.getEventType();
        assert Event.class.isAssignableFrom(eventType);
        if (!subscribers.containsKey(eventType)) {
            subscribers.put(eventType, new ArrayList<>());
            subscribers.get(eventType).add(eventListener);
        } else {
            subscribers.get(eventType).add(eventListener);
        }
    }

    public void unregisterListener(EventListener<?> listener) {
//        if (isNotifying) {
//            removalStack.push(listener);
//        } else {
            removeListener(listener);
//        }
    }

    private void removeListener(EventListener<?> listener) {
        subscribers.get(listener.getEventType()).remove(listener);
    }

    public void notifyListeners(Event event) {
//        while (!additionStack.isEmpty()) {
//            addListener(additionStack.pop());
//        }

        if (eventLogger != null) {
            this.eventLogger.logEvent(event);
        }

        isNotifying = true;
        ArrayList<EventListener> listeners;
        if (subscribers.get(event.getEventType()) != null) {
            listeners = new ArrayList<>(subscribers.get(event.getEventType()));
            if (!listeners.isEmpty()) {
                for (EventListener listener : listeners) {
                    listener.handle(event);
                }
            }
        }
        isNotifying = false;

//        while (!removalStack.isEmpty()) {
//            removeListener(removalStack.pop());
//        }

    }

    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }


    public boolean hasListener(EventListener<?> listener) {
        return subscribers.get(listener.getEventType()).contains(listener);
    }

    public String toString() {
        return subscribers.toString();
    }


}
