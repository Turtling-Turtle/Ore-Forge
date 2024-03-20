package ore.forge;

import com.badlogic.gdx.utils.JsonValue;

//@author Nathan Ulmen
public class UpgradeTag {
    private final String name;
    private final int maxUpgrades;
    private int currentUpgrades;
    private final boolean isResetter;

    public UpgradeTag(String name, int maxUpgrades, boolean isResetter) {
        this.name = name;
        this.maxUpgrades = maxUpgrades;
        currentUpgrades = 0;
        this.isResetter = isResetter;
    }

    public UpgradeTag(UpgradeTag tagToClone) {
        this.name = tagToClone.getName();
        this.maxUpgrades = tagToClone.getMaxUpgrades();
        currentUpgrades = 0;
        this.isResetter = tagToClone.isResetter;
    }

    public UpgradeTag(JsonValue jsonValue) {
        this.name = jsonValue.getString("name");
        this.maxUpgrades = jsonValue.getInt("maxUpgrades");
        this.isResetter = jsonValue.getBoolean("isResetter");
        currentUpgrades = 0;
    }

    public Boolean isResetter() {
        return isResetter;
    }

    public String getName() {
        return name;
    }

    public void reset() {
        this.currentUpgrades = 0;
    }

    public int getMaxUpgrades() {
        return maxUpgrades;
    }

    public int getCurrentUpgrades() {
        return currentUpgrades;
    }

    public void incrementCurrentUpgrades() {
        currentUpgrades++;
    }

    public String toString() {
        return "Name: " + name + "  Max Upgrades: " + maxUpgrades +
                "  CurrentUpgrades: " + currentUpgrades;
    }

}
