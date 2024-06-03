package ore.forge.EventSystem.Events;

import ore.forge.QuestComponents.Quest;

public record QuestCompletedEvent(Quest compeletedQuest) implements Event<Quest> {

    @Override
    public Quest getSubject() {
        return compeletedQuest;
    }
}
