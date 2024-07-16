package ore.forge.EventSystem;


import com.badlogic.gdx.Gdx;
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
    private final Deque<EventListener<?>> removalStack, additionStack;
    private boolean isNotifying;

    public EventManager() {
        subscribers = new HashMap<>();
        removalStack = new ArrayDeque<>();
        additionStack = new ArrayDeque<>();
        isNotifying = false;
    }


    public static EventManager getSingleton() {
        if (eventManager == null) {
            eventManager = new EventManager();
        }
        return eventManager;
    }

    private void addListener(EventListener<?> eventListener) {
        var eventType = eventListener.getEventType();
        assert Event.class.isAssignableFrom(eventType);
//        Gdx.app.log("EventManager", "Adding listener: " +  eventListener);
        if (!subscribers.containsKey(eventType)) {
            subscribers.put(eventType, new ArrayList<>());
            subscribers.get(eventType).add(eventListener);
        } else {
            subscribers.get(eventType).add(eventListener);
        }
    }

    public void registerListener(EventListener<?> listener) {
        System.out.println("isNotifying: " + isNotifying);
        if (isNotifying) {
//            Gdx.app.log("EventManager", "Adding listener to registration queue: " + listener);
            additionStack.push(listener);
        } else {
//            Gdx.app.log("EventManager", "Registering listener Immediately : " + listener);
            addListener(listener);
        }

    }

    public void removeListener(EventListener<?> listener) {
//        Gdx.app.log("EventManager", "Removing listener: " + listener);
        subscribers.get(listener.getEventType()).remove(listener);
    }

    public void unregisterListener(EventListener<?> listener) {
        System.out.println(isNotifying);
        if (isNotifying) {
//            Gdx.app.log("EventManager", "Adding listener to removal queue: " + listener);
            removalStack.push(listener);
        } else {
//            Gdx.app.log("EventManager", "Removing listener Immediately : " + listener);
            removeListener(listener);
        }
    }

    public void notifyListeners(Event event) {
        while (!additionStack.isEmpty()) {
            addListener(additionStack.pop());
        }

        if (eventLogger != null) {
            this.eventLogger.logEvent(event);
        }

        isNotifying = true;
//        Gdx.app.log("EventManager", "Beginning Notification for event: " + event);
        ArrayList<EventListener> listeners = subscribers.get(event.getEventType());
//        Gdx.app.log("EventManager", "Notifying listeners: " + listeners);
        if (listeners != null && !listeners.isEmpty()) {
//            Gdx.app.log("EventManager", "Notifying subscribers: " + listeners);
            for (EventListener listener : listeners) {
//                Gdx.app.log("EventManager", "Notifying listener: " + listener);
                listener.handle(event);
            }
//            for (int i = 0; i < listeners.size(); i++) {
//                listeners.get(i).handle(event);
//            }
        }
//        Gdx.app.log("EventManager", "Ending Notification");

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


}
