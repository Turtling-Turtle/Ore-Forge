package ore.forge.Screens.Widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import ore.forge.Listener;
import ore.forge.QuestComponents.Quest;
import ore.forge.QuestComponents.QuestCondition;
import ore.forge.QuestComponents.QuestStatus;
import ore.forge.UIHelper;

import java.util.ArrayList;

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
public class QuestIcon extends Table implements Listener<Quest>{
    private final Table descriptionTable;
    private final Quest heldQuest;
    private Label questName, questDescription, questStep;

    public QuestIcon(Quest quest) {
        this.heldQuest = quest;
        heldQuest.addListener(this);
        descriptionTable = new Table();
        descriptionTable.setBackground(UIHelper.getRoundFull());


        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = UIHelper.generateFont(determineFontSize());
        labelStyle.fontColor = Color.BLACK;
        labelStyle.background = UIHelper.getRoundFull();

        questName = new Label(quest.getName(), labelStyle);
        questDescription = new Label(quest.getDescription(), labelStyle);
        questStep = new Label("Step " + heldQuest.stepOf() + " of " + heldQuest.getStepCount(), labelStyle);

        System.out.println(quest.getStatus());
        switch (quest.getStatus()) {
            case LOCKED -> configureLocked();
            case COMPLETED -> configureCompleted();
            case IN_PROGRESS -> configureInProgress();
        }
        ScrollPane scrollPane = new ScrollPane(descriptionTable);

        this.add(questName).top().left().expandX().fillX();
        this.add(questStep).top().left().expandX().fillX().row();
        this.add(questDescription).top().left().expandX().fillX().row();
        this.setBackground(UIHelper.getRoundFull());

        this.add(scrollPane).top().left().expand().fill();
        scrollPane.setDebug(true);
        scrollPane.setScrollingDisabled(true,false);

        this.setSize(Gdx.graphics.getWidth() * .8f , Gdx.graphics.getHeight() * .2f);
        this.setPosition(Gdx.graphics.getWidth() * 0.15f, Gdx.graphics.getHeight() * 0.55f);
//        this.setDebug(true);

    }

    private void configureLocked() {
        for (QuestCondition condition : heldQuest.getCurrentStep().getConditions()) {
            var widget = new ConditionWidget(condition);
            descriptionTable.add(widget).top().left().expand().fill().row();
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
            labelStyle.background = UIHelper.getRoundFull();
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
            moreInfoIcon.addListener(new TextTooltip("Triggers on: " + questCondition.getEventType().getSimpleName()
                + " Event   " + "Condition: " + questCondition.getCondition().toString(), tooltipStyle));



            this.add(condition).expand().fill().top().left().align(Align.topLeft);
            this.add(moreInfoIcon).top().right().align(Align.topRight);
            this.setBackground(UIHelper.getRoundFull());
            this.setColor(Color.BLACK);
            questCondition.addListener(this);
            this.setDebug(true);
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
                case 1080 -> 20;
                case 1440 -> 32;
                case 2160 -> 40;
                default -> 22;
            };
        }
    }

}
