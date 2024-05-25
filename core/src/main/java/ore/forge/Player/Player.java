package ore.forge.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import ore.forge.Color;
import ore.forge.Constants;
import ore.forge.ResourceManager;

//@author Nathan Ulmen
public class Player {
    private int prestigeLevel, prestigeCurrency;
    private long specialPoints;
    private double wallet;
    private double mostMoneyObtained;
    private static Player playerInstance = new Player();
    private Inventory inventory;
    private long numberOfTicks = 0;

    private Player() {

    }

    public void addToWallet(double value) {
        wallet += value;
        mostMoneyObtainedCheck();
    }

    public void subtractFromWallet(double value) {
        wallet -= value;
    }

    public void addSpecialPoints(int SP) {
        specialPoints += SP;
    }


    public void setWallet(double valueToSetTo) {
        wallet = valueToSetTo;
        mostMoneyObtainedCheck();
    }

    private void mostMoneyObtainedCheck() {
        if (mostMoneyObtained < wallet) {
            mostMoneyObtained = wallet;
        }
    }

    public void saveData() {
        inventory.saveInventory();
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        PlayerData data = new PlayerData(this.prestigeLevel, wallet, prestigeCurrency, specialPoints, mostMoneyObtained);
        String jsonOutput = json.prettyPrint(data);
        FileHandle fileHandle = Gdx.files.local(Constants.PLAYER_STATS_FP);
        fileHandle.writeString(jsonOutput, false);
    }

    public void loadSaveData() {
        JsonReader jsonReader = new JsonReader();
        JsonValue fileContents;
        try {
            fileContents = jsonReader.parse(Gdx.files.local(Constants.PLAYER_STATS_FP));
        } catch (SerializationException e) {
            Gdx.app.log("PLAYER", Color.highlightString(Constants.PLAYER_STATS_FP + " was not present", Color.YELLOW));
            fileContents = null;
        }
        if (fileContents != null) {
            prestigeLevel = fileContents.getInt("prestigeLevel");
            wallet = fileContents.getDouble("wallet");
            specialPoints = fileContents.getLong("specialPoints");
            prestigeCurrency = fileContents.getInt("prestigeCurrency");
            mostMoneyObtained = fileContents.getDouble("mostMoneyObtained");
        } else {
            //Set Default Stats
            prestigeLevel = 0;
            wallet = 50;
            specialPoints = 0;
            prestigeCurrency = 0;
            mostMoneyObtained = 50;
        }

    }

    public void initInventory(ResourceManager resourceManager) {
        inventory = new Inventory(resourceManager);
    }

    public double getWallet() {
        return wallet;
    }

    public double getMostMoneyObtained() {
        return mostMoneyObtained;
    }

    public void setMostMoneyObtained(double newVal) {
        mostMoneyObtained = newVal;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void incrementPrestigeLevel() {
        prestigeLevel++;
    }

    public int getPrestigeLevel() {
        return prestigeLevel;
    }

    public void setPrestigeLevel(int newPrestigeLevel) {
        this.prestigeLevel = newPrestigeLevel;
    }

    public void setPrestigeCurrency(int newVal) {
        prestigeCurrency = newVal;
    }

    public int getPrestigeCurrency() {
        return prestigeCurrency;
    }

    public void setSpecialPoints(long newSpecialPointsValue) {
        this.specialPoints = newSpecialPointsValue;
    }

    public long getSpecialPoints() {
        return specialPoints;
    }

    public static Player getSingleton() {
        if (playerInstance == null) {
            playerInstance = new Player();
        }
        return playerInstance;
    }

    public void incrementTicks() {
        numberOfTicks++;
    }

    public long getNumberOfTicks() {
        return numberOfTicks;
    }

    public String toString() {
        return "Prestige Level: " + prestigeLevel + "\tWallet: " + wallet + "\tPlayer Prestige Currency: " + prestigeCurrency + "\tSpecial Points: " + specialPoints;
    }

    private record PlayerData(int prestigeLevel, double wallet, int prestigeCurrency, long specialPoints,
                              double mostMoneyObtained) {

    }


}
