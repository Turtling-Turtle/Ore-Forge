package ore.forge.Screens.Widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import ore.forge.QuestComponents.Quest;
import ore.forge.QuestComponents.QuestCondition;

/**
 * @author Nathan Ulmen
 * A quest Icon displays the name of the quest, description, current step, (step x of x), and condition.
 * For each quest condition there should be an info icon that when moused over displays the "actual" condition
 * (Condition.toString() and when the trigger is evaluated) via a tool tip.
 *
 *
 */
public class QuestIcon extends Table {
    private final Table background;
    private final Quest heldQuest;
    private Label questName, questDescription;

    public QuestIcon(Quest quest) {
        background = new Table();
        heldQuest = quest;
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

    }

    private void configureCompleted() {


    }

    private void configureInProgress() {

    }

    private Label createConditionLabel(Quest quest) {
        Label conditions;
        String text;
        var step = quest.getCurrentStep();
        for (QuestCondition condition : step.getConditions()) {

        }

        return null;
    }

}
