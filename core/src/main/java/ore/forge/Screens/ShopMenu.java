package ore.forge.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import ore.forge.ButtonHelper;
import ore.forge.Currency;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.NodeGameEvent;
import ore.forge.EventSystem.GameEventListener;
import ore.forge.Items.*;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;
import ore.forge.Player.Player;
import ore.forge.Screens.Widgets.ItemIcon;
import ore.forge.UI.ButtonType;
import ore.forge.UI.UIHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/*
 * X "tabs" at the top, one for each of the following: Droppers, Furnaces, Upgraders/Conveyors, Special Points.
 * Below the tabs will be a search bar which searches the current group/tab of items.
 * Items will be sorted by price.
 * Clicking an item will bring up a menu/tab that has two arrow buttons that allow you to increase the number of that item bought.
 * Can also enter a number manually.
 *
 * Hovering over an Item will display a widget giving info on the item.
 * Locked Items will be greyed out??
 *
 *
 *
 * */
public class ShopMenu extends Table implements GameEventListener<NodeGameEvent> {
    private final static Player player = Player.getSingleton();
    private final TextButton droppers, furnaces, processItems, specialPoints, prestigeItems;
    private final ArrayList<ItemIcon> dropperIcons, furnaceIcons, processItemsIcons, specialPointsIcons, prestigeItemsIcons;
    private final HashMap<String, ItemIcon> iconLookUp;
    private final ScrollPane scrollPane;
    private final Table background, iconTable, topTable;
    private final Value padValue;

    public ShopMenu(Inventory inventory) {
        dropperIcons = new ArrayList<>();
        furnaceIcons = new ArrayList<>();
        processItemsIcons = new ArrayList<>();
        specialPointsIcons = new ArrayList<>();
        prestigeItemsIcons = new ArrayList<>();
        iconLookUp = new HashMap<>();

        createIcons(inventory.getInventoryNodes());

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = UIHelper.generateFont(48);
        style.up = UIHelper.getRoundFull();
        style.down = UIHelper.getRoundFull();
        style.over = UIHelper.getRoundFull();
        style.pressedOffsetY = -2f;
        style.fontColor = Color.BLACK;

        dropperIcons.sort(Comparator.comparingDouble(ItemIcon::getPrice));
        furnaceIcons.sort(Comparator.comparingDouble(ItemIcon::getPrice));
        processItemsIcons.sort(Comparator.comparingDouble(ItemIcon::getPrice));
        specialPointsIcons.sort(Comparator.comparingDouble(ItemIcon::getPrice));

        style.font.getData().setScale(.5f);


        //Initialize "tab" buttons
        droppers = new TextButton("Droppers", style);
        furnaces = new TextButton("Furnaces", style);
        processItems = new TextButton("Process Items", style);
        specialPoints = new TextButton("Special Items", style);
        prestigeItems = new TextButton("Prestige Items", style);
        Button[] widgets = new Button[5];
        widgets[0] = droppers;
        widgets[1] = furnaces;
        widgets[2] = processItems;
        widgets[3] = specialPoints;
        widgets[4] = prestigeItems;




        droppers.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateIcons(dropperIcons);
            }

        });

        furnaces.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateIcons(furnaceIcons);
            }

        });

        processItems.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateIcons(processItemsIcons);
            }

        });

        specialPoints.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateIcons(specialPointsIcons);
            }

        });

        prestigeItems.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateIcons(prestigeItemsIcons);
            }
        });


        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = style.font;
        textFieldStyle.background = UIHelper.getRoundFull();
        textFieldStyle.fontColor = Color.BLACK;
        TextField searchBar = new TextField("Search...", textFieldStyle);

        //Initialize Tables
        topTable = new Table();
        background = new Table();
        iconTable = new Table();

        background.setSize(IRHelper.getWidth(.58f), IRHelper.getHeight(0.8f));
        padValue = Value.Fixed.percentHeight(0.005f, background);


        for (Button button : widgets) {
            topTable.add(button).top().left().expandX().fillX().padLeft(padValue).padRight(padValue);
        }
        topTable.row();
        topTable.add(searchBar).fill().top().left().colspan(3).padRight(padValue).padLeft(padValue).padTop(padValue);

        topTable.setBackground(UIHelper.getButton(ButtonType.ROUND_BOLD_128));
        topTable.setColor(Color.BLACK);

