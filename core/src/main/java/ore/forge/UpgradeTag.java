package ore.forge;

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

    public Boolean isResseter() {
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
