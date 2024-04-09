package ore.forge.Enums;

import java.util.function.DoubleBinaryOperator;

public enum Operator {
    ADD, SUBTRACT, MULTIPLY, DIVIDE, EXPONENT, ASSIGNMENT, MODULO;

    public final double apply(double x, double y) {
        return operator.applyAsDouble(x, y);
    }

    private final DoubleBinaryOperator operator;

    public static Operator fromSymbol(char operatorSymbol) {
        return switch (operatorSymbol) {
            case '+' -> Operator.ADD;
            case '-' -> Operator.SUBTRACT;
            case '*' -> Operator.MULTIPLY;
            case '/' -> Operator.DIVIDE;
            case '^' -> Operator.EXPONENT;
            case '=' -> Operator.ASSIGNMENT;
            case '%' -> Operator.MODULO;
            default -> throw new IllegalArgumentException("Invalid operator: " + operatorSymbol);
        };
    }

    public static Operator fromSymbol(String operatorSymbol) {
        return switch (operatorSymbol) {
            case "+" -> Operator.ADD;
            case "-" -> Operator.SUBTRACT;
            case "*" -> Operator.MULTIPLY;
            case "/" -> Operator.DIVIDE;
            case "^" -> Operator.EXPONENT;
            case "=" -> Operator.ASSIGNMENT;
            case "%" -> Operator.MODULO;
            default -> throw new IllegalArgumentException("Invalid operator: " + operatorSymbol);
        };
    }

    public String asSymbol() {
        return switch (this) {
            case ADD -> "+";
            case SUBTRACT -> "-";
            case MULTIPLY -> "*";
            case DIVIDE -> "/";
            case EXPONENT -> "^";
            case ASSIGNMENT -> "=";
            case MODULO -> "%";
        };
    }

    public static boolean isOperator(String symbol) {
        return switch (symbol) {
            case "+", "-" , "*", "/", "^", "=", "%" -> true;
            default -> false;
        };
    }

    Operator() {
        operator = switch (this) {
            case ADD -> (x, y) -> x + y;
            case SUBTRACT -> (x, y) -> x - y;
            case MULTIPLY -> (x, y) -> x * y;
            case DIVIDE -> (x, y) -> x / y; //TODO: Handle division by zero....
            case EXPONENT -> (x, y) -> Math.pow(x, y);
            case ASSIGNMENT -> (x, y) -> y;
            case MODULO -> (x, y) -> x % y;
        };
    }

}
