package ore.forge.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import ore.forge.ButtonHelper;
import ore.forge.Currency;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.FailedPurchaseEvent;
import ore.forge.EventSystem.Events.PurchaseEvent;
import ore.forge.Items.*;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;
import ore.forge.Player.Player;

import java.util.ArrayList;
import java.util.Comparator;
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
public class ShopMenu extends WidgetGroup {
    private final static Player player = Player.getSingleton();
    private TextButton droppers, furnaces, processItems, specialPoints, prestigeItems;
    private ArrayList<ItemIcon> dropperIcons, furnaceIcons, processItemsIcons, specialPointsIcons, prestigeItemsIcons;
    private ScrollPane scrollPane;
    private HorizontalGroup horizontalGroup;
    private Table background, iconTable, topTable;
    private final static Skin buttonAtlas = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/UIButtons.atlas")));
    private static final String roundFull = "128xRoundFull";

    public ShopMenu(Inventory inventory) {
        dropperIcons = new ArrayList<>();
        furnaceIcons = new ArrayList<>();
        processItemsIcons = new ArrayList<>();
        specialPointsIcons = new ArrayList<>();
        prestigeItemsIcons = new ArrayList<>();

        createIcons(inventory.getInventoryNodes());

        dropperIcons.sort(Comparator.comparingDouble(ItemIcon::getPrice));
        furnaceIcons.sort(Comparator.comparingDouble(ItemIcon::getPrice));
        processItemsIcons.sort(Comparator.comparingDouble(ItemIcon::getPrice));
        specialPointsIcons.sort(Comparator.comparingDouble(ItemIcon::getPrice));


        //Initialize "tab" buttons
        droppers = ButtonHelper.createRoundTextButton("Droppers", Color.LIGHT_GRAY);
        furnaces = ButtonHelper.createRoundTextButton("Furnaces", Color.LIGHT_GRAY);
        processItems = ButtonHelper.createRoundTextButton("Process Items", Color.LIGHT_GRAY);
        specialPoints = ButtonHelper.createRoundTextButton("Special Items", Color.LIGHT_GRAY);
        prestigeItems = ButtonHelper.createRoundTextButton("Prestige Items", Color.SKY);


        droppers.setSize(0, 0);
        furnaces.setSize(0, 0);
        processItems.setSize(0, 0);
        specialPoints.setSize(0, 0);
        prestigeItems.setSize(0, 0);

        droppers.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateIcons(dropperIcons);
            }

            ;
        });

        furnaces.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateIcons(furnaceIcons);
            }

            ;
        });
        processItems.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateIcons(processItemsIcons);
            }

            ;
        });

        specialPoints.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateIcons(specialPointsIcons);
            }

            ;
        });

        prestigeItems.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateIcons(prestigeItemsIcons);
            }
        });


        //Initialize Tables
        topTable = new Table();
        background = new Table();
        iconTable = new Table();

        topTable.add(droppers).top().left().expand().fill().align(Align.topLeft).pad(5);
        topTable.add(furnaces).top().left().expand().fill().align(Align.topLeft).pad(5);
        topTable.add(processItems).top().left().expand().fill().align(Align.topLeft).pad(5);
        topTable.add(specialPoints).top().left().expand().fill().align(Align.topLeft).pad(5);
        topTable.add(prestigeItems).top().left().expand().fill().align(Align.topLeft).pad(5);
//        topTable.pack();
//        topTable.setFillParent(true);

        scrollPane = new ScrollPane(iconTable);
        updateIcons(dropperIcons);

        background.setSize(Gdx.graphics.getWidth() * .5f, Gdx.graphics.getHeight() * .5f);
        background.add(topTable).align(Align.topLeft).expandX().fillX().row();
//        background.pack();
//        background.setSize(Gdx.graphics.getWidth() * .4f, Gdx.graphics.getHeight() * .5f);
//        background.pack();


        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setVisible(true);

        background.add(scrollPane).top().left().expand().fill();
        background.setColor(Color.OLIVE);
        this.addActor(background);

        background.setDebug(true);
        iconTable.setDebug(true);
        scrollPane.setDebug(true);

        background.setBackground(new NinePatchDrawable(buttonAtlas.getPatch(roundFull)));
    }

    private void createIcons(ArrayList<InventoryNode> nodes) {
        for (InventoryNode node : nodes) {
            System.out.println(node.getName());
            if (node.getHeldItem().getCurrencyBoughtWith() == Currency.NONE) {
                assert node.getHeldItem().getUnlockMethod() == Item.UnlockMethod.QUEST;
                Gdx.app.log("SHOP MENU", node.getName() + "Is not a shop item.");
                continue;
            }

            var icon = new ItemIcon(node);
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
            } else if (node.getHeldItem().getCurrencyBoughtWith() == Currency.PRESTIGE_POINTS) {
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
        iconTable.setFillParent(true);
        iconTable.pack();
//        background.pack();
    }

    private void addIconToTable(Table iconTable, ItemIcon icon, int count) {
        iconTable.top().left();
        if (count % 8 == 0) {
            iconTable.row();
        }
        iconTable.add(icon).left().top().size(icon.getWidth(), icon.getHeight()).align(Align.topLeft).pad(5);
    }

    public void selectedWidget(ItemIcon icon) {
        var table = new Table();
        var itemPurchaseIcon = new ItemIcon(icon.getNode());
        table.add(itemPurchaseIcon).left().fillY();
//        table.add(ButtonHelper.createRoundTextButton("Buy", Color.GREEN)).right();
//        table.add(ButtonHelper.createRoundTextButton("Cancel", Color.LIGHT_GRAY)).right();
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
                var item = itemPurchaseIcon.getNode().getHeldItem();
                player.purchaseItem(item);
            }
        });
        table.add(buyButton).right().expandX().fillX();
        table.add(cancelButton).right().expandX().fillX();
        background.row();
        background.add(table).align(Align.bottomLeft).left().bottom().expandX().fillX();
    }

}
