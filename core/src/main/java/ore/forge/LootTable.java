package ore.forge;

import ore.forge.BreakInfinity.BigDouble;
import ore.forge.EventSystem.EventListener;
import ore.forge.EventSystem.Events.PrestigeEvent;
import ore.forge.Expressions.Function;
import ore.forge.Expressions.NumericOperator;
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

import static ore.forge.Expressions.NumericOreProperties.ORE_VALUE;


public class LootTable {
    private final ArrayList<Item> lockedPrestigeItems;
    private HashMap<Float, ArrayList<Item>> buckets;
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

    //TODO: This probably isn't working as intended.
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
        Dropper dropper = new Dropper("Test Dropper", "test", dropperConfig, Item.Tier.COMMON, 0.0, 1.12312f, "Test Ore", 20, 1, 1, .001f, dropperStrat);
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
//        Stopwatch stopwatch = new Stopwatch(TimeUnit.MILLISECONDS);
//
//
//        stopwatch.stop();
//        System.out.println(stopwatch);
//
//        var e = new BigDouble("2.990956342259643e+92229713907621998");
//        var u = new BigDouble("2.9956342259643e+932321713907621995");
//        u = u.multiply(e).multiply(u).multiply(u).multiply(e).add(u);
//        System.out.println(u);
//        ArrayList<BigDouble> bigNumbers = new ArrayList<>(2500);
//
//        stopwatch.restart();
//        for (int i = 0; i < 2_500; i++) {
//            bigNumbers.add(new BigDouble((i + 5) * 2e4));
//        }
//        stopwatch.stop();
//        System.out.println("Creation finished in " + stopwatch);
//
//        stopwatch.restart();
//        for (int i = 0; i < 2500; i++) {
//            for (int j = 0; j < 9999; j++) {
//                bigNumbers.set(i, bigNumbers.get(i).add(bigNumbers.get(i)));
//            }
//        }
//        stopwatch.stop();
//        System.out.println("Adding finished in" + stopwatch);
//        System.out.println("Sum: " + sum(bigNumbers).toString());
//
//
//        System.out.println();
//        stopwatch.restart();
//
//        BigDouble biggestNumber = new BigDouble(0);
////        "9e9,999,999,999,999,999"
//        var testDouble = new BigDouble("9.3525e9999999999999");
//        var index = 0;
//        for (int i = 0; i < 2500; i++) {
//            for (int j = 0; j < 99999; j++) {
//                bigNumbers.set(i, bigNumbers.get(i).multiply(testDouble));
//                if (bigNumbers.get(i).greaterThan(biggestNumber)) {
//                    biggestNumber = new BigDouble(bigNumbers.get(i));
//                    index = i;
//                }
//            }
//        }
//        stopwatch.stop();
//        System.out.println("Biggest Number:" + biggestNumber);
//        System.out.println("Multiply finished in" + stopwatch);
//        System.out.println("Sum: " + sum(bigNumbers));
//        System.out.println(sum(bigNumbers).greaterThan(biggestNumber));
//        System.out.println(bigNumbers.get(index));
//
//        System.out.println();
//        stopwatch.restart();
//        for (BigDouble bigNumber1 : bigNumbers) {
//            for (BigDouble bigNumber2 : bigNumbers) {
//                bigNumber1.divide(bigNumber2);
//            }
//        }
//        stopwatch.stop();
//        System.out.println("Division finished in" + stopwatch);
//        System.out.println("Sum: " + sum(bigNumbers));
//
//        stopwatch.restart();
//        for (BigDouble bigNumber1 : bigNumbers) {
//            for (BigDouble bigNumber2 : bigNumbers) {
//                bigNumber1.subtract(bigNumber2);
//            }
//        }
//        stopwatch.stop();
//        System.out.println("Subtraction finished in" + stopwatch);
//        System.out.println("Sum: " + sum(bigNumbers));
//
//        stopwatch.restart();
//        for (BigDouble bigNumber1 : bigNumbers) {
//            for (BigDouble bigNumber2 : bigNumbers) {
//                bigNumber1.greaterThan(bigNumber2);
//            }
//        }
//        stopwatch.stop();
//        System.out.println("Comparison finished in" + stopwatch);
//        System.out.println("Sum: " + sum(bigNumbers));
//        System.out.println(sum(bigNumbers).add("2.223372036854776e+9168681316920207656"));
//

    }

    private static BigDouble sum(ArrayList<BigDouble> bigNumbers) {
        BigDouble sum = new BigDouble(0);
        for (BigDouble bigNumber : bigNumbers) {
            sum = new BigDouble(sum.add(bigNumber));
        }
        return sum;

    }

    public static <E extends Comparable<E>> Float customBinarySearch(ArrayList<Float> data, Float target) {
        return binarySearch(data, 0, data.size() - 1, target, new HashSet<>());
    }

    public static Float binarySearch(ArrayList<Float> data, int min, int max, Float target, HashSet<Float> previous) {

        int midPoint;
        if (min > max) {
            midPoint = (max + min) / 2;
            if (previous.contains(data.get(midPoint))) {
                System.out.println("Target/Roll: " + target);
                System.out.println("Midpoint: " + data.get(midPoint));
                var middleData = data.get(midPoint);
                if (target.compareTo(middleData) <= 0) {
                    System.out.println("Target is less than or equal to. Returned: " + data.get(midPoint));
                    return data.get(midPoint);
                } else {
                    System.out.println("Target is greater than. Returned: " + data.get(midPoint + 1));
                    return data.get(midPoint + 1);
                }
            }
            throw new IllegalStateException("NUll");
//            System.out.println("nulled!!!");
//            return null;
        } // Base case of "not there"
        midPoint = (max + min) / 2;
        if (target.compareTo(data.get(midPoint)) == 0) { // Found it! Base case
            System.out.println("Target: " + target + " Midpoint: " + data.get(midPoint));
            System.out.println("Exact Return");
            return data.get(midPoint);
        } else if (data.get(midPoint).compareTo(target) > 0) { // when target should be in left partition
            previous.add(data.get(midPoint));
            return binarySearch(data, min, midPoint - 1, target, previous);
        } else { // when target should be in the right partition
            previous.add(data.get(midPoint));
            return binarySearch(data, midPoint + 1, max, target, previous);
        }

    }


}
