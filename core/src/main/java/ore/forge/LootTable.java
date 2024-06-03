package ore.forge;

import ore.forge.Expressions.Function;
import ore.forge.Expressions.NumericOperator;
import ore.forge.Items.*;
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

import static ore.forge.Expressions.NumericOreProperties.ORE_VALUE;

public class LootTable {
    private ArrayList<Item> items;
    private HashMap<Float, ArrayList<Item>> buckets;
    Random rand = new Random();

    public LootTable() {
        this.buckets = new HashMap<>();
        this.items = new ArrayList<>();
    }

    public void rewardItem() {
        Random rand = new Random();
/*
        generate a number between 0-100
        have a hash map that uses item rarity as a key.
        go through list from most rare to least rare and check to see if generated number is less than the rarity.
        if rolled number is <= rarity get all the values in that bucket.
        generate another random number within the range/number of items in the bucket, then reward the item whose index
        aligns with that number.
*/
        var tempRoll = BigDecimal.valueOf(rand.nextFloat() * 100);
        float roll = tempRoll.setScale(1, RoundingMode.HALF_UP).floatValue();
        float aggregate = 0;
        var sortedKeys = new ArrayList<>(buckets.keySet());
        sortedKeys.sort(Collections.reverseOrder());

        for (Float bucketKey : sortedKeys) {
            if (roll < bucketKey + aggregate) {
                var reward = getItemFromBucket(buckets.get(bucketKey));
                System.out.println(reward);
            } else {
                aggregate += bucketKey;
            }
        }

    }

    public Item generateItem() {
        Random rand = new Random();
/*
        generate a number between 0-100
        have a hash map that uses item rarity as a key.
        go through list from most rare to least rare and check to see if generated number is less than the rarity.
        if rolled number is < rarity get all the values in that bucket.
        generate another random number within the range/number of items in the bucket, then reward the item whose index
        aligns with that number.
*/
        var sortedKeys = new ArrayList<>(buckets.keySet());
        Collections.sort(sortedKeys);

        while (true) {
            var tempRoll = BigDecimal.valueOf(rand.nextFloat() * 100);
            float roll = tempRoll.setScale(1, RoundingMode.HALF_UP).floatValue();
//            System.out.println(roll);
            float aggregate = 0;


            for (Float bucketKey : sortedKeys) {
                if (roll < (bucketKey + aggregate)) {
                    return getItemFromBucket(buckets.get(bucketKey));
                } else {
                    aggregate += bucketKey;
                }
            }
        }
    }

    private Item getItemFromBucket(ArrayList<Item> bucket) {
        Random rand = new Random();
        return bucket.get(rand.nextInt(bucket.size()));
    }


    public void addItem(Item item) {
        if (!buckets.containsKey(item.getRarity())) {
            var newBucket = new ArrayList<Item>();
            newBucket.add(item);
            buckets.put(item.getRarity(), newBucket);
        } else {
            var bucket = buckets.get(item.getRarity());
            assert !bucket.contains(item);
            bucket.add(item);
        }
    }

    public void removeItem(Item item) {

    }

    public String toString() {
        return null;
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
        Dropper dropper = new Dropper("Test Dropper", "test", dropperConfig, Item.Tier.COMMON, 0.0, 1.12312f, "Test Ore", 20, 1, 1, .001f, dropperStrat);
        dropper.setID("Test Dropper");
        Upgrader upgrader = new Upgrader("Test Upgrader", "test", upgraderConfig, Item.Tier.COMMON, 0.0, 99, 5, influencedUpgrade, upgradeTag);
        upgrader.setID("Test Upgrader");

        LootTable table = new LootTable();

        table.addItem(furnace);
        table.addItem(conveyor);
        table.addItem(dropper);
        table.addItem(upgrader);

        var obtainedItems = new HashMap<String, Item>();
        int count = 0;
        Stopwatch stopwatch = new Stopwatch(TimeUnit.MICROSECONDS);
//        stopwatch.start();
        while (!obtainedItems.containsKey(furnace.getID()) || !obtainedItems.containsKey(conveyor.getID()) || !obtainedItems.containsKey(dropper.getID()) || !obtainedItems.containsKey(upgrader.getID())) {
//            System.out.println(count++);
            stopwatch.restart();
            var item = table.generateItem();
            System.out.println(stopwatch);
            if (!obtainedItems.containsKey(item.getID())) {
//                System.out.println(item + "\tRarity:" + item.getRarity());
                obtainedItems.put(item.getID(), item);
            }
        }
//        stopwatch.stop();
//        System.out.println(stopwatch);


    }


}
