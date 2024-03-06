package ore.forge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Items.*;
import ore.forge.Strategies.OreStrategies.BundledEffect;
import ore.forge.Strategies.OreStrategies.Inflamed;
import ore.forge.Strategies.OreStrategies.FrostBite;
import ore.forge.Strategies.OreStrategies.OreStrategy;
import ore.forge.Strategies.UpgradeStrategies.*;
import ore.forge.Strategies.UpgradeStrategies.PrimaryUPGS.AddUPG;
import ore.forge.Strategies.UpgradeStrategies.PrimaryUPGS.MultiplyUPG;
import ore.forge.Strategies.UpgradeStrategies.PrimaryUPGS.SubtractUPG;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;
import ore.forge.Player.Player;

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
            Gdx.app.log("Item", item.toString());
            System.out.println();
        }
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
            createOreStrategy(jsonValue.get("oreStrategy"))
        );
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
            createUpgradeStrategy(jsonValue.get("upgrade")),
            jsonValue.getInt("specialPointReward"),
            jsonValue.getInt("rewardThreshold")
        );
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
            createUpgradeStrategy(jsonValue.get("upgrade")),
            createUpgradeTag(jsonValue.get("upgradeTag"))
        );
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

    private UpgradeStrategy createUpgradeStrategy(JsonValue upgradeStrategyJson) {//This should be improved to be more flexible but don't know how.
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
            case "ApplyEffect":
                return new ApplyEffect(createOreStrategy(upgradeStrategyJson));
            case "EffectPurger":
                return new EffectPurger();//Not Implemented
            case "TargetedCleanser":
                return new TargetedCleanser();//Not Implemented
            case "null" :
                return null;
            default:
                throw new IllegalArgumentException("Unknown/Invalid Strategy: " + strategyType);
        }
    }

    private OreStrategy createOreStrategy(JsonValue oreStrategyJson) {
        if (oreStrategyJson.getString("strategyType") == null) {return null;}
        String type = oreStrategyJson.getString("strategyType");
        switch (type) {
            case "BundledEffect":
                return createBundledEffect(oreStrategyJson);
            case "Inflamed":
                return new Inflamed(oreStrategyJson.getFloat("duration"), oreStrategyJson.getFloat("tempIncrease"));
            case "FrostBite":
                return new FrostBite(oreStrategyJson.getFloat("duration"), oreStrategyJson.getFloat("tempDecrease"));
            default:
                throw new IllegalArgumentException("Unknown/Invalid Strategy: " + type);
        }
    }

    private OreStrategy createBundledEffect(JsonValue param) {
        OreStrategy oreStrategy1 = createOreStrategyOrNull(param, "oreStrat1");
        OreStrategy oreStrategy2 = createOreStrategyOrNull(param, "oreStrat2");
        OreStrategy oreStrategy3 = createOreStrategyOrNull(param, "oreStrat3");
        OreStrategy oreStrategy4 = createOreStrategyOrNull(param, "oreStrat4");

        return new BundledEffect(oreStrategy1, oreStrategy2, oreStrategy3, oreStrategy4);
    }

    private OreStrategy createOreStrategyOrNull(JsonValue param, String valueToGet) {
        try {
            return createOreStrategy(param.get(valueToGet));
        } catch (NullPointerException e) {
            return null;
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
        UpgradeStrategy upgradeStrategy1 = createUpgradeStrategyOrNull(param,"upgStrat1");
        UpgradeStrategy upgradeStrategy2 = createUpgradeStrategyOrNull(param, "upgStrat2");
        UpgradeStrategy upgradeStrategy3 = createUpgradeStrategyOrNull(param, "upgStrat3");
        UpgradeStrategy upgradeStrategy4 = createUpgradeStrategyOrNull(param, "upgStrat4");

        return new BundledUPG(upgradeStrategy1, upgradeStrategy2, upgradeStrategy3, upgradeStrategy4);
    }

    private UpgradeStrategy createUpgradeStrategyOrNull(JsonValue param, String valueToGet) {
        try {
            return createUpgradeStrategy(param.get(valueToGet));
        } catch (NullPointerException e) {
            return null;
        }
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
