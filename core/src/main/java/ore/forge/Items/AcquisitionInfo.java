package ore.forge.Items;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Currency;

public class AcquisitionInfo {
    public enum UnlockMethod {SPECIAL_POINTS, PRESTIGE_LEVEL, QUEST}

    protected final float rarity; //Rarity of item. Only matters if item is prestige item.
    private final boolean isShopItem; //Denotes if the item can be purchased from the shop.
    private final Currency currencyBoughtWith; // The Currency the item is bought from the shop with.
    private final UnlockMethod unlockMethod; //Denotes the unlock method.
    private final double unlockRequirement; // The prestige level or special point currency required to unlock item from shop.
    private boolean isUnlocked; //Denotes if the item has been unlocked for purchase in the shop.
    private final boolean canBeSold;

    public AcquisitionInfo(float rarity, boolean isShopItem, Currency currency, UnlockMethod unlockMethod, double unlockRequirements, boolean canBeSold, boolean isUnlocked) {
        this.rarity = rarity;
        this.isShopItem = isShopItem;
        this.currencyBoughtWith = currency;
        this.unlockMethod = unlockMethod;
        this.unlockRequirement = unlockRequirements;
        this.canBeSold = canBeSold;
        this.isUnlocked = isUnlocked;

    }

    public AcquisitionInfo(JsonValue jsonValue) {
        this.rarity = jsonValue.getFloat("rarity");

        this.isShopItem = jsonValue.getBoolean("isShopItem");
        this.currencyBoughtWith = Currency.valueOf(jsonValue.getString("currencyBoughtWith"));

        this.unlockMethod = UnlockMethod.valueOf(jsonValue.getString("unlockMethod"));
        this.unlockRequirement = jsonValue.getDouble("unlockRequirement");

        this.canBeSold = jsonValue.getBoolean("canBeSold");
    }

    public void unlock() {
        this.isUnlocked = true;
    }

    public String toString() {
        return "Rarity: " + rarity + "\t";
    }


}
