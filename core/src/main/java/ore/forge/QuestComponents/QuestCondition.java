package ore.forge.QuestComponents;

import ore.forge.Ore;

public interface QuestCondition {
    public boolean isCompleted();

    public void checkConditions(Ore ore);

}
