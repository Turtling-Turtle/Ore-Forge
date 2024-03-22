package ore.forge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Items.*;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;
import ore.forge.Player.Player;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

//@author Nathan Ulmen
public class ResourceManager {
    private final AssetManager assetManager;
    private final JsonReader jsonReader;
    private final HashMap<String, Sound> allSounds;
    private final HashMap<String, Item> allItems;
    private int loadCount;

    public ResourceManager() {
        loadCount = 0;
        assetManager = new AssetManager();
        jsonReader = new JsonReader();
        allSounds = new HashMap<>();
        allItems = new HashMap<>();
        long t1 = System.currentTimeMillis();
//        loadItems(Constants.CONVEYORS_FP);
//        loadItems(Constants.DROPPERS_FP);
//        loadItems(Constants.UPGRADER_FP);
//        loadItems(Constants.FURNACE_FP);
        for (Item item: allItems.values()) {
            Gdx.app.log(item.getClass().getSimpleName(), item.toString());
            System.out.println();
        }

        System.out.println("Loaded " + loadCount + " items in: " + (System.currentTimeMillis() -t1) + " ms");
    }

    public void loadItems(String fileToParse) {
        JsonValue fileContents = jsonReader.parse(Gdx.files.internal(fileToParse));
        switch (fileToParse) {
            case Constants.DROPPERS_FP:
                for (JsonValue jsonValue : fileContents) {
                    createDropper(jsonValue);
                }
                break;
            case Constants.FURNACE_FP:
                for (JsonValue jsonValue : fileContents) {
                    createFurnace(jsonValue);
                }
                break;
            case Constants.UPGRADER_FP:
                for (JsonValue jsonValue : fileContents) {
                    createUpgrader(jsonValue);
                }
                break;
            case Constants.CONVEYORS_FP:
                for (JsonValue jsonValue : fileContents) {
                    createConveyor(jsonValue);
                }
                break;
        }
    }

    private void createDropper(JsonValue jsonValue) {
        Dropper dropper = new Dropper(
            jsonValue.getString("name"),
            jsonValue.getString("description"),
            parseBlockLayout(jsonValue.get("blockLayout")),
            Item.Tier.valueOf(jsonValue.getString("tier")),
            jsonValue.getDouble("itemValue"),
            jsonValue.getString("oreName"),
            jsonValue.getDouble("oreValue"),
            jsonValue.getInt("oreTemp"),
            jsonValue.getInt("multiOre"),
            jsonValue.getFloat("dropInterval"),
            loadViaReflection(jsonValue.get("oreStrategy"), "strategyName")
        );
        loadCount++;
        allItems.put(dropper.getName(), dropper);
        System.out.println(Constants.GREEN + "Successfully Loaded: " + jsonValue.getString("name") + Constants.DEFAULT);
    }

    private void createFurnace(JsonValue jsonValue) {
        Furnace furnace = new Furnace(
            jsonValue.getString("name"),
            jsonValue.getString("description"),
            parseBlockLayout(jsonValue.get("blockLayout")),
            Item.Tier.valueOf(jsonValue.getString("tier")),
            jsonValue.getDouble("itemValue"),
            jsonValue.getInt("rewardThreshold"),
            jsonValue.getInt("specialPointReward"),
            loadViaReflection(jsonValue.get("upgrade"), "upgradeName")
        );
        loadCount++;
        allItems.put(furnace.getName(), furnace);
        System.out.println(Constants.GREEN + "Successfully Loaded: " + jsonValue.getString("name") + Constants.DEFAULT);
    }

