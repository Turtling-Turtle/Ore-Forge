package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import ore.forge.ButtonType;
import ore.forge.EventSystem.Events.QuestStepCompletedGameEvent;
import ore.forge.EventSystem.GameEventListener;
import ore.forge.QuestComponents.Quest;
import ore.forge.QuestComponents.QuestManager;
import ore.forge.QuestComponents.QuestStatus;
import ore.forge.Screens.Widgets.QuestIcon;
import ore.forge.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestMenu extends Table implements GameEventListener<QuestStepCompletedGameEvent> {
    private final ArrayList<QuestIcon> questIcons;
    private final HashMap<String, QuestIcon> lookup;
    private QuestStatus filterType;

    public QuestMenu(QuestManager questManager) {
        /*
         * Completed
         * Locked
         * Active
         * */
        Table iconTable = new Table();
        iconTable.setBackground(UIHelper.getButton(ButtonType.ROUND_FULL_128));
        iconTable.setColor(Color.ORANGE);


        questIcons = new ArrayList<>(questManager.getAllQuests().size());
        lookup = new HashMap<>(questManager.getAllQuests().size());
        int count = 0;
        for (Quest quest : questManager.getAllQuests().values()) {
            var questIcon = new QuestIcon(quest);
            lookup.put(quest.getId(), questIcon);
            questIcons.add(questIcon);
            count++;
            iconTable.add(questIcon).top().left().size(questIcon.getWidth(), questIcon.getHeight()).align(Align.topLeft).pad(2f);
            if (count % 2 == 0) {
                iconTable.row();
            }
        }

//        iconTable.setFillParent(true);
        this.add(iconTable).expand().fill().top().left();
        this.background(UIHelper.getButton(ButtonType.ROUND_BOLD_128));
        this.setColor(Color.BLACK);

        this.setSize(Gdx.graphics.getWidth() * .8f, Gdx.graphics.getHeight() * .8f);
        this.setPosition(Gdx.graphics.getWidth() * .1f, Gdx.graphics.getHeight() * 0.1f);

    }

    private void foo() {
        for (QuestIcon questIcon : questIcons) {
            if (questIcon.getStatus() == filterType) {
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
