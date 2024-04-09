package ore.forge.Strategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Enums.Operator;
import ore.forge.Enums.OreProperty;
import ore.forge.Enums.ValueOfInfluence;
import ore.forge.Ore;

import java.util.Stack;

//@author Nathan Ulmen
//A pair of parenthesis encapsulate a function.
//A function is composed of a left operand, right operand, and an operator.
//An operand can be a KeyValue, fixed number(double, int, float, etc.), or another function.
//KeyValues are Enums. Each enum will call its associated method to get its value. EX: if the enum is ORE_VALUE then calling ORE_VALUE.getAssociatedValue(Ore ore) will return the value of the ore.
//This class will parse out an equation from a String and return a function which takes an ore and returns a number.

//Pseudo-Code Example:
//Function parsedEquation;
//String exampleEquation = "(((ORE_VALUE + 100)/2) - (ORE_VALUE * (TEMPERATURE / 10)))";
//Left side:
    //f1= operator.apply(ORE_VALUE, 100);
    //f2 = operator.apply(f1, 2);
//Right Side:
    //f3 = operator.apply(TEMPERATURE, 10);
    //f4 = operator.apply(ORE_VALUE, f3);
//parsedEquation = operator.apply(f2, f4);
public class Function implements Operand {

    //THINGS TO RESEARCH FOR THIS:
    //Tokenization
    //Shunting Yard Algorithm
    //Stack Based-Parsing.
    //Recursive Decent Parsing.

    private final Operand leftOperand, rightOperand;
    private final Operator operator;

    private Function(Operand leftOperand, Operand rightOperand, Operator operator) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operator = operator;
    }

    @Override
    public double getOperandValue(Ore ore) {
        return calculate(ore);
    }

    public double calculate(Ore ore) {
        return operator.apply(leftOperand.getOperandValue(ore), rightOperand.getOperandValue(ore));
    }

    //Takes a Json Value, extracts the function string from it, then creates a function object based on the extracted string
    public static Function parseFunction(JsonValue jsonValue) {
//        String equation = jsonValue.getString("equation");
        String exampleEquation = "(((ORE_VALUE + 100)/2) - (ORE_VALUE * (TEMPERATURE / 10)))";
        System.out.println(Double.valueOf("100"));

        return null;
    }

    public static Function parseFunction(String function) {

        return null;
    }

    private Operand operandFromString(String operandString) {
        //Check to see if member of Ore property
        try { return OreProperty.valueOf(operandString); } catch (Exception ignored) {}

        //Check to see if member of ValueOfInfluence
        try { return ValueOfInfluence.valueOf(operandString); } catch (Exception ignored) {}

        try {
            return new FixedValue(Double.valueOf(operandString)); //Check to see if it's a number.
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid operand: " + operandString);
        }
    }

    private Operator operatorFromString(String operatorString) {
        try { return Operator.fromSymbol(operatorString); } catch (Exception ignored) {}

        try {
            return Operator.valueOf(operatorString);
        } catch (IllegalArgumentException e2) {
            throw new RuntimeException("Invalid operator: " + operatorString);
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
        String exampleEquation = "(((ORE_VALUE + 100) / 2) - (ORE_VALUE * (TEMPERATURE / 10)))";
//        //Left Side:
//        Function function1 = new Function(OreProperty.ORE_VALUE, new FixedValue(100), Operator.MULTIPLY); //(ORE_VALUE * 100)
//        Function function2 = new Function(function1, new FixedValue(2), Operator.DIVIDE); //(function1 / 2)
//        //Right Side:
//        Function function3 = new Function(OreProperty.TEMPERATURE, new FixedValue(10), Operator.DIVIDE); //(TEMPERATURE / 10)
//        Function function4 = new Function(OreProperty.ORE_VALUE, function3, Operator.MULTIPLY); //(ORE_VALUE * function3)
//
//        Function finishedFunction = new Function(function2, function4, Operator.SUBTRACT);//(function2 / function4)

        Stack<Function> stack = new Stack<Function>();
        exampleEquation = exampleEquation.replaceAll("\\s", "");
        String[] result = exampleEquation.split("\\w+");
        for (String token : result) {
            if (token.equals("(")) {

            } else if (token.equals(")")) {

            } else if(Operator.isOperator(token)) {

            } else if (OreProperty.isProperty(token)) {

            } else if (ValueOfInfluence.isValue(token)) {

            } else { //means it's a fixed number

            }
            System.out.println(token);
        }


    }
}
