package ore.forge.Screens.Widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import ore.forge.Listener;
import ore.forge.QuestComponents.Quest;
import ore.forge.QuestComponents.QuestCondition;
import ore.forge.QuestComponents.QuestStatus;

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
public class QuestIcon extends Table {
    private final Table descriptionTable;
    private final Quest heldQuest;
    private Label questName, questDescription, questStep;

    public QuestIcon(Quest quest) {
        this.heldQuest = quest;
        descriptionTable = new Table();
        ScrollPane scrollPane = new ScrollPane(descriptionTable);

        /* TODO
         * Configure Background shape and color
         * Configure Font
         *
         *
         * */

        switch (quest.getStatus()) {
            case LOCKED -> configureLocked();
            case COMPLETED -> configureCompleted();
            case IN_PROGRESS -> configureInProgress();
        }


    }

    private void configureLocked() {
        for (QuestCondition condition : heldQuest.getCurrentStep().getConditions()) {
            var widget = new ConditionWidget(condition);
            descriptionTable.add(widget).row();
        }
    }

    private void configureCompleted() {

    }

    private void configureInProgress() {

    }

    private static class ConditionWidget extends Table implements Listener<QuestCondition> {

        public ConditionWidget(QuestCondition questCondition) {
            Skin buttonAtlas = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/Icons.atlas")));

            Image moreInfoIcon = new Image(buttonAtlas.getDrawable("icon_question"));

            TextTooltip.TextTooltipStyle tooltipStyle = new TextTooltip.TextTooltipStyle();
            /*TODO
             * Configure tooltip style.
             * */
            moreInfoIcon.addListener(new TextTooltip("Triggers on: " + questCondition.getEventType().getSimpleName() + " Event   " + "Condition: " + questCondition.getCondition().toString(), tooltipStyle));

            Label.LabelStyle labelStyle = new Label.LabelStyle();
            /*TODO
             * Configure Label Style
             * */
            Label condition = new Label(questCondition.getDescription(), labelStyle);

            this.add(condition).left().align(Align.topLeft);
            this.add(moreInfoIcon).right().align(Align.topRight);
            questCondition.addListener(this);
        }

        @Override
        public void update(QuestCondition quest) {
            if (quest.getState() == QuestStatus.COMPLETED) {
                /*TODO
                * Grey out elements of this quest and add a completed label.
                * */
            }
        }

    }

}
