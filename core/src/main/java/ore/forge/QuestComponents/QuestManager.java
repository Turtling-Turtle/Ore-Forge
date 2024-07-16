package ore.forge.QuestComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.EventSystem.EventListener;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.QuestCompletedEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestManager implements EventListener<QuestCompletedEvent> {
    private HashMap<String, Quest> allQuests;
    private ArrayList<Quest> completedQuests, lockedQuests;
    private final EventManager eventManager = EventManager.getSingleton();

    public QuestManager() {
        JsonReader reader = new JsonReader();
        JsonValue allQuestData = reader.parse(Gdx.files.local("Player/Quests.json"));
        allQuests = new HashMap<>();

        for (JsonValue questData : allQuestData) {
            Quest quest = new Quest(questData);
            allQuests.put(quest.getName(), quest);
            handleQuest(quest);
        }
    }

    public void activateQuest(String questId) {
        var quest = allQuests.get(questId);
        assert quest != null && quest.getState() == QuestState.LOCKED;
        quest.start();
    }

    private void handleQuest(Quest quest) {
        switch (quest.getState()) {
            case COMPLETED -> completedQuests.add(quest);
            case LOCKED -> lockedQuests.add(quest);
            case IN_PROGRESS -> quest.initialize();
        }
    }

    public Quest getQuest(String questId) {
        return allQuests.get(questId);
    }

    @Override
    public void handle(QuestCompletedEvent event) {
        var finishedQuest = event.getSubject();
        assert finishedQuest.getState() == QuestState.COMPLETED;
        completedQuests.add(finishedQuest);
    }

    @Override
    public Class<?> getEventType() {
        return QuestCompletedEvent.class;
    }

}




