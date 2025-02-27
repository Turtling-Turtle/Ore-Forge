package ore.forge;

import ore.forge.Expressions.Condition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        var testCase = Condition.parseCondition("test == ORE_NAME");
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
        ore.setTemperature(50);
        ore.setOreValue(20);
        var testCase = Condition.parseCondition("TEMPERATURE > 50 OR ORE_VALUE < 50");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testXOR() {
        ore.setTemperature(20);
        ore.setOreValue(30);
        var testCase = Condition.parseCondition("TEMPERATURE != 0 XOR ORE_VALUE < 20");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testComplexCondition2() {
        ore.setOreName("test");
        ore.setOreValue(15);
        var testCase = Condition.parseCondition("ORE_NAME == test AND ! ORE_VALUE > 20");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testCollectionBasedCondition2() {
        ore.getUpgradeTag(new UpgradeTag("Foolish Tag", "1234", 5, false));
        var testCase = Condition.parseCondition("UPGRADE_TAGS.CONTAINS(1234)");
        System.out.println(testCase.evaluate(ore));
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testNumericRetriever() {
        var testCase = Condition.parseCondition("UPGRADE_TAGS.GET_COUNT(1234) <= 0");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testOreTemperature() {
        ore.setTemperature(-30);
        var testCase = Condition.parseCondition("TEMPERATURE < 0");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testBiconditionalIsTrue() {
        ore.setOreName("foo");
        ore.setOreValue(20);
        var testCase = Condition.parseCondition("ORE_NAME == foo <-> ORE_VALUE >= 20");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testBiconditionalIsFalse() {
        ore.setOreName("bar");
        ore.setOreValue(20);
        //Left is True. Right is False. Result should be False.
        var testCase = Condition.parseCondition("ORE_NAME == bar <-> ORE_VALUE > 100");
        assertFalse(testCase.evaluate(ore));
    }

    @Test
    void testTwoFalseBiconditional() {
        ore.setOreName("foo");
        ore.setOreValue(44);
        //Left is False. Right is False. Result should be True.
        var testCase = Condition.parseCondition("ORE_NAME == bar <-> ORE_VALUE > 100");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testParen() {
        ore.setOreName("foo");
        ore.setOreValue(3);
        var testCase = Condition.parseCondition("!(ORE_NAME == foo && ORE_VALUE == 2)");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testParenExplicitPrecedence() {
        var testCase = Condition.parseCondition("(5 == 5 && 3 > 2) || 4 < 3");
        assertTrue(testCase.evaluate(ore));
    }

    @Test
    void testParenExplicitPrecedence2() {
        var testCase = Condition.parseCondition("5 == 5 && (3 > 2 || 4 < 3)");
        assertTrue(testCase.evaluate(ore));
    }

}
