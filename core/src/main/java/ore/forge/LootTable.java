package ore.forge;

import ore.forge.BreakInfinity.BigDouble;
import ore.forge.Items.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class LootTable {
    private final ItemManager manager;
    private HashMap<Float, ArrayList<Item>> buckets;
    private final Random random;

    public LootTable(ItemManager manager) {
        this.manager = manager;
        random = new Random();
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
                if (roll < (bucketKey)) {
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
//            assert !bucket.contains(item);
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
        var allItems = manager.getAllItems();
        for (Item item : allItems.values()) {
            if (item.getTier() == Item.Tier.PRESTIGE) {
                addItem(item);
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
//        UpgradeStrategy testUpgrade = new BasicUpgrade(3.0, NumericOperator.MULTIPLY, ORE_VALUE);
//
//
//        UpgradeTag upgradeTag = new UpgradeTag("Basic Upgrade Tag", "tst", 4, false);
//
//        OreEffect invincibility = new Invulnerability(12, 10f);
//
//        UpgradeStrategy simpleMultiply = new BasicUpgrade(1.02, NumericOperator.MULTIPLY, ORE_VALUE);
//        OreEffect upgradeOverTime = new UpgradeOreEffect(1, 10E10f, simpleMultiply);
//
//        UpgradeStrategy basicUpgrade = new BasicUpgrade(.1, NumericOperator.MULTIPLY, ORE_VALUE);
//        Function influenceFunction = Function.parseFunction("(ORE_VALUE * .01) * 2");
//        UpgradeStrategy influencedUpgrade = new InfluencedUpgrade(influenceFunction, (BasicUpgrade) basicUpgrade, 1.5, 20.0);
//        OreEffect influencedUpgradeOverTime = new UpgradeOreEffect(1, 2E10f, influencedUpgrade);
//
//        OreEffect dropperStrat = new BundledOreEffect(invincibility, upgradeOverTime, influencedUpgradeOverTime);
//
//
//        Furnace furnace = new Furnace("Test Furnace", "test", furnaceConfig, Item.Tier.COMMON, 0.0, 2.2f, 32, 5, influencedUpgrade);
//        furnace.setID("Test Furnace");
//        Conveyor conveyor = new Conveyor("Test Conveyor", "test", conveyorConfig, Item.Tier.COMMON, 0.0, 32.58f, 8);
//        conveyor.setID("Test Conveyor");
//        Dropper dropper = new Dropper("Test Dropper", "test", dropperConfig, Item.Tier.COMMON, 0.0, 1.12312f, "Test Ore", 20, 1, 1, .001f, dropperStrat);
//        dropper.setID("Test Dropper");
//        Upgrader upgrader = new Upgrader("Test Upgrader", "test", upgraderConfig, Item.Tier.COMMON, 0.0, 59, 5, influencedUpgrade, upgradeTag);
//        upgrader.setID("Test Upgrader");
//
//        LootTable table = new LootTable();
//
//        table.addItem(furnace);
//        table.addItem(conveyor);
//        table.addItem(dropper);
//        table.addItem(upgrader);
//
//        var obtainedItems = new HashMap<String, Item>();
//        int count = 0;
//        Stopwatch stopwatch = new Stopwatch(TimeUnit.MICROSECONDS);
//
////        stopwatch.start();
//
//        while (!obtainedItems.containsKey(furnace.getID()) || !obtainedItems.containsKey(conveyor.getID()) || !obtainedItems.containsKey(dropper.getID()) || !obtainedItems.containsKey(upgrader.getID())) {
//            count++;
////            System.out.println(count++);
//            stopwatch.restart();
//            var item = table.getRandomItem();
//            System.out.println(stopwatch);
//            if (!obtainedItems.containsKey(item.getID())) {
////                System.out.println(item + "\tRarity:" + item.getRarity());
//                obtainedItems.put(item.getID(), item);
//            }
//        }
//        System.out.println(count);
        Stopwatch stopwatch = new Stopwatch(TimeUnit.MILLISECONDS);


//        stopwatch.stop();
//        System.out.println(stopwatch);

        var e = new BigDouble("2.990956342259643e+92229713907621998");
        var u = new BigDouble("2.9956342259643e+932321713907621995");
        u = u.multiply(e).multiply(u).multiply(u).multiply(e).add(u);
        System.out.println(u);
        ArrayList<BigDouble> bigNumbers = new ArrayList<>(2500);

        stopwatch.restart();
        for (int i = 0; i < 2_500; i++) {
            bigNumbers.add(new BigDouble((i + 5) * 2e4));
        }
        stopwatch.stop();
        System.out.println("Creation finished in " + stopwatch);

        stopwatch.restart();
        for (int i = 0; i < 2500; i++) {
            for (int j = 0; j < 9999; j++) {
                bigNumbers.set(i, bigNumbers.get(i).add(bigNumbers.get(i)));
            }
        }
        stopwatch.stop();
        System.out.println("Adding finished in" + stopwatch);
        System.out.println("Sum: " + sum(bigNumbers).toString());


        System.out.println();
        stopwatch.restart();

        BigDouble biggestNumber = new BigDouble(0);
//        "9e9,999,999,999,999,999"
        var testDouble = new BigDouble("9.3525e9999999999999");
        var index = 0;
        for (int i = 0; i < 2500; i++) {
            for (int j = 0; j < 99999; j++) {
                bigNumbers.set(i, bigNumbers.get(i).multiply(testDouble));
                if (bigNumbers.get(i).greaterThan(biggestNumber)) {
                    biggestNumber = new BigDouble(bigNumbers.get(i));
                    index = i;
                }
            }
        }
        stopwatch.stop();
        System.out.println("Biggest Number:" + biggestNumber);
        System.out.println("Multiply finished in" + stopwatch);
        System.out.println("Sum: " + sum(bigNumbers));
        System.out.println(sum(bigNumbers).greaterThan(biggestNumber));
        System.out.println(bigNumbers.get(index));

        System.out.println();
        stopwatch.restart();
        for (BigDouble bigNumber1 : bigNumbers) {
            for (BigDouble bigNumber2 : bigNumbers) {
                bigNumber1.divide(bigNumber2);
            }
        }
        stopwatch.stop();
        System.out.println("Division finished in" + stopwatch);
        System.out.println("Sum: " + sum(bigNumbers));

        stopwatch.restart();
        for (BigDouble bigNumber1 : bigNumbers) {
            for (BigDouble bigNumber2 : bigNumbers) {
                bigNumber1.subtract(bigNumber2);
            }
        }
        stopwatch.stop();
        System.out.println("Subtraction finished in" + stopwatch);
        System.out.println("Sum: " + sum(bigNumbers));

        stopwatch.restart();
        for (BigDouble bigNumber1 : bigNumbers) {
            for (BigDouble bigNumber2 : bigNumbers) {
                bigNumber1.greaterThan(bigNumber2);
            }
        }
        stopwatch.stop();
        System.out.println("Comparison finished in" + stopwatch);
        System.out.println("Sum: " + sum(bigNumbers));
        System.out.println(sum(bigNumbers).add("2.223372036854776e+9168681316920207656"));


    }

    private static BigDouble sum(ArrayList<BigDouble> bigNumbers) {
        BigDouble sum = new BigDouble(0);
        for (BigDouble bigNumber : bigNumbers) {
            sum = new BigDouble(sum.add(bigNumber));
        }
        return sum;

    }


}
