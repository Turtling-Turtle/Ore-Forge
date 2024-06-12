package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import ore.forge.ButtonHelper;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InventoryTable extends WidgetGroup {
    private Comparator<ItemIcon> sortMethod;
    private final TextField searchBar;
    private final Table background, iconTable, topTable;
    private final HorizontalGroup horizontalGroup;
    private ScrollPane scrollPane;
    private final ArrayList<ItemIcon> allIcons;
    private final CheckBox[] checkBoxes;
    private final static Skin buttonAtlas = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/UIButtons.atlas")));
    private static final String roundFull = "128xRoundFull";

    public InventoryTable(Inventory inventory) {
        topTable = new Table();
        this.background = new Table();
        background.setBackground(new NinePatchDrawable(buttonAtlas.getPatch(roundFull)));
        background.setColor(Color.GRAY);
        var textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.background = new NinePatchDrawable(buttonAtlas.getPatch(roundFull));
        searchBar = new TextField("", textFieldStyle);
        searchBar.setMessageText("Search...");

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
        this.iconTable = new Table();
        topTable.add(searchBar).top().left().expand().fill().align(Align.topLeft).pad(5);

        checkBoxes = new CheckBox[3];
        horizontalGroup = new HorizontalGroup();
        horizontalGroup.align(Align.topLeft);

        CheckBox.CheckBoxStyle buttonStyle = new CheckBox.CheckBoxStyle();
        buttonStyle.font = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
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
        }

        topTable.add(checkBoxes[0]).top().left().expand().fill().align(Align.topLeft).pad(5);
        topTable.add(checkBoxes[1]).top().left().expand().fill().align(Align.topLeft).pad(5);
        topTable.add(checkBoxes[2]).top().left().expand().fill().align(Align.topLeft).pad(5);

        allIcons = new ArrayList<>();
        for (InventoryNode node : inventory.getInventoryNodes()) {
            allIcons.add(new ItemIcon(node));
        }
        addNewIcons(allIcons);


        scrollPane.setScrollingDisabled(true, false);
        horizontalGroup.setVisible(true);

        background.setSize(Gdx.graphics.getWidth() * .3f, Gdx.graphics.getHeight() * .5f);

        background.add(topTable).align(Align.topLeft).expandX().fillX().row();
        background.add(scrollPane).top().left().expand().fill();
        this.addActor(background);
        background.setDebug(true);
//        horizontalGroup.setDebug(true);
        iconTable.setDebug(true);
        scrollPane.setDebug(true);
    }

    //So right now I have an inventory Component of my user interface that has a searchbar that you can use to look up specific items.
    //Currently, in my application everytime a frame is updated/when a new frame is drawn the game goes through the game loop which is in charge of drawing and updating all the actors in my game.
    //This means that every frame the game checks to see if it needs to search and sort a set of items.
    //I want to make the search feature of the code asynchronous so that it doesn't cause any stutters when you search for an item as its rather resource intensive based off profiling that ive done (its O(n log n))
    //Is it possible for the program to have multiple different versions of this task running at the same time? I think it would with my current knowledge, and I was wondering if it is possible to limit it so that only one task is running?

    private void asyncSearch(String target) {
//        var stopwatch = new Stopwatch(TimeUnit.MICROSECONDS);
//        stopwatch.start();
        String finalizedTarget = target;
        CompletableFuture.runAsync(() -> {
                ArrayList<ItemIcon> icons = findIcons(finalizedTarget);
                if (sortMethod != null) {
                    quickSort(icons, sortMethod);
                }
                Gdx.app.postRunnable(() -> addNewIcons(icons));
        });
//        stopwatch.stop();
//        Gdx.app.log("Inventory Table", ore.forge.Color.highlightString(stopwatch.toString(), ore.forge.Color.GREEN));
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
            addIconToTable(iconTable, icon, count++);
        }
        iconTable.setFillParent(true);
        iconTable.pack();
    }

    private void addIconToTable(Table iconTable, ItemIcon icon, int count) {
        iconTable.top().left();
        if (count >= 8) {
            iconTable.row();
            count = 0;
        }
        iconTable.add(icon).left().top().size(icon.getWidth(),icon.getHeight()).align(Align.topLeft).pad(5);
    }

    private <E extends Comparator<ItemIcon>> void quickSort(List<ItemIcon> icons, E compareType) {
        quickSort(icons, compareType, 0, icons.size() - 1);
    }

    private <E extends Comparator<ItemIcon>> void quickSort(List<ItemIcon> icons, E compareType, int min, int max) {
        if (min >= max) {
            return;
        }
        int indexOfPartition = partition(icons, compareType, min, max);

        quickSort(icons, compareType, min, indexOfPartition - 1);
        quickSort(icons, compareType, indexOfPartition + 1, max);
    }

    private <E extends Comparator<ItemIcon>> int partition(List<ItemIcon> icons, E compareType, int min, int max) {
        ItemIcon partitionedElement;
        int left, right;
        int midpoint = (min + max) / 2;
        partitionedElement = icons.get(midpoint);

        swap(icons, midpoint, min);

        left = min;
        right = max;

        while (left < right) {
            while (left < max && compareType.compare(icons.get(left), partitionedElement) <= 0) {
                left++;
            }

            while (right > min && compareType.compare(icons.get(right), partitionedElement) > 0) {
                right--;
            }
            if (left < right) {
                swap(icons, left, right);
            }
        }

        swap(icons, min, right);
        return right;
    }

    private void swap(List<ItemIcon> icons, int firstIndex, int secondIndex) {
        ItemIcon temp = icons.get(secondIndex);
        icons.set(secondIndex, icons.get(firstIndex));
        icons.set(firstIndex, temp);
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
            Integer result = icon1.getNode().getTotalOwned();
            result = result.compareTo(icon2.getNode().getTotalOwned());
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