//        topTable.setDebug(true);

        scrollPane = new ScrollPane(iconTable);
        updateIcons(dropperIcons);


        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setVisible(true);

        background.add(topTable).top().left().expandX().fillX().padTop(padValue).padRight(padValue).padLeft(padValue).row();
        background.add(scrollPane).top().left().fill().pad(padValue).expand();

        for (int i = 0; i < 6; i++) {
            iconTable.columnDefaults(i).width(IRHelper.getWidth(0.0784f));
        }

        background.setColor(Color.OLIVE);
        background.setBackground(UIHelper.getRoundFull());
        this.setBackground(UIHelper.getButton(ButtonType.ROUND_BOLD_128));
        this.setColor(Color.BLACK);
//        this.pad(2.4f, 0, 2.4f, 2.5f);
        this.pad(Value.Fixed.percentHeight(0.0026f, this));

        this.add(background).fill().expand();
        this.setSize(IRHelper.getWidth(.58f), IRHelper.getHeight(0.8f));


        EventManager.getSingleton().registerListener(this);
    }

    private void createIcons(ArrayList<InventoryNode> nodes) {
        for (InventoryNode node : nodes) {
            if (node.getHeldItem().getCurrencyBoughtWith() == Currency.NONE) {
                assert node.getHeldItem().getUnlockMethod() == Item.UnlockMethod.QUEST;
                Gdx.app.log("SHOP MENU", node.getName() + "Is not a shop item.");
                continue;
            }

            var icon = new ItemIcon(node);
            iconLookUp.put(node.getHeldItemID(), icon);
            icon.updateTopLeftText("Owned: " + node.getTotalOwned());
            icon.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedWidget(icon);
                }
            });

            if (node.getHeldItem().getCurrencyBoughtWith() == Currency.SPECIAL_POINTS) {
                Gdx.app.log("SHOP MENU", "Added " + icon.getNodeName() + " to Special Point Items");
                addToList(icon, specialPointsIcons);
                continue;
            } else if (node.getHeldItem().getCurrencyBoughtWith() == Currency.PRESTIGE_POINTS || node.getHeldItem().getTier() == Item.Tier.PRESTIGE) {
                Gdx.app.log("SHOP MENU", "Added " + icon.getNodeName() + " to Prestige Items");
                addToList(icon, prestigeItemsIcons);
                continue;
            }
            switch (node.getHeldItem()) {
                case Dropper ignored -> addToList(icon, dropperIcons);
                case Furnace ignored -> addToList(icon, furnaceIcons);
                case Upgrader ignored -> addToList(icon, processItemsIcons);
                case Conveyor ignored -> addToList(icon, processItemsIcons);
                default -> throw new IllegalStateException("Unexpected value: " + node.getHeldItem());
            }
            Gdx.app.log("SHOP MENU", "Added " + icon.getNodeName() + " to Normal List");
        }
    }

    private void addToList(ItemIcon icon, List<ItemIcon> list) {
        list.add(icon);
    }

    private void updateIcons(ArrayList<ItemIcon> icons) {
        iconTable.clear();
        int count = 0;
        for (ItemIcon icon : icons) {
            addIconToTable(iconTable, icon, count++);
        }
//        iconTable.setFillParent(true);
//        iconTable.pack();
    }

    private void addIconToTable(Table iconTable, ItemIcon icon, int count) {
        iconTable.top().left();
        if (count % 6 == 0) {
            iconTable.row();
        }

        iconTable.add(icon).height(Value.percentHeight(0.2f, this)).left().top().align(Align.topLeft).pad(padValue).colspan(1);
//        iconTable.add(icon).left().top().size(icon.getWidth(), icon.getHeight()).expandX().fill().align(Align.topLeft).padLeft(padValue).padTop(padValue).padBottom(padValue);
//        iconTable.add(icon).left().top().size(icon.getWidth(), icon.getHeight()).align(Align.topLeft).pad(padValue);
    }

    public void selectedWidget(ItemIcon icon) {
        VerticalGroup verticalGroup = new VerticalGroup();

        Button upButton = ButtonHelper.createRoundTextButton("Up", Color.LIGHT_GRAY);
        Button downButton = ButtonHelper.createRoundTextButton("Down", Color.LIGHT_GRAY);


        var textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.background = UIHelper.getRoundFull();
        TextField count = new TextField("1", textFieldStyle);
        TextField.TextFieldFilter filter = new TextField.TextFieldFilter.DigitsOnlyFilter();
        count.setTextFieldFilter(filter);
//        count.setMessageText("1");

        upButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int newCount;
                if (count.getText().isEmpty()) {
                    newCount = 1;
                } else {
                    newCount = Integer.valueOf(count.getText()).intValue();
                    newCount++;
                }
                count.setText(String.valueOf(newCount));
            }
        });

        downButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int newCount;
                if (count.getText().isEmpty()) {
                    newCount = 1;
                } else {
                    newCount = Integer.parseInt(count.getText());
                }
                if (newCount > 1) {
                    newCount--;
                }
                count.setText(String.valueOf(newCount));
            }
        });

        verticalGroup.addActor(upButton);
        verticalGroup.addActor(count);
        verticalGroup.addActor(downButton);


        var table = new Table();
        var itemPurchaseIcon = new ItemIcon(icon.getNode());
        table.add(itemPurchaseIcon).left().fillY();
        var cancelButton = ButtonHelper.createRoundTextButton("Cancel", Color.LIGHT_GRAY);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                background.removeActor(table);
            }
        });

        var buyButton = ButtonHelper.createRoundTextButton("Buy", Color.LIGHT_GRAY);
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!count.getText().isEmpty()) {
                    int numberToBuy = Integer.parseInt(count.getText());
                    if (numberToBuy >= 1) {
                        var item = itemPurchaseIcon.getNode().getHeldItem();
                        player.purchaseItem(item, numberToBuy);
                    }
                }
            }
        });

        var purchaseVbox = new VerticalGroup();
        purchaseVbox.addActor(buyButton);
        purchaseVbox.addActor(cancelButton);
        table.add(verticalGroup).right().expand().fill();
