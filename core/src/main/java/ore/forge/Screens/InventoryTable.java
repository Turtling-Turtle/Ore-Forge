package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class InventoryTable extends WidgetGroup {
    private final TextField searchBar;
    private final Table background, iconTable;
    private ScrollPane scrollPane;
    private final ArrayList<ItemIcon> allIcons;
    private final static Skin buttonAtlas = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/UIButtons.atlas")));
    private static final String roundFull = "128xRoundFull";

    public InventoryTable(Inventory inventory) {
        var topTable = new Table();
        this.background = new Table();
        background.setBackground(new NinePatchDrawable(buttonAtlas.getPatch(roundFull)));
        background.setColor(Color.BROWN);
        var textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.background = new NinePatchDrawable(buttonAtlas.getPatch(roundFull));
        searchBar = new TextField("", textFieldStyle);
        searchBar.setMessageText("Search...");
        searchBar.setSize(300, 20);

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
        topTable.add(searchBar).align(Align.topLeft).pad(5);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
        buttonStyle.fontColor = Color.BLACK;
        buttonStyle.up = new NinePatchDrawable(buttonAtlas.getPatch(roundFull));
        TextButton button1 = new TextButton("Button1", buttonStyle);
        TextButton button2 = new TextButton("Button2", buttonStyle);
        topTable.add(button1).align(Align.topLeft).pad(5);
        topTable.add(button2).align(Align.topLeft).pad(5);

        allIcons = new ArrayList<>();
        for (InventoryNode node : inventory.getInventoryNodes()) {
            allIcons.add(new ItemIcon(node));
        }
        addNewIcons(allIcons);

        this.scrollPane = new ScrollPane(this.iconTable);
        scrollPane.setScrollingDisabled(true, false);

        background.setSize(Gdx.graphics.getWidth() * .3f, Gdx.graphics.getHeight() * .5f);

        background.add(topTable).align(Align.topLeft).expandX().fillX().row();
        background.add(scrollPane).expand().fill().row();
        this.addActor(background);
//        background.setDebug(true);
//        iconTable.setDebug(true);
//        scrollPane.setDebug(true);
    }

    private void asyncSearch(String target) {
//        var stopwatch = new Stopwatch(TimeUnit.MICROSECONDS);
//        stopwatch.start();
        final String finalizedTarget = target;
        CompletableFuture.runAsync(() -> {
            ArrayList<ItemIcon> icons = findIcons(finalizedTarget);
            Gdx.app.postRunnable(() -> addNewIcons(icons));
        });
//        stopwatch.stop();
//        Gdx.app.log("Inventory Table", ore.forge.Color.highlightString(stopwatch.toString(), ore.forge.Color.GREEN));
    }

    private ArrayList<ItemIcon> findIcons(String target) {
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
    }

    private void addIconToTable(Table iconTable, ItemIcon icon, int count) {
        if (count >= 8) {
            iconTable.row().top().left().expand().fill();
            count = 0;
        }
        iconTable.add(icon).left().top().size(icon.getWidth(), icon.getHeight()).expandX().align(Align.topLeft);
    }

    private <E extends Comparator<ItemIcon>> void quickSort(E compareType) {
        quickSort(compareType, 0, allIcons.size()-1);
    }




    private <E extends Comparator<ItemIcon>> void quickSort(E compareType,int min, int max) {
        if (min >= max) {return;}
        int indexOfPartition = partition(compareType, min, max);

        quickSort(compareType,min, indexOfPartition-1);
        quickSort(compareType,indexOfPartition+1, max);
    }

    private <E extends Comparator<ItemIcon>> int partition(E compareType, int min, int max) {
        ItemIcon partitionedElement;
        int left, right;
        int midpoint = (min+max)/2;
        partitionedElement = allIcons.get(midpoint);
        swap(midpoint,min);

        left = min;
        right = max;
        while (left < right) {
            while (left< max && compareType.compare(allIcons.get(left), partitionedElement) <= 0) {
                left++;
            }

            while (right> min && compareType.compare(allIcons.get(right), partitionedElement) > 0) {
                right--;
            }
            if (left < right) {
                swap(left,right);
            }
        }

        swap(min,right);
        return right;
    }

    private void swap(int firstIndex, int secondIndex) {
        var temp = allIcons.get(secondIndex);
        allIcons.set(secondIndex, allIcons.get(firstIndex));
        allIcons.set(firstIndex, temp);
    }


    static class NameComparator implements Comparator<ItemIcon> {
        @Override
        public int compare(ItemIcon node1, ItemIcon node2) {
            return node1.getName().compareTo(node2.getName());
        }
    }

    static class TierComparator implements Comparator<ItemIcon> {
        //Sort by Tier should sort by tier, then type, then Name.
        @Override
        public int compare(ItemIcon node1, ItemIcon node2) {
            //Tier
            int result = node1.getNode().getHeldItem().getTier().compareTo(node2.getNode().getHeldItem().getTier());
            if(result != 0) {return result;}
            //Type
            result = node1.getNode().getHeldItem().getClass().getSimpleName().
                compareTo(node2.getNode().getHeldItem().getClass().getSimpleName());
            if (result != 0) {return result;}
            //Name
            return node1.getName().compareTo(node2.getName());

        }
    }

    static class TypeComparator implements Comparator<ItemIcon> {
        //Sort by type should sort by type, tier,  then name.
        @Override
        public int compare(ItemIcon node1, ItemIcon node2) {
            //Type
            int result = node1.getNode().getHeldItem().getClass().getSimpleName().
                compareTo(node2.getNode().getHeldItem().getClass().getSimpleName());
            if (result != 0) {return result;}
            //Tier
            result = node1.getNode().getHeldItem().getTier().compareTo(node2.getNode().getHeldItem().getTier());
            if(result != 0) {return result;}
            //Name
            return node1.getName().compareTo(node2.getName());

        }
    }

    //Sort by Most to least, currently doesn't do that.
    static class StoredComparator implements Comparator<ItemIcon> {
        @Override
        public int compare(ItemIcon icon1, ItemIcon icon2) {
            Integer firstStored = icon1.getNode().getStored();
            Integer secondStored = icon2.getNode().getStored();
            return firstStored.compareTo(secondStored);
        }
    }

}
