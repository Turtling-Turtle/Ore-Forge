package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import ore.forge.ButtonType;
import ore.forge.EventSystem.Events.QuestStepCompletedGameEvent;
import ore.forge.EventSystem.GameEventListener;
import ore.forge.QuestComponents.Quest;
import ore.forge.QuestComponents.QuestManager;
import ore.forge.QuestComponents.QuestStatus;
import ore.forge.Screens.Widgets.QuestWidget;
import ore.forge.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestMenu extends Table implements GameEventListener<QuestStepCompletedGameEvent> {
    private final ArrayList<QuestWidget> questWidgets;
    private final HashMap<String, QuestWidget> lookup;
    private QuestStatus filterType;

    public QuestMenu(QuestManager questManager) {

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.fontColor = Color.BLACK;
        textButtonStyle.up = UIHelper.getRoundFull();
        textButtonStyle.down = UIHelper.getRoundFull();
        textButtonStyle.checked = UIHelper.getRoundFull();
        textButtonStyle.over = UIHelper.getRoundFull();
        textButtonStyle.font = UIHelper.generateFont(10);
        TextButton completed = new TextButton("Completed", textButtonStyle);
        TextButton inProgress = new TextButton("In Progress", textButtonStyle);
        TextButton locked = new TextButton("Locked", textButtonStyle);
        TextButton all = new TextButton("Unlocked", textButtonStyle);
        /*
         * Completed
         * Locked
         * Active
         * */
        Table iconTable = new Table();
        iconTable.setBackground(UIHelper.getButton(ButtonType.ROUND_FULL_128));
        iconTable.setColor(Color.ORANGE);


        questWidgets = new ArrayList<>(questManager.getAllQuests().size());
        lookup = new HashMap<>(questManager.getAllQuests().size());
        int count = 0;
        for (Quest quest : questManager.getAllQuests().values()) {
            var questIcon = new QuestWidget(quest);
            lookup.put(quest.getId(), questIcon);
            questWidgets.add(questIcon);
            count++;
            iconTable.add(questIcon).top().left().size(questIcon.getWidth(), questIcon.getHeight())
                .align(Align.topLeft).expand().fill();
            if (count % 2 == 0) {
                iconTable.row();
            }
        }

//        this.pad(2f);
//        iconTable.setFillParent(true);
        this.pad(2.75f);
        this.add(iconTable).expand().fill().top().left().align(Align.topLeft);
        this.background(UIHelper.getButton(ButtonType.ROUND_BOLD_128));
        this.setColor(Color.BLACK);

        this.setSize(Gdx.graphics.getWidth() * .85f, Gdx.graphics.getHeight() * .85f);
        this.setPosition(Gdx.graphics.getWidth() * .075f, Gdx.graphics.getHeight() * 0.075f);

//        this.debugAll();

    }

    private void foo() {
        for (QuestWidget questWidget : questWidgets) {
            if (questWidget.getStatus() == filterType) {
                //add icon to current table.
            }
        }
    }

    @Override
    public void handle(QuestStepCompletedGameEvent event) {

    }

    @Override
    public Class<?> getEventType() {
        return QuestStepCompletedGameEvent.class;
    }
}
