package ore.forge.EventSystem;


import com.badlogic.gdx.Gdx;
import ore.forge.EventSystem.Events.Event;
import ore.forge.Screens.EventLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

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

    public void registerListener(EventType type, EventListener listener) {
        Gdx.app.log("EventManager", "Registering listener: " + type + " " + listener);
        if (!subscribers.containsKey(type)) {
//            assert !subscribers.get(type).contains(listener);
            subscribers.put(type, new ArrayList<>());
            subscribers.get(type).add(listener);

        } else {
            subscribers.get(type).add(listener);
        }
    }

    public void unregisterListener(EventType type, EventListener listener) {
//        Gdx.app.log("EventManager", "Unregistering listener: " + listener);
//        var listeners = subscribers.get(type);
//        if (listeners != null) {
        removalStack.push(listener);
//            listeners.remove(listener);
//            if (listeners.isEmpty()) {
//                subscribers.remove(type);
//            }
//        }
    }

    public void notifyListeners(Event event) {
        this.eventLogger.logEvent(event);
        ArrayList<EventListener> listeners = subscribers.get(event.getType());
        if (listeners != null && !listeners.isEmpty()) {
            for (int i = 0; i < listeners.size(); i++) {
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
                subscribers.remove(event.getType());
            }
        }


    }

    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

}
