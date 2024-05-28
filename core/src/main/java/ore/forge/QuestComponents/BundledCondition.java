package ore.forge.QuestComponents;


import ore.forge.Ore;

public class BundledCondition implements QuestCondition {
    private final QuestCondition[] conditions;
    private final QuestType type;
    private boolean isCompleted;

    public BundledCondition(QuestType type, QuestCondition... conditions) {
        this.type = type;
        this.conditions = new QuestCondition[conditions.length];
        System.arraycopy(conditions, 0, this.conditions, 0, conditions.length);
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public void checkConditions(Ore ore) {
        if (type == QuestType.REQUIRED) { //means all the conditions must be true.
            for (QuestCondition condition : conditions) {
                condition.checkConditions(ore);
                if (!condition.isCompleted()) {
                    return;
                }
            }
        } else if (type == QuestType.OPTIONAL) { //means one of the conditions must be true.
            for (QuestCondition condition : conditions) {
                condition.checkConditions(ore);
                if (condition.isCompleted()) {
                    isCompleted = true;
                    return;
                }
            }
        }
    }

}
