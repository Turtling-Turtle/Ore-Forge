package ore.forge.EventSystem.Events;

import ore.forge.FontColors;
import ore.forge.QuestComponents.Quest;

public record QuestCompletedEvent(Quest compeletedQuest) implements Event<Quest> {

    @Override
    public Class getEventType() {
        return QuestCompletedEvent.class;
    }

    public Quest getSubject() {
        return compeletedQuest;
    }

    @Override
    public String getBriefInfo() {
        return "Unimplemented";
    }

    @Override
    public String getInDepthInfo() {
        return "Unimplemented";
    }

    @Override
    public String eventName() {
        return "Quest Completed";
    }

    @Override
    public FontColors getColor() {
        return FontColors.FOREST_GREEN;
    }
}
