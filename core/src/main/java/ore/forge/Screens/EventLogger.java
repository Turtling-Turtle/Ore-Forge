package ore.forge.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.TimeUtils;
import ore.forge.Constants;
import ore.forge.CoolDown;
import ore.forge.EventSystem.Events.Event;
import ore.forge.FontColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Nathan Ulmen
 * The event logger will register to the eventManager and whenever an event occurs the logger will log it in the bottom right corner of the screen.
 * It will contain a scrollbox which constains a table of labels.
 * Labels will be culled after X events.
 * Events should be color coded based on event.
 * EventLogger should have functionality which allow you to turn on and off notifications for specific events
 * without having to restart the game.
 */
public class EventLogger extends WidgetGroup {
    private final SimpleDateFormat simpleDateFormat;
    private final Table logTable;
    private final ScrollPane scrollPane;
    private final static int MAX_EVENTS = 1_000;
    private final BitmapFont font2;
    private final Label.LabelStyle fpsStyle;
    private boolean autoScroll = true;
    private final CoolDown coolDown;

    public EventLogger() {
        coolDown = new CoolDown(0.3f);
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        font2 = new BitmapFont(Gdx.files.internal(Constants.FONT_FP));
        font2.setColor(Color.WHITE);
        font2.getData().markupEnabled = true;
        fpsStyle = new Label.LabelStyle(font2, Color.WHITE);

        logTable = new Table();

        logTable.setSize(Gdx.graphics.getWidth() * .3f, Gdx.graphics.getHeight() * .2f);
        logTable.setDebug(true);


        scrollPane = new ScrollPane(logTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setDebug(true);

        scrollPane.setSize(Gdx.graphics.getWidth() * .4f, Gdx.graphics.getHeight() * .2f);
        scrollPane.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                float scrollY = scrollPane.getScrollY();
                float maxScrollY = scrollPane.getMaxY();
                if (scrollY < maxScrollY) {
                    autoScroll = false;
                } else if (scrollY >= maxScrollY) {
                    autoScroll = true;
                }
                return true;
            }
        });


        this.addActor(scrollPane);
        this.setSize(Gdx.graphics.getWidth() * .3f, Gdx.graphics.getHeight() * .2f);
    }

    //TODO:Update this so that it removes events, make sure it isn't super jittery.
    public void logEvent(Event event) {
        if (logTable.getChildren().size > MAX_EVENTS) {
//            logTable.removeActor(logTable.getChildren().first());
        }
        logTable.add(createLabel(event)).top().left().fillX().expandX().row();
//        logTable.layout();
        if (autoScroll) {
            scrollPane.scrollTo(0, 0, 0, 0);
        }
//        scrollPane.updateVisualScroll();

    }

    private Label createLabel(Event event) {
        var label = new Label(formatText(event), fpsStyle);
        label.setFontScale(.55f, .55f);
        return label;
    }

    private String formatText(Event event) {
        String info = event.getBriefInfo();
        String timestamp = simpleDateFormat.format(new Date(TimeUtils.millis()));
        timestamp = FontColors.highlightString("[" + timestamp + "]", FontColors.PALE_GOLDEN_ROD);
        return timestamp + " - " + FontColors.highlightString("[" + event.eventName() + "]", event.getColor()) + " - " + info;
    }


}
