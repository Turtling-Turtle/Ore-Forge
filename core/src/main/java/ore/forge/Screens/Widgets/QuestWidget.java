package ore.forge.Screens.Widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import ore.forge.Listener;
import ore.forge.QuestComponents.Quest;
import ore.forge.QuestComponents.QuestCondition;
import ore.forge.QuestComponents.QuestStatus;
import ore.forge.QuestComponents.QuestStep;
import ore.forge.UI.ButtonType;
import ore.forge.UI.UIHelper;


/**
 * @author Nathan Ulmen
 * A quest Icon displays the name of the quest, description, current step, (step x of x), and condition.
 * For each quest condition there should be an info icon that when moused over displays the "actual" condition
 * (Condition.toString() and when the trigger is evaluated) via a tool tip.
 */
/*
 * ___________________________________________________
 * | QUEST NAME <-(Step X of X)->        Reward      |
 * | Description                                     |
 * |   ___________________________________________   |
 * |  | * Condition description                ?  |  |
 * |  | * Condition description                ?  |  |
 * |  | * Condition description  (COMPLETED)   ?  |  |
 * |  |___________________________________________|  |
 * |_________________________________________________|
 * Reward will display Item Icon if Item
 */
public class QuestWidget extends Table implements Listener<Quest> {
    private final Table descriptionTable;
    private final Quest heldQuest;
    private final Label questName;
    private final Label questDescription;
    private final Label questStep;
    private int index;


    /*
     * TODO:
     *  Figure out a way to shrink/scale down font size for descriptions that are super long to prevent excessive wrapping.
     *  Figure out a way to better scale
     * */

    public QuestWidget(Quest quest) {
        index = 0;
        this.heldQuest = quest;
        heldQuest.addListener(this);
        descriptionTable = new Table();
        this.setSize(Gdx.graphics.getWidth() * .38f, Gdx.graphics.getHeight() * .25f);
        this.setPosition(Gdx.graphics.getWidth() * 0.15f, Gdx.graphics.getHeight() * 0.55f);

        Label.LabelStyle descriptionStyle = new Label.LabelStyle();
        descriptionStyle.font = UIHelper.generateFont((int) (determineFontSize() * .65f));
        descriptionStyle.fontColor = Color.BLACK;
        descriptionStyle.background = UIHelper.getRoundFull();

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = UIHelper.generateFont((int) (determineFontSize() * 1.55));
        titleStyle.fontColor = Color.BLACK;
        titleStyle.background = UIHelper.getRoundFull();

        questName = new Label(quest.getName(), titleStyle);
        questDescription = new Label(quest.getDescription(), descriptionStyle);
        questDescription.setWrap(true);

        Label.LabelStyle stepOfStyle = new Label.LabelStyle();
        stepOfStyle.font = UIHelper.generateFont((int) (determineFontSize() * .75f));
        stepOfStyle.fontColor = Color.DARK_GRAY;
        questStep = new Label("Step " + heldQuest.currentStepNumber() + " of " + heldQuest.getTotalSteps(), stepOfStyle);

        System.out.println(quest.getStatus());

        ScrollPane scrollPane = new ScrollPane(descriptionTable);

        Table topRow = new Table();


        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("UIAssets/Icons.atlas"));
        Skin skin = new Skin(atlas);
        Sprite previousSprite = new Sprite(skin.getRegion("icon_next_thin"));
        previousSprite.flip(true, true);
        Image previousStepButton = new Image(previousSprite);
        previousStepButton.setColor(Color.BLACK);
//        previousStepButton.setDebug(true);

        Image nextStepButton = new Image(UIHelper.getIcon("icon_next_thin"));
        nextStepButton.setColor(Color.BLACK);
//        nextStepButton.setDebug(true);

        float dimensions = questStep.getHeight();

        topRow.pad(0);
        topRow.add(questName).top().align(Align.topLeft);
        topRow.add(previousStepButton).bottom().left().size(dimensions, dimensions).padBottom(25).padRight(1f);
        topRow.add(questStep).bottom().left().padBottom(25);
        topRow.add(nextStepButton).bottom().left().size(dimensions, dimensions).padBottom(25).padLeft(1f);


        this.add(topRow).top().left().row();
        this.add(questDescription).top().left().pad(0).expandX().fillX().row();
        this.setBackground(UIHelper.getRoundFull());


