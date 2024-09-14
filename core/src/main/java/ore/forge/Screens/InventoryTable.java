package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import ore.forge.ButtonHelper;
import ore.forge.EventSystem.GameEventListener;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.NodeGameEvent;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;
import ore.forge.Screens.Widgets.ItemIcon;
import ore.forge.Stopwatch;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class InventoryTable extends WidgetGroup implements GameEventListener<NodeGameEvent> {
    private final static int ROW_COUNT = 4;
    private Comparator<ItemIcon> sortMethod;
    private final TextField searchBar;
    private final Table background, iconTable, topTable;
    private ScrollPane scrollPane;
    private final ArrayList<ItemIcon> allIcons;
    private final HashMap<String, ItemIcon> lookUp;
    private final CheckBox[] checkBoxes;
    private final static Skin buttonAtlas = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/UIButtons.atlas")));
    private static final String roundFull = "128xRoundFull";
    private final Value padValue;

    public InventoryTable(Inventory inventory) {

        lookUp = new HashMap<>();
        topTable = new Table();
        this.background = new Table();
        background.setBackground(new NinePatchDrawable(buttonAtlas.getPatch(roundFull)));
        background.setColor(Color.DARK_GRAY);
        var textFieldStyle = new TextField.TextFieldStyle();
//        textFieldStyle.font = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.background = new NinePatchDrawable(buttonAtlas.getPatch(roundFull));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/ebrimabd.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.genMipMaps = true;
        parameter.size = switch (Gdx.graphics.getHeight()) {
            case 1080 -> 20;
            case 1440 -> 32;
            case 2160 -> 40;
            default -> 22;
        };
        textFieldStyle.font = generator.generateFont(parameter);

        searchBar = new TextField("", textFieldStyle);
        searchBar.setMessageText("Search...");

        EventManager.getSingleton().registerListener(this);

        background.setSize(Gdx.graphics.getWidth() * .365f, Gdx.graphics.getHeight() * .8f);
        padValue = Value.Fixed.percentHeight(0.005f, background);


        searchBar.setTextFieldListener(new TextFieldListener() {
            String last;

            @Override
            public void keyTyped(TextField textField, char c) {
                if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) && last != null && !last.equals(textField.getText())) {
                    asyncSearch(textField.getText());
                }
                last = textField.getText();
            }
        });
        topTable.add(searchBar).top().left().size(Value.percentWidth(0.35f, background), Value.percentHeight(0.08f, background)).expand().fill().align(Align.topLeft).pad(padValue);
//        topTable.add(searchBar).top().left().size(Value.percentWidth(0.30f, background), Value.percentWidth(0.08f,background)).expand().fill().align(Align.topLeft).pad(padValue);


//        searchStack.add(searchBar);
//        searchStack.add(searchIconContainer);
//        search.setScale(0.5f, 0.5f);
//        searchIconContainer.align(Align.topRight);


