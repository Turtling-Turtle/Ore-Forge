package ore.forge.Strategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Enums.NumericOperator;
import ore.forge.Enums.NumericOreProperties;
import ore.forge.Enums.ValueOfInfluence;
import ore.forge.Ore;

import java.util.Random;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**@author Nathan Ulmen
* A pair of parenthesis encapsulate a function.
* A function is composed of a left operand, right operand, and an operator.
* An operand can be a KeyValue, Fixed number(double, int, float, etc.), or another Function.
* KeyValues are Enums. Each enum will call its associated method to get its value.
*   EX: if the enum is ORE_VALUE then calling ORE_VALUE.getAssociatedValue(ore) will return the value of the ore.
* This class will parse an equation from a String and return a Function.
*/
public class Function implements NumericOperand {
    private final static Pattern pattern = Pattern.compile("([a-zA-Z_]+|\\(|\\)|\\d+(\\.\\d+)?|\\+|-|\\*|/|=|%|\\^)"); //regex sucks
    private final NumericOperand leftNumericOperand, rightNumericOperand;
    private final NumericOperator numericOperator;

    public Function(NumericOperand leftNumericOperand, NumericOperand rightNumericOperand, NumericOperator numericOperator) {
        this.leftNumericOperand = leftNumericOperand;
        this.rightNumericOperand = rightNumericOperand;
        this.numericOperator = numericOperator;
    }

    //Takes a Json Value, extracts the function string from it, then creates a function object based on the extracted string
    public static Function parseFunction(JsonValue jsonValue) {
        String equation = jsonValue.getString("upgradeFunction");
        return parseFunction(equation);
    }

    //Takes a Json Value, extracts the string from it, then creates a Function object based on the extracted string
    public static Function parseFunction(String equation) {
        equation = equation.replaceAll("\\s", ""); //get rid of spaces in Function string.
        Matcher matcher = pattern.matcher(equation);
        return parseFromTokens(matcher);
    }

    /**Uses the Shunting Yard Algorithm: <a href="https://en.wikipedia.org/wiki/Shunting_yard_algorithm">...</a>
     to parse the Function from the string.*/
    //TODO: Implement an internal state machine for what type of Operand to expect next.
    private static Function parseFromTokens(Matcher matcher) {
        Stack<NumericOperand> operandStack = new Stack<>();
        Stack<NumericOperator> numericOperatorStack = new Stack<>();
        while (matcher.find()) {
            String token = matcher.group();
            if (token.equals("(")) { //We Ignore '(' char
            } else if (token.equals(")")) {
                NumericOperand right = operandStack.pop();
                NumericOperand left = operandStack.pop();
                NumericOperator numericOperator = numericOperatorStack.pop();
                operandStack.push(new Function(left, right, numericOperator));
            } else if (NumericOperator.isOperator(token)) {
                numericOperatorStack.push(NumericOperator.fromSymbol(token));
            } else if (NumericOreProperties.isProperty(token)) {
                operandStack.push(NumericOreProperties.valueOf(token));
            } else if (ValueOfInfluence.isValue(token)) {
                operandStack.push(ValueOfInfluence.valueOf(token));
            } else if (isNumeric(token)) {
                operandStack.push(new Constant(Double.parseDouble(token)));
            } else {
                throw new RuntimeException("Unknown token: " + token);
            }
        }
        return (Function) operandStack.pop();
    }

    @Override
    public double calculate(Ore ore) {
        return numericOperator.apply(leftNumericOperand.calculate(ore), rightNumericOperand.calculate(ore));
    }

    public static boolean isNumeric(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public String toString() {
        return ("(" + leftNumericOperand + " " + numericOperator.asSymbol() + " " + rightNumericOperand + ")");
    }

    /**Record class to keep track of primitive doubles*/
    public record Constant(double value) implements NumericOperand {
        @Override
        public double calculate(Ore ore) {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    //Method used for testing class
    public static void main(String[] args) {
        //String exampleEquation = "(((ORE_VALUE = 100) % 2) - (ORE_VALUE * (TEMPERATURE / 10)) + (MULTIORE * (100 ^ 2)))";
        //Key Values: ORE_VALUE, TEMPERATURE, MULTIORE, UPGRADE_COUNT, SPEED, ACTIVE_ORE, PLACED_ITEMS, WALLET, PRESTIEGE_LEVEL, SPECIAL_POINTS
        //Operators: + , - , * , / , ^ , = , %

        String function = "((";
        String function2 = "((3.14 * 3) + ((200 ^ 1.02) % 5))";
        Function funkyUpgradeFunction = parseFunction(function);
        Function numericUpgradeFunction = parseFunction(function2);

        Ore ore = new Ore();
        ore.applyBaseStats(20, 50 , 0 , "Test", "idk", null);
        //((20 * 2) + 50) = 90
        System.out.println("ORE VALUE : " + ore.getOreValue());
        System.out.println("ORE TEMPERATURE: " + ore.getOreTemp());
        System.out.println(function + " Evaluates to: " + funkyUpgradeFunction.calculate(ore));

        System.out.println(function2 + " Evaluates to : " + numericUpgradeFunction.calculate(ore));
        double yourModifier = funkyUpgradeFunction.calculate(ore);

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"; //52
        String nums = "0123456789";
        // 10^3 * 52^8 = total unique combinations
        StringBuilder id = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 3; i++) {
            id.append(nums.charAt(rand.nextInt(nums.length())));
        }
        id.append("-");
        for (int i = 0; i < 8; i++) {
            id.append(chars.charAt(rand.nextInt(chars.length())));
        }
        System.out.println(id);
    }

}