//        table.add(buyButton).right();
//        table.add(cancelButton).right();
        table.add(purchaseVbox).right().expand().fill();
//        table.setDebug(true, true);
        background.row();
        background.add(table).align(Align.bottomLeft).left().bottom().expandX().fillX();
//        this.add(background).fill().expand();

    }

    public void show() {
        this.addAction(Actions.sequence(Actions.show(), Actions.moveTo(IRHelper.getWidth(0.005f), IRHelper.getHeight(0.1f), .13f)));
//        this.addAction(Actions.sequence(Actions.show(), Actions.moveTo(Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * .1f, 0.13f)));
    }

    public void hide() {
        this.addAction(Actions.sequence(Actions.moveTo(this.getWidth() - IRHelper.getWidth(1f), IRHelper.getHeight(0.1f), .13f), Actions.hide()));
//        this.addAction(Actions.sequence(Actions.moveTo(this.getWidth() - Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * .1f, 0.13f), Actions.hide()));
    }

    @Override
    public void handle(NodeGameEvent event) {
        var icon = iconLookUp.get(event.node().getHeldItemID());
        if (icon != null) {
            icon.updateTopLeftText("Owned: " + event.node().getTotalOwned());
            //TODO? update the sorted order of the list.
        }

    }

    private void purchaseCountButton() {
        VerticalGroup verticalGroup = new VerticalGroup();

        Button upButton = ButtonHelper.createRoundTextButton("Up", Color.LIGHT_GRAY);
        Button downButton = ButtonHelper.createRoundTextButton("Down", Color.LIGHT_GRAY);


        var textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.background = UIHelper.getRoundFull();
        TextField count = new TextField("", textFieldStyle);
        TextField.TextFieldFilter filter = new TextField.TextFieldFilter.DigitsOnlyFilter();
        count.setTextFieldFilter(filter);
        count.setMessageText("1");

        upButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int newCount = Integer.valueOf(count.getText()).intValue();
                newCount++;
                count.setText(String.valueOf(newCount));
            }
        });

        downButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int newCount = Integer.valueOf(count.getText()).intValue();
                if (newCount > 1) {
                    newCount--;
                }
                count.setText(String.valueOf(newCount));
            }
        });

        verticalGroup.addActor(upButton);
        verticalGroup.addActor(count);
        verticalGroup.addActor(downButton);

    }

    @Override
    public Class<?> getEventType() {
        return NodeGameEvent.class;
    }

}

