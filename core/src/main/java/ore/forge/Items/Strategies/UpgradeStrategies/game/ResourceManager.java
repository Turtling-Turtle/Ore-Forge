package ore.forge.game.Items.Strategies.UpgradeStrategies.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.*;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies.*;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies.PrimaryUPGS.AddUPG;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies.PrimaryUPGS.MultiplyUPG;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies.PrimaryUPGS.SubtractUPG;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Player.Inventory;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Player.InventoryNode;
import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Player.Player;

import java.util.ArrayList;
import java.util.HashMap;

//@author Nathan Ulmen
public class ResourceManager {
    private final AssetManager assetManager;
    private final JsonReader jsonReader;
    private final HashMap<String, Sound> allSounds;
    private final HashMap<String, Item> allItems;

    public ResourceManager() {
        assetManager = new AssetManager();
        jsonReader = new JsonReader();
        allSounds = new HashMap<>();
        allItems = new HashMap<>();
        loadItems(Constants.CONVEYORS_FP);
        loadItems(Constants.DROPPERS_FP);
        loadItems(Constants.UPGRADER_FP);
        loadItems(Constants.FURNACE_FP);
        for (Item item: allItems.values()) {
            Gdx.app.log("Name", item.toString());
        }
    }

    public void loadItems(String fileToParse) {
        JsonValue fileContents = jsonReader.parse(Gdx.files.internal(fileToParse));
        for (JsonValue jsonValue : fileContents) {
            createItem(jsonValue, fileToParse);
        }
    }