        this.update(heldQuest);

//        this.add(scrollPane).top().expandX().fillX();
//        scrollPane.setDebug(true);
        Table scrollBorder = new Table();
        scrollBorder.background(UIHelper.getButton(ButtonType.ROUND_BOLD_128));
        scrollBorder.setColor(Color.BLACK);
        scrollBorder.add(scrollPane).size(scrollBorder.getWidth(), this.getHeight() / 2f).expand().fill();
//        this.add(scrollBorder).top().expand().fill().minHeight(this.getHeight() / 2f);
        this.add(scrollBorder).expand().fill();
        scrollPane.setScrollingDisabled(true, false);

//        this.debug();

    }

    private void configureLocked() {
//        for (QuestCondition condition : heldQuest.getCurrentStep().getConditions()) {
//            var widget = new ConditionWidget(condition);
//            descriptionTable.add(widget).top().left().row();
//        }
        var locked = new Image(UIHelper.getIcon("icon_lock"));
        locked.setColor(Color.BLACK);
        float dimensions = locked.getHeight();
        descriptionTable.add(locked).size(dimensions, dimensions);
    }

    private void configureCompleted() {

    }

    private void configureInProgress(QuestStep step) {
        descriptionTable.clear();
        for (QuestCondition condition : step.getConditions()) {
            var widget = new ConditionWidget(condition);
            descriptionTable.add(widget).top().expand().fill().left().pad(5f).row();
        }
        questStep.setText("Step " + heldQuest.currentStepNumber() + " of " + heldQuest.getTotalSteps());
    }

    private int determineFontSize() {
        return switch (Gdx.graphics.getHeight()) {
            case 1080 -> 18;
            case 1440 -> 30;
            case 2160 -> 38;
            default -> 22;
        };
    }


    @Override
    public void update(Quest subject) {
        switch (subject.getStatus()) {
            case IN_PROGRESS -> {
                configureInProgress(subject.getCurrentStep());
            }
            case LOCKED -> {
                configureLocked();
                //Set widget to be grayed out and have lock icon over it.
            }
            case COMPLETED -> {
                this.setColor(Color.OLIVE);
            }
        }
    }

    public QuestStatus getStatus() {
        return heldQuest.getStatus();
    }

    public void changeStep(int step) {
        var questSteps = heldQuest.getQuestSteps();
        if (step <= questSteps.size() && step >= 0) {
            var questStep = questSteps.get(step);
            configureInProgress(questStep);
        }
    }

    private static class ConditionWidget extends Table implements Listener<QuestCondition> {
        private final Image moreInfoIcon = new Image(UIHelper.getIcon("icon_question"));
        private Label condition;

        public ConditionWidget(QuestCondition questCondition) {
//            Skin buttonAtlas = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/Icons.atlas")));

            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = UIHelper.generateFont(determineFontSize());
            labelStyle.fontColor = Color.BLACK;

            /*TODO
             * Configure Label Style
             * */
            condition = new Label(questCondition.getDescription(), labelStyle);

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

            this.add(condition).expandX().fillX().top().left().align(Align.topLeft);
            this.add(moreInfoIcon).size(50, 50).top().right().align(Align.topRight);
            this.setBackground(UIHelper.getButton(ButtonType.ROUND_BOLD_128));
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

                condition.getStyle().fontColor = Color.LIGHT_GRAY;
                Label.LabelStyle labelStyle = new Label.LabelStyle();
                labelStyle.font = UIHelper.generateFont(determineFontSize());
                labelStyle.fontColor = Color.OLIVE;
//                this.addActorBefore(new Label("Completed", labelStyle), moreInfoIcon);

                this.clear();
                this.add(condition).expandY().fillY().top().left().align(Align.topLeft).padRight(10f);
                this.add(new Label("Completed", labelStyle)).expand().fill().top().left().align(Align.topLeft);
                this.add(moreInfoIcon).size(50, 50).right().align(Align.right);
                System.out.println("Quest Widget Updated");
            }
        }

        private int determineFontSize() {
            return switch (Gdx.graphics.getHeight()) {
                case 1080 -> 16;
                case 1440 -> 28;
                case 2160 -> 32;
                default -> 22;
            };
        }
    }

}
