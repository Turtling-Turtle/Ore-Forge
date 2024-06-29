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
import ore.forge.Items.*;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;

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

        createIcons(inventory.getInventoryNodes());

        dropperIcons.sort(Comparator.comparingDouble(ItemIcon::getPrice));
        furnaceIcons.sort(Comparator.comparingDouble(ItemIcon::getPrice));
        processItemsIcons.sort(Comparator.comparingDouble(ItemIcon::getPrice));
        specialPointsIcons.sort(Comparator.comparingDouble(ItemIcon::getPrice));


        //Initialize "tab" buttons
        droppers = ButtonHelper.createRoundTextButton("Droppers", Color.LIGHT_GRAY);
        furnaces = ButtonHelper.createRoundTextButton("Furnaces",Color.LIGHT_GRAY);
        processItems = ButtonHelper.createRoundTextButton("Process Items", Color.LIGHT_GRAY);
        specialPoints = ButtonHelper.createRoundTextButton("Special Items", Color.LIGHT_GRAY);
        prestigeItems = ButtonHelper.createRoundTextButton("Prestige Items", Color.SKY);

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
//                updateIcons(prestigeIcons);
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

        updateIcons(dropperIcons);

        background.setSize(Gdx.graphics.getWidth() * .4f, Gdx.graphics.getHeight() * .5f);
        background.add(topTable).align(Align.topLeft).expandX().fillX().row();

        scrollPane = new ScrollPane(iconTable);
        scrollPane.setScrollingDisabled(true,false);
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
            if (node.getHeldItem().getCurrencyBoughtWith() == Currency.SPECIAL_POINTS) {
                addToList(new ItemIcon(node), specialPointsIcons);
                continue;
            }
            switch (node.getHeldItem()) {
                case Dropper ignored -> addToList(new ItemIcon(node), dropperIcons);
                case Furnace ignored -> addToList(new ItemIcon(node), furnaceIcons);
                case Upgrader ignored -> addToList(new ItemIcon(node), processItemsIcons);
                case Conveyor ignored -> addToList(new ItemIcon(node), processItemsIcons);
                default -> throw new IllegalStateException("Unexpected value: " + node.getHeldItem());
            }
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
        table.add(icon).left().fillY();
        table.add(ButtonHelper.createRoundTextButton("Buy",Color.GREEN)).right();
    }

}
