package ore.forge;

import ore.forge.Expressions.Function;
import ore.forge.Expressions.Operators.NumericOperator;
import ore.forge.Items.*;
import ore.forge.Player.Player;
import ore.forge.Strategies.OreEffects.BundledOreEffect;
import ore.forge.Strategies.OreEffects.Invulnerability;
import ore.forge.Strategies.OreEffects.OreEffect;
import ore.forge.Strategies.OreEffects.UpgradeOreEffect;
import ore.forge.Strategies.UpgradeStrategies.BasicUpgrade;
import ore.forge.Strategies.UpgradeStrategies.InfluencedUpgrade;
import ore.forge.Strategies.UpgradeStrategies.UpgradeStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static ore.forge.Expressions.Operands.NumericOreProperties.ORE_VALUE;


public class LootTable {
    private final ArrayList<Item> lockedPrestigeItems;
    private final HashMap<Float, ArrayList<Item>> buckets;
    private final Random random;

    public LootTable(ItemManager manager) {
        lockedPrestigeItems = new ArrayList<>();
        for (Item item : manager.getAllItems().values()) {
            if (item.getTier() == Item.Tier.PRESTIGE) {
                lockedPrestigeItems.add(item);
            }
        }
        random = new Random(123);
        this.buckets = new HashMap<>();
        updateItems();
    }

    public Item getRandomItem() {
        var keys = new ArrayList<>(buckets.keySet());
        Collections.sort(keys);
        float roll = generateRoll();
        if (roll >= keys.getLast()) {
            return getItemFromBucket(buckets.get(keys.getLast()));
        } else {
            for (Float bucketKey : keys) {
                if (roll <= (bucketKey)) {
                    return getItemFromBucket(buckets.get(bucketKey));
                }
            }
        }
        throw new IllegalStateException("Roll did not match either bucket.");
    }

    private float generateRoll() {
        return BigDecimal.valueOf(random.nextFloat() * 100).setScale(1, RoundingMode.HALF_UP).floatValue();
    }

    private Item getItemFromBucket(ArrayList<Item> bucket) {
        return bucket.get(random.nextInt(bucket.size()));
    }

    private void addItem(Item item) {
        assert item.getTier() == Item.Tier.PRESTIGE;
        if (!buckets.containsKey(item.getRarity())) {
            var newBucket = new ArrayList<Item>();
            newBucket.add(item);
            buckets.put(item.getRarity(), newBucket);
        } else {
            var bucket = buckets.get(item.getRarity());
            if (!bucket.contains(item)) {
                bucket.add(item);
            }
        }
    }

    private void removeItem(Item item) {
        buckets.get(item.getRarity()).remove(item);
    }

    public String toString() {
        return String.valueOf(buckets.size());
    }

    public void updateItems() {
        Iterator<Item> iterator = lockedPrestigeItems.iterator();
        while (iterator.hasNext()) {
            var item = iterator.next();
            if (item.getTier() == Item.Tier.PRESTIGE && item.getUnlockMethod() == Item.UnlockMethod.PRESTIGE_LEVEL) {
                if (item.getUnlockRequirements() <= Player.getSingleton().getPrestigeLevel()) {
                    addItem(item);
                    iterator.remove();
                }
            }
        }
    }

    public static void main(String[] args) {
        int[][] upgraderConfig = {//Test values
//            { 0, 1, 1, 0},
//            { 0, 2, 2, 0},
//            { 0, 1, 1, 0},
            {2, 2},
            {1, 1},
        };
        int[][] conveyorConfig = {
            {1, 1},
            {1, 1},
        };
        int[][] furnaceConfig = {
            {4, 4},
            {4, 4},
        };
        int[][] dropperConfig = {
            {0, 3, 0},
            {0, 0, 0},
            {0, 0, 0},
        };
        int[][] buildingConfig = {
            {0, 1, 0},
            {0, 1, 0},
            {0, 1, 1}
        };
        UpgradeStrategy testUpgrade = new BasicUpgrade(3.0, NumericOperator.MULTIPLY, ORE_VALUE);


        UpgradeTag upgradeTag = new UpgradeTag("Basic Upgrade Tag", "tst", 4, false);

        OreEffect invincibility = new Invulnerability(12, 10f);

        UpgradeStrategy simpleMultiply = new BasicUpgrade(1.02, NumericOperator.MULTIPLY, ORE_VALUE);
        OreEffect upgradeOverTime = new UpgradeOreEffect(1, 10E10f, simpleMultiply);

        UpgradeStrategy basicUpgrade = new BasicUpgrade(.1, NumericOperator.MULTIPLY, ORE_VALUE);
        Function influenceFunction = Function.parseFunction("(ORE_VALUE * .01) * 2");
        UpgradeStrategy influencedUpgrade = new InfluencedUpgrade(influenceFunction, (BasicUpgrade) basicUpgrade, 1.5, 20.0);
        OreEffect influencedUpgradeOverTime = new UpgradeOreEffect(1, 2E10f, influencedUpgrade);

        OreEffect dropperStrat = new BundledOreEffect(invincibility, upgradeOverTime, influencedUpgradeOverTime);


        Furnace furnace = new Furnace("Test Furnace", "test", furnaceConfig, Item.Tier.COMMON, 0.0, 2.2f, 32, 5, influencedUpgrade);
        furnace.setID("Test Furnace");
        Conveyor conveyor = new Conveyor("Test Conveyor", "test", conveyorConfig, Item.Tier.COMMON, 0.0, 32.58f, 8);
        conveyor.setID("Test Conveyor");
        Dropper dropper = new Dropper("Test Dropper", "test", dropperConfig, Item.Tier.COMMON, 0.0, 1.12312f, "Test Ore", 20, 1, 1, dropperStrat);
        dropper.setID("Test Dropper");
        Upgrader upgrader = new Upgrader("Test Upgrader", "test", upgraderConfig, Item.Tier.COMMON, 0.0, 59, 5, influencedUpgrade, upgradeTag);
        upgrader.setID("Test Upgrader");

        LootTable table = new LootTable(null);

        var rand = new Random(123);
        for (int i = 0; i < 900; i++) {
            var item = new Conveyor("Test Conveyor", "test", conveyorConfig, Item.Tier.COMMON, 0, rand.nextFloat() * 100, 9);
            table.addItem(item);
            item.setID(String.valueOf(rand.nextInt(9_999_999)));

        }
        table.addItem(furnace);
        table.addItem(conveyor);
        table.addItem(dropper);
        table.addItem(upgrader);

        var obtainedItems = new HashMap<String, Item>();
        int count = 0;
        Stopwatch stopwatch = new Stopwatch(TimeUnit.MICROSECONDS);

//        stopwatch.start();


        Stopwatch totalTime = new Stopwatch(TimeUnit.MILLISECONDS);
        totalTime.start();
        while (!obtainedItems.containsKey(furnace.getID()) || !obtainedItems.containsKey(conveyor.getID()) || !obtainedItems.containsKey(dropper.getID()) || !obtainedItems.containsKey(upgrader.getID())) {
            count++;
//            System.out.println(count++);
            stopwatch.restart();
            var item = table.getRandomItem();
            System.out.println(stopwatch);
            if (!obtainedItems.containsKey(item.getID())) {
//                System.out.println(item + "\tRarity:" + item.getRarity());
                obtainedItems.put(item.getID(), item);
            }
        }
//        System.out.println(table.getRandomItem());
        totalTime.stop();
        System.out.println(totalTime);
        System.out.println(count);
    }

}
