package ore.forge.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import ore.forge.*;
import ore.forge.EventSystem.EventManager;
import ore.forge.EventSystem.Events.FailedPurchaseEvent;
import ore.forge.EventSystem.Events.PurchaseEvent;
import ore.forge.Expressions.Function;
import ore.forge.Items.Item;

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

    public void addPrestigeCurrency(int currencyToAdd) {
        this.prestigeCurrency += currencyToAdd;
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

    public void prestige() {
        Function prestigeFunction = Function.parseFunction("(2.5 * 10^19) * (PRESTIGE_LEVEL + 1)");
        if (prestigeFunction.calculate(null) <= wallet) {
            //Clear Map of all items.
            //Remove all non-prestige proof items from inventory
            //Reset wallet
            //Reward player with a prestige item.
            //Notify Listeners of type PrestigeEvent.
            //Reward prestige Currency?
        }
    }

    public void initInventory(ItemManager itemManager) {
        inventory = new Inventory(itemManager);
    }

    public void purchaseItem(Item itemToPurchase) {
        double playerCurrency = switch (itemToPurchase.getCurrencyBoughtWith()) {
            case CASH -> getWallet();
            case SPECIAL_POINTS -> getSpecialPoints();
            case PRESTIGE_POINTS -> getPrestigeCurrency();
            case NONE ->
                throw new IllegalStateException("Unexpected Currency: " + itemToPurchase.getCurrencyBoughtWith());
        };

        if (playerCurrency >= itemToPurchase.getItemValue()) {
            inventory.getNode(itemToPurchase.getID()).addNew();
            switch (itemToPurchase.getCurrencyBoughtWith()) {
                case CASH -> setWallet(playerCurrency - itemToPurchase.getItemValue());
                case SPECIAL_POINTS -> setSpecialPoints((long) (playerCurrency - itemToPurchase.getItemValue()));
                case PRESTIGE_POINTS -> setPrestigeCurrency((int) (playerCurrency - itemToPurchase.getItemValue()));
            }
            EventManager.getSingleton().notifyListeners(new PurchaseEvent(itemToPurchase, itemToPurchase.getCurrencyBoughtWith(), 1));
        } else {
            EventManager.getSingleton().notifyListeners(new FailedPurchaseEvent(itemToPurchase, itemToPurchase.getCurrencyBoughtWith(), 1));
        }
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