//        searchStack.setDebug(true);
//        topTable.add(searchStack).top().left().size(Value.percentWidth(0.30f, background), Value.percentWidth(0.08f,background)).expand().fill().align(Align.topLeft).pad(padValue);
//        search.setSize(search.getWidth()/2f, search.getHeight()/2f);

        this.iconTable = new Table();
        checkBoxes = new CheckBox[3];

        CheckBox.CheckBoxStyle buttonStyle = new CheckBox.CheckBoxStyle();
        buttonStyle.font = searchBar.getStyle().font;
        buttonStyle.fontColor = Color.BLACK;
        buttonStyle.up = new NinePatchDrawable(buttonAtlas.getPatch(roundFull));

        checkBoxes[0] = new CheckBox("Type", buttonStyle);
        checkBoxes[0].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ButtonHelper.getButtonClickSound().play();
                if (checkBoxes[0].isChecked()) {
                    sortMethod = new TypeComparator();
                    asyncSearch(searchBar.getText());
                    for (CheckBox checkBox : checkBoxes) {
                        if (checkBox != checkBoxes[0] && checkBox != null && checkBox.isChecked()) {
                            checkBox.setChecked(false);
                        }
                    }
                } else {
                    sortMethod = null;
                }
            }
        });


        checkBoxes[1] = new CheckBox("Tier", buttonStyle);
        checkBoxes[1].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ButtonHelper.getButtonClickSound().play();
                if (checkBoxes[1].isChecked()) {
                    sortMethod = new TierComparator();
                    asyncSearch(searchBar.getText());
                    for (CheckBox checkBox : checkBoxes) {
                        if (checkBox != checkBoxes[1] && checkBox != null && checkBox.isChecked()) {
                            checkBox.setChecked(false);
                        }
                    }
                } else {
                    sortMethod = null;
                }
            }
        });

        checkBoxes[2] = new CheckBox("Stored", buttonStyle);
        checkBoxes[2].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ButtonHelper.getButtonClickSound().play();
                if (checkBoxes[2].isChecked()) {
                    sortMethod = new StoredComparator();
                    asyncSearch(searchBar.getText());
                    for (CheckBox checkBox : checkBoxes) {
                        if (checkBox != checkBoxes[2] && checkBox != null && checkBox.isChecked()) {
                            checkBox.setChecked(false);
                        }
                    }
                } else {
                    sortMethod = null;
                }
            }
        });

        this.scrollPane = new ScrollPane(this.iconTable);
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(false);
            topTable.add(checkBox).top().left().size(Value.percentWidth(0.18f, background), Value.percentHeight(0.08f, background)).expand().fill().align(Align.topLeft).padRight(padValue).padTop(padValue).padBottom(padValue);
        }
//        topTable.setBackground(new NinePatchDrawable(buttonAtlas.getPatch(roundFull)));
//        topTable.setColor(Color.BLACK);

//        topTable.add(checkBoxes[0]).top().left().expand().fill().align(Align.topLeft).pad(5);
//        topTable.add(checkBoxes[1]).top().left().expand().fill().align(Align.topLeft).pad(5);
//        topTable.add(checkBoxes[2]).top().left().expand().fill().align(Align.topLeft).pad(5);

        allIcons = new ArrayList<>();
        for (InventoryNode node : inventory.getInventoryNodes()) {
            var itemIcon = new ItemIcon(node);
            allIcons.add(itemIcon);
            lookUp.put(itemIcon.getNode().getHeldItemID(), itemIcon);
        }
        addNewIcons(allIcons);


        scrollPane.setScrollingDisabled(true, false);


        background.add(topTable).expandX().fillX().padTop(padValue).padRight(padValue).row(); //Dont pad bottom so that when we add scrollpane it doesnt double pad.
        background.add(scrollPane).top().left().expand().pad(padValue).fill();
        this.addActor(background);
