package ore.forge.Strategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Enums.Operator;
import ore.forge.Enums.OreProperty;
import ore.forge.Enums.ValueOfInfluence;
import ore.forge.Ore;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@author Nathan Ulmen
//A pair of parenthesis encapsulate a function.
//A function is composed of a left operand, right operand, and an operator.
//An operand can be a KeyValue, Fixed number(double, int, float, etc.), or another Function.
//KeyValues are Enums. Each enum will call its associated method to get its value.
    //EX: if the enum is ORE_VALUE then calling ORE_VALUE.getAssociatedValue(Ore ore) will return the value of the ore.
//This class will parse out an equation from a String and return a function which takes an ore and returns a number.

//Pseudo-Code Example:
//Function parsedEquation;
//String exampleEquation = "(((ORE_VALUE + 100)/2) - (ORE_VALUE * (TEMPERATURE / 10)))";
//Left side:
    //function1= operator.apply(ORE_VALUE, 100);
    //function2 = operator.apply(function1, 2);
//Right Side:
    //function3 = operator.apply(TEMPERATURE, 10);
    //function4 = operator.apply(ORE_VALUE, f3);
//parsedEquation = operator.apply(function2, function4);
public class Function implements Operand {
    //Tokenization
    //Shunting Yard Algorithm
    //Stack Based-Parsing.
    //Recursive Decent Parsing.

    private final static Pattern pattern = Pattern.compile("([a-zA-Z_]+|\\(|\\)|\\d+(\\.\\d+)?|\\+|-|\\*|/|=|%|\\^)"); //regex sucks
    private final Operand leftOperand, rightOperand;
    private final Operator operator;

    private Function(Operand leftOperand, Operand rightOperand, Operator operator) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operator = operator;
    }

    //Takes a Json Value, extracts the string from it, then creates a function object based on the extracted string
    public static Function parseFunction(String equation) {
        equation = equation.replaceAll("\\s", ""); //get rid of spaces in Function string.
        Matcher matcher = pattern.matcher(equation);
        return parseFromTokens(matcher);
    }

    //Takes a Json Value, extracts the function string from it, then creates a function object based on the extracted string
    public static Function parseFunction(JsonValue jsonValue) {
        String equation = jsonValue.getString("upgradeFunction");
        return parseFunction(equation);
    }

    private static Function parseFromTokens(Matcher matcher) {
        Stack<Operand> operandStack = new Stack<>();
        Stack<Operator> operatorStack = new Stack<>();
        while (matcher.find()) {
            String token = matcher.group();
            if (token.equals("(")) { //Ignore
            } else if (token.equals(")")) {
                Operand right = operandStack.pop();
                Operand left = operandStack.pop();
                Operator operator = operatorStack.pop();
                operandStack.push(new Function(left, right, operator));
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
        return (Function) operandStack.pop();
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
        String exampleEquation = "(((ORE_VALUE = 100) % 2) - (ORE_VALUE * (TEMPERATURE / 10)) + (MULTIORE * (100 ^ 2)))";
        //Key Values: ORE_VALUE, TEMPERATURE, MULTIORE, UPGRADE_COUNT, SPEED, ACTIVE_ORE, PLACED_ITEMS, WALLET, PRESTIEGE_LEVEL, SPECIAL_POINTS
        //Operators: + , - , * , / , ^ , = , %

        String yourFunction = "((ORE_VALUE * 2) + TEMPERATURE)";
        Function yourFunc = parseFunction(yourFunction);



        Ore ore = new Ore();
        ore.applyBaseStats(1, 5 , 0 , "Name", null);
        System.out.println(yourFunc);
        System.out.println(yourFunc.calculate(ore));
        double yourModifier = yourFunc.calculate(ore);



//        long t1 = System.currentTimeMillis();
//        ArrayList<Function> stored = new ArrayList<>(100_000);
//        for (int i = 0; i < 1_000_000; i++) {
//            stored.add(parseFunction(exampleEquation));
//        }

//        System.out.println(System.currentTimeMillis() - t1 + "ms to parse " + stored.size() + " functions");



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
