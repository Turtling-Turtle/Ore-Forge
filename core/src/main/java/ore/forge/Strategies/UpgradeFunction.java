package ore.forge.Strategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Enums.Operator;
import ore.forge.Enums.OreProperty;
import ore.forge.Enums.ValueOfInfluence;
import ore.forge.Ore;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@author Nathan Ulmen
//A pair of parenthesis encapsulate a function.
//A function is composed of a left operand, right operand, and an operator.
//An operand can be a KeyValue, Fixed number(double, int, float, etc.), or another Function.
//KeyValues are Enums. Each enum will call its associated method to get its value.
//    EX: if the enum is ORE_VALUE then calling ORE_VALUE.getAssociatedValue(ore) will return the value of the ore.
//This class will parse out an equation from a String and return a function which takes an ore and returns a double.
//
//Example:
//exampleEquation = "(((ORE_VALUE + 100)/2) - (ORE_VALUE * (TEMPERATURE / 10)))";
//Left side:
    //function1= operator.apply(ORE_VALUE, 100);
    //function2 = operator.apply(function1, 2);
//Right Side:
    //function3 = operator.apply(TEMPERATURE, 10);
    //function4 = operator.apply(ORE_VALUE, f3);
//parsedEquation = operator.apply(function2, function4);
public class UpgradeFunction implements Operand {
    //Tokenization
    //Shunting Yard Algorithm
    //Stack Based-Parsing.
    //Recursive Decent Parsing.

    private final static Pattern pattern = Pattern.compile("([a-zA-Z_]+|\\(|\\)|\\d+(\\.\\d+)?|\\+|-|\\*|/|=|%|\\^)"); //regex sucks
    private final Operand leftOperand, rightOperand;
    private final Operator operator;

    private UpgradeFunction(Operand leftOperand, Operand rightOperand, Operator operator) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operator = operator;
    }

    //Takes a Json Value, extracts the string from it, then creates a function object based on the extracted string
    public static UpgradeFunction parseFunction(String equation) {
        equation = equation.replaceAll("\\s", ""); //get rid of spaces in Function string.
        Matcher matcher = pattern.matcher(equation);
        return parseFromTokens(matcher);
    }

    //Takes a Json Value, extracts the function string from it, then creates a function object based on the extracted string
    public static UpgradeFunction parseFunction(JsonValue jsonValue) {
        String equation = jsonValue.getString("upgradeFunction");
        return parseFunction(equation);
    }

    private static UpgradeFunction parseFromTokens(Matcher matcher) {
        Stack<Operand> operandStack = new Stack<>();
        Stack<Operator> operatorStack = new Stack<>();
        while (matcher.find()) {
            String token = matcher.group();
            if (token.equals("(")) { //We Ignore '(' char
            } else if (token.equals(")")) {
                Operand right = operandStack.pop();
                Operand left = operandStack.pop();
                Operator operator = operatorStack.pop();
                operandStack.push(new UpgradeFunction(left, right, operator));
            } else if (Operator.isOperator(token)) {
                operatorStack.push(Operator.fromSymbol(token));
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
        return (UpgradeFunction) operandStack.pop();
    }

    @Override
    public double getOperandValue(Ore ore) {
        return calculate(ore);
    }

    public double calculate(Ore ore) {
        return operator.apply(leftOperand.getOperandValue(ore), rightOperand.getOperandValue(ore));
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
        return ("(" +leftOperand + " " + operator.asSymbol() + " " + rightOperand + ")");
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
//        String exampleEquation = "(((ORE_VALUE = 100) % 2) - (ORE_VALUE * (TEMPERATURE / 10)) + (MULTIORE * (100 ^ 2)))";
        //Key Values: ORE_VALUE, TEMPERATURE, MULTIORE, UPGRADE_COUNT, SPEED, ACTIVE_ORE, PLACED_ITEMS, WALLET, PRESTIEGE_LEVEL, SPECIAL_POINTS
        //Operators: + , - , * , / , ^ , = , %

        String function = "((ORE_VALUE * 2) + TEMPERATURE)";
        String function2 = "((3.14 * 3) + ((200 ^ 1.02) % 5))";
        UpgradeFunction funkyUpgradeFunction = parseFunction(function);
        UpgradeFunction numericUpgradeFunction = parseFunction(function2);

        Ore ore = new Ore();
        ore.applyBaseStats(20, 50 , 0 , "Tests", null);
        //((20 * 2) + 50) = 90
        System.out.println("ORE VALUE : " + ore.getOreValue());
        System.out.println("ORE TEMPERATURE: " + ore.getOreTemp());
        System.out.println(function + " Evaluates to: " + funkyUpgradeFunction.calculate(ore));

        System.out.println(function2 + " Evaluates to : " + numericUpgradeFunction.calculate(ore));
        double yourModifier = funkyUpgradeFunction.calculate(ore);



//        System.out.println(parseFunction(exampleEquation));
//    [a-zA-Z_]+: This part matches one or more characters that are alphabetic (both uppercase and lowercase letters) or an underscore. This is used to match variable names like ORE_VALUE, TEMPERATURE, and MULTIORE.
//    \\d+: This part matches one or more digits. It is used to match numbers like 100 and 2.
//    \\(: This part matches the opening parenthesis (.
//    \\): This part matches the closing parenthesis ).
//    \\+: This part matches the plus sign +.
//    \\-: This part matches the minus sign -.
//    \\*: This part matches the asterisk *.
//    \\/: This part matches the forward slash /.
//    %: This part matches the percent sign %.
//    =: This part matches the equal sign =.
//    \\^: This part matches the caret ^.
//        Matcher matcher = pattern.matcher(exampleEquation);
//        while (matcher.find()) {
//            String token = matcher.group();
//            System.out.println(token);
//        }

    }

}
