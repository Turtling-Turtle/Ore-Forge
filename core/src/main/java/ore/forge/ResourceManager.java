package ore.forge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import com.mongodb.client.*;
import ore.forge.Items.*;
import ore.forge.Player.Inventory;
import ore.forge.Player.InventoryNode;
import ore.forge.Player.Player;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

//@author Nathan Ulmen

//Resource Manager is responsible for loading assets.
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
        loadItems(Constants.CONVEYORS_FP);
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
        JsonReader jsonReader = new JsonReader();
        JsonValue fileContents = jsonReader.parse(Gdx.files.local(fileToParse));
        fileContents.child().remove();//Remove Version field from file.
        switch (fileToParse) {
            case Constants.DROPPERS_FP:
                for (JsonValue jsonValue : fileContents) {
                    addToAllItems(new Dropper(jsonValue));
                }
                break;
            case Constants.FURNACE_FP:
                for (JsonValue jsonValue : fileContents) {
                    addToAllItems(new Furnace(jsonValue));
                }
                break;
            case Constants.UPGRADER_FP:
                for (JsonValue jsonValue : fileContents) {
                    addToAllItems(new Upgrader(jsonValue));
                }
                break;
            case Constants.CONVEYORS_FP:
                for (JsonValue jsonValue : fileContents) {
                    addToAllItems(new Conveyor(jsonValue));
                }
                break;
        }
    }

    private void addToAllItems(Item item) {
        allItems.put(item.getName(), item);
        loadCount++;
        System.out.println(Constants.GREEN + "Successfully Loaded: " + item.getName() + Constants.DEFAULT);
    }

    public void mongoConnect() {
        //TODO: Add exception handling so that if you dont have internet it still works.
        MongoClient mongoClient = MongoClients.create("mongodb+srv://client:JAaTk8dtGkpSe42u@primarycluster.bonuplz.mongodb.net/");
        MongoDatabase database = mongoClient.getDatabase("OreForge");

        updateLocalFile("Conveyors", Constants.CONVEYORS_FP, database);
        updateLocalFile("Upgraders", Constants.UPGRADER_FP, database);

//        updateFile("Droppers", Constants.DROPPERS_FP, database);
//        updateFile("Furnaces", Constants.FURNACE_FP, database);
        mongoClient.close();

    }

    public HashMap<String, Item> getAllItems() {
        return allItems;
    }

    private void updateLocalFile(String mongoCollection, String localFile, MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection(mongoCollection);
        Document version = collection.find().first();
        JsonReader jsonReader = new JsonReader();
        JsonValue localFileContents;
        double localVersion = -1;
        try {
            localFileContents = jsonReader.parse(Gdx.files.local(localFile));
            localVersion = localFileContents.child().getDouble("version");
        } catch (SerializationException e ) {

        }

        double dbVersion = -2;
        try {
            dbVersion = version.getDouble("version");
        } catch (NullPointerException e) {
            throw new RuntimeException("Could not retrieve version from MongoDB collection: " + mongoCollection);
        }


        if (dbVersion != localVersion) {
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

    }


}
