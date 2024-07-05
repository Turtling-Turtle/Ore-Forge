package ore.forge.EventSystem.Events;

import ore.forge.FontColors;
import ore.forge.QuestComponents.QuestStep;

public record QuestStepCompletedEvent(QuestStep step) implements Event {

    @Override
    public Class getEventType() {
        return QuestStepCompletedEvent.class;
    }

    @Override
    public Object getSubject() {
        return null;
    }

    @Override
    public String getBriefInfo() {
        return "Completed " + step.getStepName() + " quest step.";
    }

    @Override
    public String getInDepthInfo() {
        return "Unimplemented";
    }

    @Override
    public String eventName() {
        return "Quest Step Completed";
    }

    @Override
    public FontColors getColor() {
        return FontColors.LIGHT_GREEN;
    }
}
