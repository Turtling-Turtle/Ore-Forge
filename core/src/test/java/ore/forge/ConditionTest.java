package ore.forge;

import ore.forge.Expressions.Condition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConditionTest {
    private final Ore ore = new Ore();

    @Test
    void testNumberComparison() {
        var testCase = Condition.parseCondition("0==0");
        assertTrue(testCase.evaluate(null));
    }

    @Test
    void testStringComparison() {
        ore.setOreName("test");
        var testCase = Condition.parseCondition("\"test\" == ORE_NAME");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testFunctionComparison() {
        ore.setOreValue(20);
        var testCase = Condition.parseCondition("{(ORE_VALUE*4)} > 79");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testLogicalAND() {
        ore.setOreValue(5);
        var testCase = Condition.parseCondition("ORE_VALUE > 4 AND {(ORE_VALUE%5)} == 0");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testLogicalNOT() {
        ore.setOreValue(5);
        var testCase = Condition.parseCondition("ORE_VALUE != 0");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testLogicalOR() {
        ore.setTemp(50);
        ore.setOreValue(20);
        var testCase = Condition.parseCondition("TEMPERATURE > 50 OR ORE_VALUE < 50");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testXOR() {
        ore.setTemp(20);
        ore.setOreValue(30);
        var testCase = Condition.parseCondition("TEMPERATURE != 0 XOR ORE_VALUE < 20");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testComplexCondition() {
        ore.setOreName("test");
        ore.setOreValue(15);
        var testCase = Condition.parseCondition("ORE_NAME == \"test\" AND ! ORE_VALUE > 20");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testCollectionBasedCondition() {
        ore.getUpgradeTag(new UpgradeTag("Foolish Tag", "1234", 5, false));
        var testCase = Condition.parseCondition("UPGRADE_TAGS.CONTAINS(1234)");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testNumericRetriever() {
        var testCase = Condition.parseCondition("UPGRADE_TAGS.GET_COUNT(1234) <= 0");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testOreTemperature() {
        ore.setTemp(-30);
        var testCase = Condition.parseCondition("TEMPERATURE < 0");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testBiconditionalIsTrue() {
        ore.setOreName("foo");
        ore.setOreValue(20);
        var testCase = Condition.parseCondition("ORE_NAME == \"foo\" <-> ORE_VALUE >= 20");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testBiconditionalIsFalse() {
        ore.setOreName("bar");
        ore.setOreValue(20);
        //Left is True. Right is False. Result should be False.
        var testCase = Condition.parseCondition("ORE_NAME == \"bar\" <-> ORE_VALUE > 100");
        assertFalse(testCase.evaluate(ore));
    }

    @Test
    void testTwoFalseBiconditional() {
        ore.setOreName("foo");
        ore.setOreValue(44);
        //Left is False. Right is False. Result should be True.
        var testCase = Condition.parseCondition("ORE_NAME == \"bar\" <-> ORE_VALUE > 100");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testComplexString() {
        ore.setOreName("The Best Ore Ever");
        ore.setOreValue(101);
        var testCase = Condition.parseCondition("ORE_NAME == \"The Best Ore Ever\"");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testStringWithColon() {
        ore.setOreName("Star Wars: The Empire Strikes Back");
        var testCase = Condition.parseCondition("ORE_NAME == \"Star Wars: The Empire Strikes Back\"");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testStringWithApostrophes() {
        ore.setOreName("Turtles' Ore");
        var testCase = Condition.parseCondition("ORE_NAME == \"Turtles' Ore\"");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testStringWithDigits() {
        ore.setOreName("Uranium-238");
        var testCase = Condition.parseCondition("ORE_NAME == \"Uranium-238\"");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testParen() {
//        ore.setOreName("foo");
//        ore.setOreValue(3);
//        var testCase = Condition.parseCondition("!(ORE_NAME == \"foo\" && ORE_VALUE == 2)");
//        assertFalse(testCase.evaluate(ore));
    }

}
