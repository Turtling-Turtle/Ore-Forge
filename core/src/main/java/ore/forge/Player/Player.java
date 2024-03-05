package ore.forge.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import ore.forge.Constants;

//@author Nathan Ulmen
public class Player {
    private int prestigeLevel, prestigeCurrency;
    private long specialPoints;
    private double wallet;
    private double mostMoneyObtained;
    private static Player playerInstance;
    public Inventory inventory;
    private long numberOfTicks = 0;

    public Player(double wallet, int prestigeLevel, int prestigeCurrency) {
        this.wallet = wallet;
        this.prestigeLevel = prestigeLevel;
        this.prestigeCurrency = prestigeCurrency;
    }

    public Player() {

    }

    public Player(Inventory inventory) {
        this.inventory = inventory;
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

        PlayerData playerData = new PlayerData();
        playerData.setMostMoneyObtained(this.mostMoneyObtained);
        playerData.setWallet(this.wallet);
        playerData.setSpecialPoints(this.specialPoints);
        playerData.setPrestigeCurrency(this.prestigeCurrency);
        playerData.setPrestigeLevel(this.prestigeLevel);

        String jsonOutput = json.prettyPrint(playerData);
        FileHandle fileHandle = Gdx.files.local(Constants.PLAYER_STATS_FP);

        fileHandle.writeString(jsonOutput, false);
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
        String playerInfo = "Prestige Level: " + prestigeLevel + "\tWallet: " + wallet + "\tPlayer Prestige Currency: " + prestigeCurrency + "\tSpecial Points: " + specialPoints;
        return playerInfo;
    }

    private class PlayerData {
        private int prestigeLevel;
        private double wallet;
        private int prestigeCurrency;
        private long specialPoints;


        public void setPrestigeLevel(int prestigeLevel) {
            this.prestigeLevel = prestigeLevel;
        }

        public void setPrestigeCurrency(int prestigeCurrency) {
            this.prestigeCurrency = prestigeCurrency;
        }

        public void setSpecialPoints(long specialPoints) {
            this.specialPoints = specialPoints;
        }

        public void setWallet(double wallet) {
            this.wallet = wallet;
        }

        public void setMostMoneyObtained(double mostMoneyObtained) {
            this.mostMoneyObtained = mostMoneyObtained;
        }

        public double mostMoneyObtained;

    }



}
