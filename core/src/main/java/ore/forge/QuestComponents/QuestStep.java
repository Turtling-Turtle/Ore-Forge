package ore.forge.QuestComponents;

import ore.forge.Ore;

public interface QuestStep {
    public QuestType getStepType();

    public boolean isCompleted();

    public void checkCondition(Ore ore);

    public void grantReward();
}
