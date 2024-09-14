package ore.forge.Screens.Widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import ore.forge.Listener;
import ore.forge.QuestComponents.Quest;
import ore.forge.QuestComponents.QuestCondition;
import ore.forge.QuestComponents.QuestStep;

import javax.swing.*;

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
public class QuestIcon extends Table implements Listener {
    private final Table descriptionTable;
    private final Quest heldQuest;
    private Label questName, questDescription, questStep;

    public QuestIcon(Quest quest) {
        this.heldQuest = quest;
        descriptionTable = new Table();
        ScrollPane scrollPane = new ScrollPane(descriptionTable);

        /*
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
            descriptionTable.add(createConditionWidget(condition)).row();
        }

    }

    private void configureCompleted() {

    }

    private void configureInProgress() {

    }

    private Actor createConditionWidget(QuestCondition questCondition) {
        Table widget = new Table();
        Skin buttonAtlas = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/Icons.atlas")));

        Image moreInfoIcon = new Image(buttonAtlas.getDrawable("icon_question"));

        TextTooltip.TextTooltipStyle tooltipStyle = new TextTooltip.TextTooltipStyle();
        /*
        * Configure tooltip style.
        * */
        moreInfoIcon.addListener(new TextTooltip("Triggers on: " + questCondition.getEventType().getSimpleName() + " Event   " + "Condition: " + questCondition.getCondition().toString(), tooltipStyle));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        /*
         * Configure Label Style
         * */
        Label condition = new Label(questCondition.getDescription(), labelStyle);

        widget.add(condition).left().align(Align.topLeft);
        widget.add(moreInfoIcon).right().align(Align.topRight);

        return widget;
    }

    @Override
    public void update() {

    }
}