    private void createUpgrader(JsonValue jsonValue) {
        Upgrader upgrader = new Upgrader(
            jsonValue.getString("name"),
            jsonValue.getString("description"),
            parseBlockLayout(jsonValue.get("blockLayout")),
            Item.Tier.valueOf(jsonValue.getString("tier")),
            jsonValue.getDouble("itemValue"),
            jsonValue.getFloat("conveyorSpeed"),
            loadViaReflection(jsonValue.get("upgrade"), "upgradeName"),
            new UpgradeTag(jsonValue.get("upgradeTag"))
        );
        loadCount++;
        allItems.put(upgrader.getName(), upgrader);
        System.out.println(Constants.GREEN + "Successfully Loaded: " + jsonValue.getString("name") + Constants.DEFAULT);
    }

    private void createConveyor(JsonValue jsonValue) {
        Conveyor conveyor = new Conveyor(jsonValue.getString("name"),
            jsonValue.getString("description"),
            parseBlockLayout(jsonValue.get("blockLayout")),
            Item.Tier.valueOf(jsonValue.getString("tier")),
            jsonValue.getDouble("itemValue"),
            jsonValue.getFloat("conveyorSpeed")
        );
        loadCount++;
        allItems.put(conveyor.getName(), conveyor);
        System.out.println(Constants.GREEN + "Successfully Loaded: " + jsonValue.getString("name") + Constants.DEFAULT);
    }

    private int[][] parseBlockLayout(JsonValue jsonValue) {
        int rows = jsonValue.size;
        int columns = jsonValue.get(0).size;
        int[][] blockLayout = new int[rows][columns];

        for (int i = 0; i < rows; i++) {//each row in json blockLayout
           JsonValue colum = jsonValue.get(i);//Json array representing current row
            for (int j = 0; j < columns; j++) {//each element in current row
                blockLayout[i][j] = colum.get(j).asInt();
            }
        }

        return blockLayout;
    }

    private <E> E loadViaReflection(JsonValue jsonValue, String field) {
        try {
            jsonValue.getString(field);
        } catch (NullPointerException e) {
            return null;
        }
        try {
            Class<?> aClass = Class.forName(jsonValue.getString(field));
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            return (E) constructor.newInstance(jsonValue);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Inventory createInventory() {
        JsonValue fileContents= jsonReader.parse(Gdx.files.local(Constants.INVENTORY_FP));
        ArrayList<InventoryNode> inventoryNodes = new ArrayList<>();
        if (fileContents != null) {
            for (JsonValue jsonValue: fileContents) {
                if (allItems.containsKey(jsonValue.getString("itemName"))) {
                    inventoryNodes.add(createNode(jsonValue));
                } else {
                    System.out.println(Constants.RED + jsonValue.getString("itemName") + "is not a valid Item" + Constants.DEFAULT);
                }
            }
        }

        boolean found;
        for (Item item : allItems.values()) {
            found = false;
            for(InventoryNode node: inventoryNodes) {
                if (node.getName().equals(item.getName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                inventoryNodes.add(new InventoryNode(item, 0));
            }
        }
        return new Inventory(allItems, inventoryNodes);
    }

    private InventoryNode createNode(JsonValue jsonValue) {
        Item heldItem = allItems.get(jsonValue.getString("itemName"));
        int numOwned = jsonValue.getInt("totalOwned");
        return new InventoryNode(heldItem, numOwned);
    }

    public void loadPlayerStats(Player player) {
        JsonValue fileContents = jsonReader.parse(Gdx.files.local(Constants.PLAYER_STATS_FP));
        if (fileContents != null) {
            player.setPrestigeLevel(fileContents.getInt("prestigeLevel"));
            player.setWallet(fileContents.getDouble("wallet"));
            player.setSpecialPoints(fileContents.getLong("specialPoints"));
            player.setPrestigeCurrency(fileContents.getInt("prestigeCurrency"));
            player.setMostMoneyObtained(fileContents.getDouble("mostMoneyObtained"));
        } else {
            player.setPrestigeLevel(0);
            player.setWallet(50);
            player.setSpecialPoints(0);
            player.setPrestigeCurrency(0);
            player.setMostMoneyObtained(0);
        }

    }

}