//        this.debugAll();
    }

    private void asyncSearch(String target) {
        var stopwatch = new Stopwatch(TimeUnit.MICROSECONDS);
        stopwatch.start();


        final String finalizedTarget = target;
        CompletableFuture.runAsync(() -> {

            ArrayList<ItemIcon> icons;
            if (finalizedTarget.equals("Search...")) {
                icons = allIcons;
            } else {
                icons = findIcons(finalizedTarget);
            }

            if (sortMethod != null) {
                icons.sort(sortMethod);
            }
            Gdx.app.postRunnable(() -> addNewIcons(icons));
        });
        stopwatch.stop();
        Gdx.app.log("Inventory Table", ore.forge.Color.highlightString(stopwatch.toString(), ore.forge.Color.GREEN));
    }

    public ArrayList<ItemIcon> getAllIcons() {
        return allIcons;
    }

    private ArrayList<ItemIcon> findIcons(String target) {
        if (target == null || target.isEmpty()) {
            return allIcons;
        }
        target = target.toLowerCase();
        var list = new ArrayList<ItemIcon>(30);
        for (ItemIcon itemIcon : allIcons) {
            if (itemIcon.getNodeName().toLowerCase().contains(target)) {
                list.add(itemIcon);
            }
        }
        return list;
    }

    private void addNewIcons(ArrayList<ItemIcon> icons) {
        iconTable.clear();
        int count = 0;
        for (ItemIcon icon : icons) {
            if (icon.getNode().getTotalOwned() > 0) {
                addIconToTable(iconTable, icon, count++);
            }
        }
        iconTable.setFillParent(true);
        iconTable.pack();
    }

    private void addIconToTable(Table iconTable, ItemIcon icon, int count) {
        iconTable.top().left();
        if (count % ROW_COUNT == 0) {
            iconTable.row();
        }
        iconTable.add(icon).left().top().size(icon.getWidth(), icon.getHeight()).expandX().fill().align(Align.topLeft).padRight(padValue).padTop(padValue).padBottom(padValue);
    }

    public void show() {
        Gdx.app.log("InventoryTable", "Showing");
        this.addAction(Actions.sequence(Actions.show(), Actions.moveTo(Gdx.graphics.getWidth() * .643f, Gdx.graphics.getHeight() * .1f, 0.13f)));
//        assert isVisible();
    }

    public void hide() {
        Gdx.app.log("InventoryTable", "hiding");
        this.addAction(Actions.sequence(Actions.moveTo(Gdx.graphics.getWidth() * 1f, Gdx.graphics.getHeight() * .1f, 0.13f), Actions.hide()));
//        assert !isVisible();
    }

    @Override
    public void handle(NodeGameEvent event) {
        var itemIcon = lookUp.get(event.node().getHeldItemID());
        itemIcon.updateTopLeftText("Stored: " + event.node().getStored());
        asyncSearch(searchBar.getText());
    }

    @Override
    public Class<?> getEventType() {
        return NodeGameEvent.class;
    }

    static class NameComparator implements Comparator<ItemIcon> {
        @Override
        public int compare(ItemIcon node1, ItemIcon node2) {
            return node1.getNodeName().compareTo(node2.getNodeName());
        }
    }

    static class TierComparator implements Comparator<ItemIcon> {
        //Sort by Tier should sort by tier, then type, then Name.
        @Override
        public int compare(ItemIcon node1, ItemIcon node2) {
            //Tier
            int result = node1.getNode().getHeldItem().getTier().compareTo(node2.getNode().getHeldItem().getTier());
            if (result != 0) {
                return result;
            }
            //Type
            result = node1.getNode().getHeldItem().getClass().getSimpleName().
                compareTo(node2.getNode().getHeldItem().getClass().getSimpleName());
            if (result != 0) {
                return result;
            }
            //Name
            return node1.getNodeName().compareTo(node2.getNodeName());
        }
    }

    static class TypeComparator implements Comparator<ItemIcon> {
        //Sort by type should sort by type, tier,  then name.
        @Override
        public int compare(ItemIcon node1, ItemIcon node2) {
            //Type
            int result = node1.getNode().getHeldItem().getClass().getSimpleName().
                compareTo(node2.getNode().getHeldItem().getClass().getSimpleName());
            if (result != 0) {
                return result;
            }
            //Tier
            result = node1.getNode().getHeldItem().getTier().compareTo(node2.getNode().getHeldItem().getTier());
            if (result != 0) {
                return result;
            }
            //Name
            return node1.getNodeName().compareTo(node2.getNodeName());

        }
    }

    //Sort by Most to least, currently doesn't do that.
    static class StoredComparator implements Comparator<ItemIcon> {
        @Override
        public int compare(ItemIcon icon1, ItemIcon icon2) {
            //Owned
            Integer result = icon1.getNode().getStored();
            result = result.compareTo(icon2.getNode().getStored());
            result *= -1; //Flip result so that its most to least.
            if (result != 0) {
                return result;
            }
            //Type
            result = icon1.getNode().getHeldItem().getClass().getSimpleName().
                compareTo(icon1.getNode().getHeldItem().getClass().getSimpleName());
            if (result != 0) {
                return result;
            }
            //Tier
            result = icon1.getNode().getHeldItem().getTier().compareTo(icon2.getNode().getHeldItem().getTier());
            if (result != 0) {
                return result;
            }
            //Name
            return icon1.getNodeName().compareTo(icon2.getNodeName());
        }
    }

    public TextField getSearchBar() {
        return searchBar;
    }

}
