package ore.forge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import com.mongodb.client.*;
import ore.forge.Enums.Color;
import ore.forge.Items.*;
import org.bson.Document;

import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

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


        mongoConnect();
//        CompletableFuture<Void> conveyorFuture = CompletableFuture.runAsync(() -> loadItems(Constants.CONVEYORS_FP));
//        CompletableFuture<Void> dropperFuture = CompletableFuture.runAsync(() -> loadItems(Constants.DROPPERS_FP));
//        CompletableFuture<Void> upgraderFuture = CompletableFuture.runAsync(() -> loadItems(Constants.UPGRADER_FP));
//        CompletableFuture<Void> furnaceFuture = CompletableFuture.runAsync(() -> loadItems(Constants.FURNACE_FP));

//        CompletableFuture.allOf(conveyorFuture, dropperFuture, upgraderFuture, furnaceFuture).join();
        long t1 = System.currentTimeMillis();
        loadItems(Constants.CONVEYORS_FP);
        loadItems(Constants.DROPPERS_FP);
        loadItems(Constants.UPGRADER_FP);
        loadItems(Constants.FURNACE_FP);
        Gdx.app.log("Resource Manager", Color.GREEN.colorId + "Loaded " + loadCount + " items in " + (System.currentTimeMillis()-t1) + " ms" + Color.NONE.colorId);
//        for (Item item: allItems.values()) {
//            Gdx.app.log(item.getClass().getSimpleName(), item.toString());
//            System.out.println();
//        }


    }

    public void loadItems(String fileToParse) {
        JsonReader jsonReader = new JsonReader();
        JsonValue fileContents = jsonReader.parse(Gdx.files.local(fileToParse));
        fileContents.child().remove();//Remove version field from file.
        switch (fileToParse) {
            case Constants.DROPPERS_FP:
                for (JsonValue jsonValue : fileContents) {
                    addToAllItems(new Dropper(jsonValue), Color.PINK);
                }
                break;
            case Constants.FURNACE_FP:
                for (JsonValue jsonValue : fileContents) {
                    addToAllItems(new Furnace(jsonValue), Color.CYAN);
                }
                break;
            case Constants.UPGRADER_FP:
                for (JsonValue jsonValue : fileContents) {
                    addToAllItems(new Upgrader(jsonValue), Color.BLUE);
                }
                break;
            case Constants.CONVEYORS_FP:
                for (JsonValue jsonValue : fileContents) {
                    addToAllItems(new Conveyor(jsonValue), Color.PURPLE);
                }
                break;
        }
    }

    private void addToAllItems(Item item, Color color) {
        allItems.put(item.getName(), item);
        loadCount++;
        Gdx.app.log(item.getClass().getSimpleName(), color.colorId + "Loaded " + item.getName() + Color.NONE.colorId);
    }

    private void mongoConnect() {
        //TODO: Add exception handling so that if you dont have internet it still works.
        long t1 = System.currentTimeMillis();
        MongoClient mongoClient = MongoClients.create("mongodb+srv://client:JAaTk8dtGkpSe42u@primarycluster.bonuplz.mongodb.net/");
        MongoDatabase database = mongoClient.getDatabase("OreForge");

        //Void specifies that this function isnt returning anything, Async loading.
        CompletableFuture<Void> conveyorFuture = CompletableFuture.runAsync(() -> checkVersion("Conveyors", Constants.CONVEYORS_FP, database));
        CompletableFuture<Void> upgraderFuture = CompletableFuture.runAsync(() -> checkVersion("Upgraders", Constants.UPGRADER_FP, database));
        CompletableFuture<Void> furnaceFuture = CompletableFuture.runAsync(() -> checkVersion("Furnaces", Constants.FURNACE_FP, database));
        CompletableFuture<Void> dropperFuture = CompletableFuture.runAsync(() -> checkVersion("Droppers", Constants.DROPPERS_FP, database));
//        updateLocalFile("Droppers", Constants.DROPPERS_FP, database);
//        updateLocalFile("Furnaces", Constants.FURNACE_FP, database);
//        updateLocalFile("Upgraders", Constants.UPGRADER_FP, database);
//        updateLocalFile("Conveyors", Constants.CONVEYORS_FP, database);
        CompletableFuture.allOf(conveyorFuture, upgraderFuture, furnaceFuture, dropperFuture).join();//This forces all ansync tasks to be completed before returning from this function.
        mongoClient.close();
        Gdx.app.log("Mongo Loader", Color.GREEN.colorId + "Verified local files in " + (System.currentTimeMillis() - t1) + "ms" + Color.NONE.colorId);
    }

    public HashMap<String, Item> getAllItems() {
        return allItems;
    }

    public HashMap<String, Item> copyAllItems() {
        return new HashMap<>(allItems);
    }

    private void checkVersion(String mongoCollection, String localFile, MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection(mongoCollection);
        // Project only the "version" field

        Document version = collection.find().first();
        JsonReader jsonReader = new JsonReader();
        JsonValue localFileContents;
        double localVersion = -1;//set to initially to ensure that it will never equal a mongoDB version by accident

        //Try to find the local document and its version. If it's not present we will automatically get
        //the most up-to-date version from MongoDB.
        try {
            localFileContents = jsonReader.parse(Gdx.files.local(localFile));
            localVersion = localFileContents.child().getDouble("version");
        } catch (Throwable ignored) {}



        double dbVersion = -2;
        Double dob = null;
        try {
            dbVersion = version.getDouble("version");
        } catch (NullPointerException e) {
            throw new RuntimeException("Could not retrieve version from MongoDB collection: " + mongoCollection + "\n" + e);
        }

        //If the versions are out of sync we Overwrite the current version on the local
        //machine with the version from MongoDB.
        if (dbVersion != localVersion) {
            Gdx.app.log("Mongo Loader", Color.YELLOW.colorId + localFile +  " version " + localVersion + " did not align with " + mongoCollection + " collection version" + dbVersion + Color.NONE.colorId);
            downloadAndUpdate(localFile, collection);
        }
    }

    private void downloadAndUpdate(String localFile, MongoCollection<Document> collection) {
        FindIterable<Document> documents = collection.find();
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        FileHandle fileHandle = Gdx.files.local(localFile);
        long total = collection.countDocuments();
        long count = 0;
        StringBuilder jsonString = new StringBuilder("[\n");
        for (Document document : documents) {
            count++;
            document.remove("_id");//remove id from mongoDB
            jsonString.append(json.prettyPrint(document.toJson()));
            if (count < total) {//This block is used to ensure that a comma isn't placed after the final item in the JSON file.
                jsonString.append(",\n");
            } else {
                jsonString.append("\n");
            }
        }
        jsonString.append("]");
        fileHandle.writeString(jsonString.toString(),false);
    }

}
