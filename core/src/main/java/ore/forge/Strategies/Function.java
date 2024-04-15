package ore.forge.Strategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Enums.NumericOperator;
import ore.forge.Enums.OreProperty;
import ore.forge.Enums.ValueOfInfluence;
import ore.forge.Ore;

import java.util.Random;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@author Nathan Ulmen
//A pair of parenthesis encapsulate a function.
//A function is composed of a left operand, right operand, and an operator.
//An operand can be a KeyValue, Fixed number(double, int, float, etc.), or another Function.
//KeyValues are Enums. Each enum will call its associated method to get its value.
//EX: if the enum is ORE_VALUE then calling ORE_VALUE.getAssociatedValue(ore) will return the value of the ore.
//This class will parse an equation from a String and return a Function.
public class Function implements Operand {
    private final static Pattern pattern = Pattern.compile("([a-zA-Z_]+|\\(|\\)|\\d+(\\.\\d+)?|\\+|-|\\*|/|=|%|\\^)"); //regex sucks
    private final Operand leftOperand, rightOperand;
    private final NumericOperator numericOperator;

    public Function(Operand leftOperand, Operand rightOperand, NumericOperator numericOperator) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
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

    //Shunting Yard Algorithm: https://en.wikipedia.org/wiki/Shunting_yard_algorithm
    private static Function parseFromTokens(Matcher matcher) {
        Stack<Operand> operandStack = new Stack<>();
        Stack<NumericOperator> numericOperatorStack = new Stack<>();
        while (matcher.find()) {
            String token = matcher.group();
            if (token.equals("(")) { //We Ignore '(' char
            } else if (token.equals(")")) {
                Operand right = operandStack.pop();
                Operand left = operandStack.pop();
                NumericOperator numericOperator = numericOperatorStack.pop();
                operandStack.push(new Function(left, right, numericOperator));
            } else if (NumericOperator.isOperator(token)) {
                numericOperatorStack.push(NumericOperator.fromSymbol(token));
            } else if (OreProperty.isProperty(token)) {
                operandStack.push(OreProperty.valueOf(token));
            } else if (ValueOfInfluence.isValue(token)) {
                operandStack.push(ValueOfInfluence.valueOf(token));
            } else if (isNumeric(token)) {
                operandStack.push(new FixedValue(Double.parseDouble(token)));
            } else {
                throw new RuntimeException("Unknown token: " + token);
            }
        }
        return (Function) operandStack.pop();
    }

    @Override
    public double getOperandValue(Ore ore) {
        return calculate(ore);
    }

    public double calculate(Ore ore) {
        return numericOperator.apply(leftOperand.getOperandValue(ore), rightOperand.getOperandValue(ore));
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
        return ("(" +leftOperand + " " + numericOperator.asSymbol() + " " + rightOperand + ")");
    }

    private record FixedValue(double value) implements Operand {
        @Override
        public double getOperandValue(Ore ore) {
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

        String function = "((ORE_VALUE * 2) + TEMPERATURE)";
        String function2 = "((3.14 * 3) + ((200 ^ 1.02) % 5))";
        Function funkyUpgradeFunction = parseFunction(function);
        Function numericUpgradeFunction = parseFunction(function2);

        Ore ore = new Ore();
        ore.applyBaseStats(20, 50 , 0 , "Test", null);
        //((20 * 2) + 50) = 90
        System.out.println("ORE VALUE : " + ore.getOreValue());
        System.out.println("ORE TEMPERATURE: " + ore.getOreTemp());
        System.out.println(function + " Evaluates to: " + funkyUpgradeFunction.calculate(ore));

        System.out.println(function2 + " Evaluates to : " + numericUpgradeFunction.calculate(ore));
        double yourModifier = funkyUpgradeFunction.calculate(ore);

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"; //52
        String nums = "0123456789";

        //52^8
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
