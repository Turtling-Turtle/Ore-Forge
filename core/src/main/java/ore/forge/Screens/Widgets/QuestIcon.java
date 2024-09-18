package ore.forge.Screens.Widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import ore.forge.Listener;
import ore.forge.QuestComponents.Quest;
import ore.forge.QuestComponents.QuestCondition;
import ore.forge.QuestComponents.QuestStatus;
import ore.forge.UIHelper;


/**
 * @author Nathan Ulmen
 * A quest Icon displays the name of the quest, description, current step, (step x of x), and condition.
 * For each quest condition there should be an info icon that when moused over displays the "actual" condition
 * (Condition.toString() and when the trigger is evaluated) via a tool tip.
 */
/*
 * ___________________________________________________
 * | QUEST NAME (Step X of X)            Reward      |
 * | Description                                     |
 * |   ___________________________________________   |
 * |  | * Condition description                ?  |  |
 * |  | * Condition description                ?  |  |
 * |  | * Condition description  (COMPLETED)   ?  |  |
 * |  |___________________________________________|  |
 * |_________________________________________________|
 */
public class QuestIcon extends Table implements Listener<Quest> {
    private final Table descriptionTable;
    private final Quest heldQuest;
    private final Label questName;
    private final Label questDescription;
    private final Label questStep;

    public QuestIcon(Quest quest) {
        this.heldQuest = quest;
        heldQuest.addListener(this);
        descriptionTable = new Table();
        descriptionTable.setBackground(UIHelper.getRoundFull());
        this.setSize(Gdx.graphics.getWidth() * .8f, Gdx.graphics.getHeight() * .22f);
        this.setPosition(Gdx.graphics.getWidth() * 0.15f, Gdx.graphics.getHeight() * 0.55f);

        Label.LabelStyle descriptionStyle = new Label.LabelStyle();
        descriptionStyle.font = UIHelper.generateFont(determineFontSize());
        descriptionStyle.fontColor = Color.BLACK;
        descriptionStyle.background = UIHelper.getRoundFull();

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = UIHelper.generateFont((determineFontSize() * 2));
        titleStyle.fontColor = Color.BLACK;
        titleStyle.background = UIHelper.getRoundFull();

        questName = new Label(quest.getName(), titleStyle);
        questDescription = new Label(quest.getDescription(), descriptionStyle);

        Label.LabelStyle labelStyle2 = new Label.LabelStyle();
        labelStyle2.font = UIHelper.generateFont((int) (determineFontSize() * .85f));
        labelStyle2.fontColor = Color.DARK_GRAY;
        questStep = new Label("Step " + heldQuest.currentStepIndex() + " of " + heldQuest.getTotalSteps(), labelStyle2);

        System.out.println(quest.getStatus());

        ScrollPane scrollPane = new ScrollPane(descriptionTable);

        Table topRow = new Table();

        topRow.add(questName).top().align(Align.topLeft);
        topRow.add(questStep).bottom().left().padBottom(25);

        this.add(topRow).top().left().row();
        this.add(questDescription).top().left().expandX().fillX().row();
        this.setBackground(UIHelper.getRoundFull());

        switch (quest.getStatus()) {
            case LOCKED -> configureLocked();
            case COMPLETED -> configureCompleted();
            case IN_PROGRESS -> configureInProgress();
        }

        this.add(scrollPane).top().expandX().fillX();
        scrollPane.setDebug(true);
        scrollPane.setScrollingDisabled(true, false);


    }

    private void configureLocked() {
        //How do I only adjust the height of these tables?
        for (QuestCondition condition : heldQuest.getCurrentStep().getConditions()) {
            var widget = new ConditionWidget(condition);
            descriptionTable.add(widget).top().left().row();
        }
    }

    private void configureCompleted() {

    }

    private void configureInProgress() {
        for (QuestCondition condition : heldQuest.getCurrentStep().getConditions()) {
            var widget = new ConditionWidget(condition);
            descriptionTable.add(widget).top().left().expand().fill().row();
        }
    }

    private int determineFontSize() {
        return switch (Gdx.graphics.getHeight()) {
            case 1080 -> 20;
            case 1440 -> 32;
            case 2160 -> 40;
            default -> 22;
        };
    }

    @Override
    public void update(Quest subject) {
        /*
         * TODO:
         *  update step label
         *   update state of quest (is it completed or not)
         *   update conditions if step changed
         * */
    }

    private static class ConditionWidget extends Table implements Listener<QuestCondition> {
        private final Image moreInfoIcon = new Image(UIHelper.getIcon("icon_question"));

        public ConditionWidget(QuestCondition questCondition) {
//            Skin buttonAtlas = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/Icons.atlas")));

            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = UIHelper.generateFont(determineFontSize());
            labelStyle.fontColor = Color.BLACK;
            /*TODO
             * Configure Label Style
             * */
            Label condition = new Label(questCondition.getDescription(), labelStyle);

            TextTooltip.TextTooltipStyle tooltipStyle = new TextTooltip.TextTooltipStyle();
            tooltipStyle.label = labelStyle;
            tooltipStyle.background = UIHelper.getRoundFull();
            /*TODO
             * Configure tooltip style.
             * */
            moreInfoIcon.setColor(Color.BLACK);
            var toolTip = new TextTooltip("Triggers on: " + questCondition.getEventType().getSimpleName()
                + " Event   " + "Condition: " + questCondition.getCondition().toString(), tooltipStyle);
            toolTip.setInstant(true);
            moreInfoIcon.addListener(toolTip);
            questCondition.addListener(this);

            this.add(condition).expandX().fillX().top().left().align(Align.topLeft);
            this.add(moreInfoIcon).size(50, 50).top().right().align(Align.topRight);
            this.setBackground(UIHelper.getButton("128xRoundBold"));
            this.setColor(Color.BLACK);
            questCondition.addListener(this);
//            this.setDebug(true);
        }

        @Override
        public void update(QuestCondition subject) {
            if (subject.getState() == QuestStatus.COMPLETED) {
                /*TODO
                 * Grey out elements of this quest and add a completed label.
                 * */
            }
        }

        private int determineFontSize() {
            return switch (Gdx.graphics.getHeight()) {
                case 1080 -> 16;
                case 1440 -> 26;
                case 2160 -> 32;
                default -> 22;
            };
        }
    }

}
