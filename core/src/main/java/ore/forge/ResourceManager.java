package ore.forge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.mongodb.client.*;
import ore.forge.Items.*;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;
import ore.forge.Player.Player;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;

//@author Nathan Ulmen
public class ResourceManager {
    private final AssetManager assetManager;
    private final HashMap<String, Sound> allSounds;
    private final HashMap<String, Item> allItems;
    private int loadCount;

    public ResourceManager() {
        loadCount = 0;
        assetManager = new AssetManager();
        allSounds = new HashMap<>();
        allItems = new HashMap<>();

        long t1 = System.currentTimeMillis();
//        mongoConnect();
//        loadItems(Constants.CONVEYORS_FP);
//        loadItems(Constants.DROPPERS_FP);
        loadItems(Constants.UPGRADER_FP);
//        loadItems(Constants.FURNACE_FP);
        for (Item item: allItems.values()) {
            Gdx.app.log(item.getClass().getSimpleName(), item.toString());
            System.out.println();
        }

        System.out.println("Loaded " + loadCount + " items in: " + (System.currentTimeMillis() -t1) + " ms");
    }

    public void loadItems(String fileToParse) {
        JsonReader jsonReader = new JsonReader();
        JsonValue fileContents = jsonReader.parse(Gdx.files.internal(fileToParse));
        fileContents.child().remove();
        System.out.println(fileContents);
        switch (fileToParse) {
            case Constants.DROPPERS_FP:
                for (JsonValue jsonValue : fileContents) {
                    addToAllItems(new Dropper(jsonValue));
                }
            case Constants.FURNACE_FP:
                for (JsonValue jsonValue : fileContents) {
                    addToAllItems(new Furnace(jsonValue));
                }
            case Constants.UPGRADER_FP:
                for (JsonValue jsonValue : fileContents) {
                    addToAllItems(new Upgrader(jsonValue));
                }
            case Constants.CONVEYORS_FP:
                for (JsonValue jsonValue : fileContents) {
                    addToAllItems(new Conveyor(jsonValue));
                }
        };
    }

    private void addToAllItems(Item item) {
        allItems.put(item.getName(), item);
        loadCount++;
        System.out.println(Constants.GREEN + "Successfully Loaded: " + item.getName() + Constants.DEFAULT);
    }

    public Inventory createInventory() {
        JsonReader jsonReader = new JsonReader();
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
        JsonReader jsonReader = new JsonReader();
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


    public void mongoConnect() {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://client:JAaTk8dtGkpSe42u@primarycluster.bonuplz.mongodb.net/");
        MongoDatabase database = mongoClient.getDatabase("OreForge");

        updateFile("Conveyors", Constants.CONVEYORS_FP, database);
        updateFile("Upgraders", Constants.UPGRADER_FP, database);
        updateFile("Droppers", Constants.DROPPERS_FP, database);

        mongoClient.close();

    }

    private void updateFile(String mongoCollection, String localFile, MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection(mongoCollection);

        Document version = collection.find().first();
        String mongoJsonContent;
        JsonReader jsonReader = new JsonReader();
        JsonValue fileContents = jsonReader.parse(Gdx.files.internal(localFile));
        if (version.getDouble("version") != fileContents.child().getDouble("version")) {
            FindIterable<Document> documents = collection.find();
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            FileHandle fileHandle = Gdx.files.local(localFile);
            long total = collection.countDocuments();
            long count = 0;
            String jsonString = "[\n";
            for (Document document : documents) {
                count++;
                document.remove("_id");
                jsonString += json.prettyPrint(document.toJson());
                if (count < total) {
                    jsonString += ",\n";
                } else {
                    jsonString += "\n";
                }
            }
            jsonString += "]";
            fileHandle.writeString(jsonString,false);
        }
//        boolean isDifferent = false;
//        loop:
//        for (Document document : documents) {
//            document.remove("_id");
//            mongoJsonContent = document.toJson();
//            for (JsonValue jsonValue : fileContents) {
//                if (!mongoJsonContent.toString().equals(jsonValue.toString())) {
//                    isDifferent = true;
//                    System.out.println(mongoJsonContent+ "\nDoes Not Equal: \n" + jsonValue);
//                    break loop;
//                }
//            }
//        }
//        if (isDifferent) {
//
//        }

    }

}