    private void createItem(JsonValue jsonValue, String fileToParse) {
        switch (fileToParse) {
            case Constants.DROPPERS_FP:
                Dropper dropper = new Dropper(jsonValue.getString("name"),
                        jsonValue.getString("description"),
                        parseBlockLayout(jsonValue.get("blockLayout")),
                        Item.Tier.valueOf(jsonValue.getString("tier")),
                        jsonValue.getDouble("itemValue"),
                        jsonValue.getString("oreName"),
                        jsonValue.getDouble("oreValue"),
                        jsonValue.getInt("oreTemp"),
                        jsonValue.getInt("multiOre"),
                        jsonValue.getFloat("dropInterval"));
                allItems.put(dropper.getName(), dropper);
                break;
            case Constants.FURNACE_FP:
                Furnace furnace = new Furnace(jsonValue.getString("name"),
                        jsonValue.getString("description"),
                        parseBlockLayout(jsonValue.get("blockLayout")),
                        Item.Tier.valueOf(jsonValue.getString("tier")),
                        jsonValue.getDouble("itemValue"),
                        createUpgradeStrategy(jsonValue.get("upgrade")),
                        jsonValue.getInt("specialPointReward"),
                        jsonValue.getInt("rewardThreshold"));
                allItems.put(furnace.getName(), furnace);
                break;
            case Constants.UPGRADER_FP:
                Upgrader upgrader = new Upgrader(jsonValue.getString("name"),
                        jsonValue.getString("description"),
                        parseBlockLayout(jsonValue.get("blockLayout")),
                        Item.Tier.valueOf(jsonValue.getString("tier")),
                        jsonValue.getDouble("itemValue"),
                        jsonValue.getFloat("conveyorSpeed"),
                        createUpgradeStrategy(jsonValue.get("upgrade")),
                        createUpgradeTag(jsonValue.get("upgradeTag")));
                allItems.put(upgrader.getName(), upgrader);
                break;
            case Constants.CONVEYORS_FP:
                Conveyor conveyor = new Conveyor(jsonValue.getString("name"),
                        jsonValue.getString("description"),
                        parseBlockLayout(jsonValue.get("blockLayout")),
                        Item.Tier.valueOf(jsonValue.getString("tier")),
                        jsonValue.getDouble("itemValue"),
                        jsonValue.getFloat("conveyorSpeed")
                        );
                allItems.put(conveyor.getName(), conveyor);
                break;
        }
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

    private UpgradeStrategy createUpgradeStrategy(JsonValue upgradeStrategyJson) {//As of 2/6/2024 this has not been tested....
        if (upgradeStrategyJson.getString("type") == null) {
            return null;
        }
        String strategyType = upgradeStrategyJson.getString("type");

        switch (strategyType) {
            case "ConditionalUPG" :
                return createConditionalUPG(upgradeStrategyJson);
            case "BundledUPG" :
                return createBundledUPG(upgradeStrategyJson);
            case "ResetterUPG" :
                return new ResetterUPG();
            case "AddUPG" :
                return new AddUPG(upgradeStrategyJson.getDouble("modifier"), AbstractUpgrade.ValueToModify.valueOf(upgradeStrategyJson.getString("ValueToModify")));
            case "MultiplyUPG" :
                return new MultiplyUPG(upgradeStrategyJson.getDouble("modifier"), AbstractUpgrade.ValueToModify.valueOf(upgradeStrategyJson.getString("ValueToModify")));
            case "SubtractUPG" :
                return new SubtractUPG(upgradeStrategyJson.getDouble("modifier"),  AbstractUpgrade.ValueToModify.valueOf(upgradeStrategyJson.getString("ValueToModify")));
            case "InfluencedUPG" :
                return new InfluencedUPG(upgradeStrategyJson.getDouble("modifier"), AbstractUpgrade.ValueToModify.valueOf(upgradeStrategyJson.getString("ValueToModify")), InfluencedUPG.ValuesOfInfluence.valueOf(upgradeStrategyJson.getString("ValueOfInfluence")));
            case "null" :
                return null;
            default:
                throw new IllegalArgumentException("Unknown/Invalid Strategy: " + strategyType);
        }
    }

    private UpgradeStrategy createConditionalUPG(JsonValue param) {
        UpgradeStrategy ifModifier = createUpgradeStrategy(param.get("ifModifier"));
        UpgradeStrategy elseModifier = createUpgradeStrategy(param.get("elseModifier"));
        ConditionalUPG.Condition condition = ConditionalUPG.Condition.valueOf(param.getString("condition")); //Values must be the EXACT same as ENUM EX: GREATER_THAN = "GREATER_THAN"
        ConditionalUPG.Comparison comparison = ConditionalUPG.Comparison.valueOf(param.getString("comparison"));
        double threshold = param.getDouble("threshold");
        return new ConditionalUPG(ifModifier, elseModifier, condition, threshold, comparison);
    }

    private UpgradeStrategy createBundledUPG(JsonValue param) {
        UpgradeStrategy upgradeStrategy1 = createUpgradeStrategy(param.get("upgStrat1"));
        UpgradeStrategy upgradeStrategy2 = createUpgradeStrategy(param.get("upgStrat2"));
        UpgradeStrategy upgradeStrategy3 = createUpgradeStrategy(param.get("upgStrat3"));
        UpgradeStrategy upgradeStrategy4 = createUpgradeStrategy(param.get("upgStrat4"));

        return new BundledUPG(upgradeStrategy1, upgradeStrategy2, upgradeStrategy3, upgradeStrategy4);
    }

    private UpgradeTag createUpgradeTag(JsonValue jsonValue) {
        return new UpgradeTag(jsonValue.getString("name"), jsonValue.getInt("maxUpgrades"), jsonValue.getBoolean("isResetter"));
    }

    public Inventory createInventory() {
        JsonValue fileContents= jsonReader.parse(Gdx.files.local(Constants.INVENTORY_FP));
        ArrayList<InventoryNode> inventoryNodes = new ArrayList<>();
        if (fileContents != null) {
            for (JsonValue jsonValue: fileContents) {
                if (allItems.containsKey(jsonValue.getString("itemName"))) {
                    inventoryNodes.add(createNode(jsonValue));
                } else {
                    System.out.println(jsonValue.getString("itemName") + "is not a valid Item");
                }
            }
        }

        for (Item item : allItems.values()) {
            boolean found= false;
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
        allItems.get(jsonValue.getString("itemName"));
        return new InventoryNode(
                allItems.get(jsonValue.getString("itemName")),
                jsonValue.getInt("totalOwned"));
    }

    public void loadPlayerStats(Player player) {
        JsonValue fileContents = jsonReader.parse(Gdx.files.local(Constants.PLAYER_STATS_FP));
        if (fileContents != null) {
            player.setPrestigeLevel(fileContents.getInt("prestigeLevel"));
            player.setWallet(fileContents.getDouble("wallet"));
            player.setSpecialPoints(fileContents.getLong("specialPoints"));
            player.setPrestigeCurrency(fileContents.getInt("prestigeCurrency"));
            player.setMostMoneyObtained(fileContents.getDouble("mostMoneyObtained"));
        }

    }

}
