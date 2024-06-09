package ore.forge.EventSystem;


import ore.forge.EventSystem.Events.Event;

import java.util.ArrayList;
import java.util.HashMap;

//The Event Manager is responsible for publishing specific events to subscribers.
//Subscribers will implement the EventListener Interface.
//Whenever an event happens the EventManager will notify all subscribers interested in that specific type of event.
//The Class<?> event that is passed into teh registerListener Method should be the same type as the EventListener
//EX: An EventListener<OreSoldEvent> should pass in OreSoldEvent as the Class<?>.
// eventManager.registerListener(OreSoldEvent.class, subscriber);
//EventType is the key, Event is a generic.
public class EventManager {
    private static EventManager eventManager = new EventManager();
    private final HashMap<EventType, ArrayList<EventListener>> subscribers;

    private EventManager() {
        subscribers = new HashMap<>();
    }

    public static EventManager getSingleton() {
        if (eventManager == null) {
            eventManager = new EventManager();
        }
        return eventManager;
    }

    public void registerListener(EventType type, EventListener listener) {
        if (!subscribers.containsKey(type)) {
            assert !subscribers.get(type).contains(listener);
            subscribers.put(type, new ArrayList<>());
            subscribers.get(type).add(listener);
        } else {
            subscribers.get(type).add(listener);
        }
    }

    public void unregisterListener(EventType type, EventListener listener) {
        var listeners = subscribers.get(type);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                subscribers.remove(type);
            }
        }
    }

    public void notifyListeners(Event event) {
        ArrayList<EventListener> listeners = subscribers.get(event.getClass());
        if (listeners != null && !listeners.isEmpty()) {
            for (var listener : listeners) {
                listener.handle(event);
            }
        }
    }

}
