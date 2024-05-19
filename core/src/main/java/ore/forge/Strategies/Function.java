package ore.forge.Strategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Enums.NumericOperator;
import ore.forge.Enums.NumericOreProperties;
import ore.forge.Enums.ValueOfInfluence;
import ore.forge.Ore;

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
    /*
    ([a-zA-Z_]+) Matches for variables. EX: ORE_VALUE, TEMPERATURE, ACTIVE_ORE
    ([-+]?\\d*\\.?\\d+(?:[eE][-+]?\\d+)?) Matches for numbers. (includes doubles, scientific notation) EX: -2.7E9, -.1, 12.7 etc.
    |\\(|\\)|\\+|-|\\*|/|=|%|\\^ Matches for Operators (+, -, *, /, =, %, ^)
    */
    private final static Pattern pattern = Pattern.compile("([a-zA-Z_]+)|(-?\\d*\\.?\\d+(?:[eE]-?\\d+)?)|\\(|\\)|\\+|-|\\*|/|=|%|\\^");
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
        equation = equation.replaceAll("(\\d+)([-+])(\\d+)", "$1 $2 $3"); //get rid of spaces in Function string.
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
        assert numericOperatorStack.isEmpty();
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

}
