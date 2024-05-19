package ore.forge.Strategies;

import ore.forge.Ore;
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
        var testCase = Condition.parseCondition("ORE_NAME == test AND ! ORE_VALUE > 20");
        assertTrue(testCase.evaluate(ore));
    }
}
