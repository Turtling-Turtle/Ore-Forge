package ore.forge;

import ore.forge.EventSystem.GameEventListener;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.NodeGameEvent;
import ore.forge.EventSystem.Events.OreDroppedGameEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameEventManagerTest {
    private EventManager manager;

    @Test
    void testRegistration() {
        manager = new EventManager();

        var listener = new GameEventListener<OreDroppedGameEvent>() {
            public boolean eventReceived = false;

            @Override
            public void handle(OreDroppedGameEvent event) {
                eventReceived = true;
            }

            @Override
            public Class<?> getEventType() {
                return OreDroppedGameEvent.class;
            }
        };
        manager.registerListener(listener);
        manager.notifyListeners(new OreDroppedGameEvent(null, null));
        assertTrue(listener.eventReceived);
    }

    @Test
    void testRegistrationWhileNotifying() {
        manager = new EventManager();
        var toBeAdded = new GameEventListener<OreDroppedGameEvent>() {
            @Override
            public void handle(OreDroppedGameEvent event) {
            }

            @Override
            public Class<?> getEventType() {
                return OreDroppedGameEvent.class;
            }
        };

        var listener = new GameEventListener<OreDroppedGameEvent>() {
            @Override
            public void handle(OreDroppedGameEvent event) {
                manager.registerListener(toBeAdded);
            }

            @Override
            public Class<?> getEventType() {
                return OreDroppedGameEvent.class;
            }
        };
        manager.registerListener(listener);

        manager.notifyListeners(new OreDroppedGameEvent(null, null));
        assertTrue(manager.hasListener(toBeAdded));
    }

    @Test
    void testRemoval() {
        manager = new EventManager();

        var listener = new GameEventListener<OreDroppedGameEvent>() {
            public boolean eventReceived = false;

            @Override
            public void handle(OreDroppedGameEvent event) {
                manager.unregisterListener(this);
                eventReceived = true;
            }

            @Override
            public Class<?> getEventType() {
                return OreDroppedGameEvent.class;
            }
        };
        manager.registerListener(listener);
        manager.notifyListeners(new OreDroppedGameEvent(null, null));
        assertFalse(manager.hasListener(listener));
    }

    @Test
    void testRemovalWhileNotifying() {
        manager = new EventManager();
        var listener = new GameEventListener<OreDroppedGameEvent>() {
            @Override
            public void handle(OreDroppedGameEvent event) {
                manager.unregisterListener(this);
            }

            @Override
            public Class<?> getEventType() {
                return OreDroppedGameEvent.class;
            }
        };

        for (int i = 0; i < 5; i++) {
            var fodder = new GameEventListener<OreDroppedGameEvent>() {
                @Override
                public void handle(OreDroppedGameEvent event) {
                    manager.notifyListeners(new NodeGameEvent(null));
                }

                @Override
                public Class<?> getEventType() {
                    return OreDroppedGameEvent.class;
                }
            };
            manager.registerListener(fodder);
        }
        manager.registerListener(listener);
        manager.notifyListeners(new OreDroppedGameEvent(null, null));
        assertFalse(manager.hasListener(listener));
    }

}
