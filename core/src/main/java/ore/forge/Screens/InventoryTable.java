package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import ore.forge.ButtonHelper;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.NodeGameEvent;
import ore.forge.EventSystem.GameEventListener;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;
import ore.forge.Screens.Widgets.ItemIcon;
import ore.forge.UI.ButtonType;
import ore.forge.UI.UIHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class InventoryTable extends Table implements GameEventListener<NodeGameEvent> {
    private final static int ROW_COUNT = 4;
    private Comparator<ItemIcon> sortMethod;
    private final TextField searchBar;
    private final Table iconTable;
    private final ArrayList<ItemIcon> allIcons;
    private final HashMap<String, ItemIcon> lookUp;
    private final CheckBox[] checkBoxes;
    private final Value padValue;
    private Table topTable;

    public InventoryTable(Inventory inventory) {
        lookUp = new HashMap<>();
        topTable = new Table();
        topTable.setBackground(UIHelper.getButton(ButtonType.ROUND_BOLD_128));
        topTable.setColor(Color.BLACK);
        Table background = new Table();
        background.setBackground(UIHelper.getRoundFull());
        background.setColor(Color.DARK_GRAY);
        var textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.background = UIHelper.getRoundFull();

        textFieldStyle.font = UIHelper.generateFont(48);
        textFieldStyle.font.getData().setScale(0.5f);

        searchBar = new TextField("", textFieldStyle);
        searchBar.setMessageText("Search...");

        EventManager.getSingleton().registerListener(this);

//        background.setSize(Gdx.graphics.getWidth() * .365f, Gdx.graphics.getHeight() * .8f);
//        background.setSize(IRHelper.getWidth(0.365f), IRHelper.getHeight(0.8f));
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

//        topTable.add(searchBar).top().left().size(Value.Fixed.percentWidth(0.35f, background), Value.Fixed.percentHeight(0.08f, background)).expand().fill().align(Align.topLeft).pad(padValue);
        topTable.add(searchBar).top().left().expandX().fill().padLeft(padValue).padRight(padValue);

        this.iconTable = new Table();
        checkBoxes = new CheckBox[3];

        CheckBox.CheckBoxStyle buttonStyle = new CheckBox.CheckBoxStyle();
        buttonStyle.font = searchBar.getStyle().font;
        buttonStyle.fontColor = Color.BLACK;
        buttonStyle.up = UIHelper.getRoundFull();

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

        ScrollPane scrollPane = new ScrollPane(this.iconTable);
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(false);
//            topTable.add(checkBox).top().left().size(Value.Fixed.percentWidth(0.18f, background), Value.Fixed.percentHeight(0.08f, background)).expand().fill().align(Align.topLeft).padRight(padValue).padTop(padValue).padBottom(padValue);
            topTable.add(checkBox).top().left().padRight(padValue).padLeft(padValue).fill();
        }

        allIcons = new ArrayList<>();
        for (InventoryNode node : inventory.getInventoryNodes()) {
            var itemIcon = new ItemIcon(node);
            allIcons.add(itemIcon);
            lookUp.put(itemIcon.getNode().getHeldItemID(), itemIcon);
        }
        addNewIcons(allIcons);


        scrollPane.setScrollingDisabled(true, false);


        background.add(topTable).expandX().fillX().padTop(padValue).padRight(padValue).padLeft(padValue).row(); //Dont pad bottom so that when we add scrollPane it doesnt double pad.
//        background.add(topTable).fillX().row();

//        background.add(scrollPane).top().left().expand().padTop(padValue).padRight(padValue).fill();
        background.add(scrollPane).top().left().fill().pad(padValue).expand();

        Value widthValue = Value.Fixed.percentWidth(0.235f, topTable);
        for (int i = 0; i < ROW_COUNT; i++) {
            iconTable.columnDefaults(i).width(widthValue);
        }

        System.out.println("width value:" + widthValue.get());
        System.out.println("foo:" + topTable.getWidth() * 0.235f);

        this.setBackground(UIHelper.getButton(ButtonType.ROUND_BOLD_128));
        this.setColor(Color.BLACK);

        Value borderPad = Value.Fixed.percentHeight(0.0026f, this);
        this.pad(borderPad);
//        this.pad(Value.zero);
//        this.pad(borderPad, borderPad, borderPad, Value.zero);
        this.add(background).fill().expand();

//        this.setSize(Gdx.graphics.getWidth() * .365f, Gdx.graphics.getHeight() * .8f);
        this.setSize(IRHelper.getWidth(0.365f), IRHelper.getHeight(.8f));
    }

    public void asyncSearch(String target) {
        final String finalizedTarget = target;
        CompletableFuture.supplyAsync(() -> {
            ArrayList<ItemIcon> icons;
            if (finalizedTarget.equals("Search...")) {
                icons = allIcons;
            } else {
                icons = findIcons(finalizedTarget);
            }

            if (sortMethod != null) {
                icons.sort(sortMethod);
            }
            return icons;
        }).thenAccept(icons -> {
            Gdx.app.postRunnable(() -> addNewIcons(icons));
        });
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
//        iconTable.setFillParent(true);
//        iconTable.pack();
    }

    private void addIconToTable(Table iconTable, ItemIcon icon, int count) {
        iconTable.top().left();
        if (count % ROW_COUNT == 0) {
            iconTable.row();
        }
//        iconTable.add(icon).top().left().height(Value.Fixed.percentHeight(0.2f,this)).width(Value.Fixed.percentWidth(0.24f, topTable));
        iconTable.add(icon).height(Value.percentHeight(0.2f, this)).left().top().align(Align.topLeft).pad(padValue).colspan(1);
    }

    public void show() {
        Gdx.app.log("InventoryTable", "Showing");
//        this.setPosition(685, 92);
        this.addAction(Actions.sequence(Actions.moveTo(IRHelper.getWidth(0.63f), IRHelper.getHeight(0.1f), 0.13f)));
        System.out.println("foo:" + topTable.getWidth() * 0.235f);

        System.out.println("World Width " + this.getStage().getViewport().getWorldWidth());
        System.out.println("Screen Width " + this.getStage().getViewport().getScreenWidth());
        this.setVisible(true);
        assert this.isVisible();
    }

    public void hide() {
        Gdx.app.log("InventoryTable", "hiding");
        this.addAction(Actions.sequence(Actions.moveTo(IRHelper.getWidth(1f), IRHelper.getHeight(0.1f), 0.13f), Actions.hide()));
        this.setVisible(false);
        assert !isVisible();
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
