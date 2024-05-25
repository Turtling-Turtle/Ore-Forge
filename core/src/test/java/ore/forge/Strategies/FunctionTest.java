package ore.forge.Strategies;

import ore.forge.Expressions.Function;
import ore.forge.Ore;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class FunctionTest {
    private final Ore ore = new Ore();

    @Test
    void testAddition() {
        Function simpleAddition = Function.parseFunction(("2+2"));
        assertEquals(4, simpleAddition.calculate(ore));
    }

    @Test
    void testSubtraction() {
        Function simpleSubtraction = Function.parseFunction("2-2");
        assertEquals(0, simpleSubtraction.calculate(ore));
    }

    @Test
    void testMultiplication() {
        Function simpleMultiplication = Function.parseFunction("(3*2)");
        assertEquals(6, simpleMultiplication.calculate(ore));
    }

    @Test
    void testDivision() {
        Function simpleDivision = Function.parseFunction("6/2");
        assertEquals(3, simpleDivision.calculate(ore));
    }

    @Test
    void testModulo() {
        Function simpleModulo = Function.parseFunction("(4%2)");
        assertEquals(0, simpleModulo.calculate(ore));
    }

    @Test
    void testExponent() {
        Function simpleExponent = Function.parseFunction("(3^2)");
        assertEquals(9, simpleExponent.calculate(ore));
    }

    @Test
    void testAssignment() {
        Function simpleAssignment = Function.parseFunction("(0=3)");
        assertEquals(3, simpleAssignment.calculate(ore));
    }

    @Test
    void testOreValueProperty() {
        ore.setOreValue(20);
        Function simpleValueProperty = Function.parseFunction("(ORE_VALUE*2)");
        assertEquals(40, simpleValueProperty.calculate(ore));
    }

    @Test
    void testTemperatureProperty() {
        ore.setTemp(15);
        Function simpleTemperatureProperty = Function.parseFunction("(TEMPERATURE*2)");
        assertEquals(30, simpleTemperatureProperty.calculate(ore));
    }

    @Test
    void testUpgradeCountProperty() {
        ore.setUpgradeCount(5);
        Function simpleUpgradeCountProperty = Function.parseFunction("(UPGRADE_COUNT+2)");
        assertEquals(7, simpleUpgradeCountProperty.calculate(ore));
    }

    @Test
    void testSpeedScalarProperty() {
        ore.setSpeedScalar(3);
        var speedScalarProperty = Function.parseFunction("(SPEED_SCALAR+5)");
        assertEquals(8, speedScalarProperty.calculate(ore));
    }

    @Test
    void testSubtractionAndNegative() {
        var testCase = Function.parseFunction("(7--3)");
        assertEquals(10, testCase.calculate(ore));
    }

    @Test
    void testSubtractionAndNegativeWithParenthesis() {
        var testCase = Function.parseFunction("((5+10)--5)");
        assertEquals(20, testCase.calculate(ore));
    }

    @Test
    void testScientificNotation() {
        var testCase = Function.parseFunction("(3E10+1)");
        assertEquals(Double.parseDouble("3E10") + 1, testCase.calculate(ore));
    }

    @Test
    void testScientificNotationWithDecimal() {
        var testCase = Function.parseFunction("(2.252E8-42E3)");
        assertEquals(Double.parseDouble("2.252E8") - Double.parseDouble("42E3"), testCase.calculate(ore));
    }

    @Test
    void testNewParse() {
        var testCase = Function.parseFunction("(36*3+2)/2");
        assertEquals(55, testCase.calculate(ore));
    }

    @Test
    void testParenthesis() {
        ore.setOreValue(10);
        var testCase = Function.parseFunction("(ORE_VALUE % 3 + 1) * 4");
        assertEquals(8, testCase.calculate(ore));
    }

    @Test
    void testParenthesisWithParenthesis() {
        var testCase = Function.parseFunction("(10 % 3 + 1)*4 + 36 - 2 + (360--9)");
        assertEquals(411, testCase.calculate(null));
    }

    @Test
    void testImpliedMultiply() {
        var testCase = Function.parseFunction("(5+2)(3+2)");
        assertEquals(35, testCase.calculate(ore));
    }

}
