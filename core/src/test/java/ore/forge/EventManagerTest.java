package ore.forge;

import ore.forge.EventSystem.EventListener;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.NodeEvent;
import ore.forge.EventSystem.Events.OreDroppedEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EventManagerTest {
    private EventManager manager;

    @Test
    void testRegistration() {
        manager = new EventManager();

        var listener = new EventListener<OreDroppedEvent>() {
            public boolean eventReceived = false;

            @Override
            public void handle(OreDroppedEvent event) {
                eventReceived = true;
            }

            @Override
            public Class<?> getEventType() {
                return OreDroppedEvent.class;
            }
        };
        manager.registerListener(listener);
        manager.notifyListeners(new OreDroppedEvent(null, null));
        assertTrue(listener.eventReceived);
    }

    @Test
    void testRegistrationWhileNotifying() {
        manager = new EventManager();
        var toBeAdded = new EventListener<OreDroppedEvent>() {
            @Override
            public void handle(OreDroppedEvent event) { }

            @Override
            public Class<?> getEventType() { return OreDroppedEvent.class; }
        };

        var listener = new EventListener<OreDroppedEvent>() {
            @Override
            public void handle(OreDroppedEvent event) { manager.registerListener(toBeAdded); }

            @Override
            public Class<?> getEventType() { return OreDroppedEvent.class; }
        };
        manager.registerListener(listener);

        manager.notifyListeners(new OreDroppedEvent(null, null));
        assertFalse(manager.hasListener(toBeAdded));
        manager.notifyListeners(new OreDroppedEvent(null, null));
        assertTrue(manager.hasListener(toBeAdded));
    }

    @Test
    void testRemoval() {
        manager = new EventManager();

        var listener = new EventListener<OreDroppedEvent>() {
            public boolean eventReceived = false;

            @Override
            public void handle(OreDroppedEvent event) {
                manager.unregisterListener(this);
                eventReceived = true;
            }

            @Override
            public Class<?> getEventType() {
                return OreDroppedEvent.class;
            }
        };
        manager.registerListener(listener);
        manager.notifyListeners(new OreDroppedEvent(null, null));
        assertFalse(manager.hasListener(listener));
    }

    @Test
    void testRemovalWhileNotifying() {
        manager = new EventManager();
        var listener = new EventListener<OreDroppedEvent>() {
            @Override
            public void handle(OreDroppedEvent event) {
                manager.unregisterListener(this);
            }

            @Override
            public Class<?> getEventType() {
                return OreDroppedEvent.class;
            }
        };

        for (int i = 0; i < 5; i++) {
            var fodder = new EventListener<OreDroppedEvent>() {
                @Override
                public void handle(OreDroppedEvent event) {
                    manager.notifyListeners(new NodeEvent(null));
                }

                @Override
                public Class<?> getEventType() { return OreDroppedEvent.class; }
            };
            manager.registerListener(fodder);
        }
        manager.registerListener(listener);
        manager.notifyListeners(new OreDroppedEvent(null, null));
        assertFalse(manager.hasListener(listener));
    }

}
