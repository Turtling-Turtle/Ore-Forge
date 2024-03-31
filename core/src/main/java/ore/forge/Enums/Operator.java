package ore.forge.Enums;

import java.util.function.DoubleBinaryOperator;

public enum Operator {
    ADD, SUBTRACT, MULTIPLY, DIVIDE, EXPONENT, ASSIGNMENT, MODULO;

    public final double apply(double x, double y) {
        return operator.applyAsDouble(x,y);
    }

    private final DoubleBinaryOperator operator;

    Operator() {
        operator = switch (this) {
            case ADD -> (x,y) -> x + y;
            case SUBTRACT -> (x,y) -> x - y;
            case MULTIPLY -> (x,y) -> x * y;
            case DIVIDE -> (x,y) -> x / y;
            case EXPONENT -> (x, y) -> Math.pow(x, y);
            case ASSIGNMENT -> (x,y) -> y;
            case MODULO -> (x,y) -> x % y;
        };
    }

}
