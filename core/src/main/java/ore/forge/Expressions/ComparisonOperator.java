package ore.forge.Expressions;


public enum ComparisonOperator {
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_EQUAL_TO,
    LESS_THAN_EQUAL_TO,
    EQUAL_TO ,
    NOT_EQUAL_TO;

    public final boolean evaluate(double left, double right) {
        return comparator.evaluate(left, right);
    }

    @FunctionalInterface
    private interface DoubleBooleanEvaluator {
        boolean evaluate(double x, double y);
    }

    private final DoubleBooleanEvaluator comparator;

    public static ComparisonOperator fromSymbol(String symbol) {
        return switch(symbol) {
            case ">" -> GREATER_THAN;
            case "<" -> LESS_THAN;
            case ">=" -> GREATER_THAN_EQUAL_TO;
            case "<=" -> LESS_THAN_EQUAL_TO;
            case "==" -> EQUAL_TO;
            case "!=" -> NOT_EQUAL_TO;
            default -> throw new IllegalArgumentException("Unexpected value: " + symbol);
        };
    }

    public static boolean isOperator(String symbol) {
        return switch (symbol) {
            case ">", "<", ">=", "<=", "==", "!=" -> true;
            default -> false;
        };
    }

    public String asSymbol() {
        return switch (this) {
            case GREATER_THAN -> ">";
            case LESS_THAN -> "<";
            case GREATER_THAN_EQUAL_TO -> ">=";
            case LESS_THAN_EQUAL_TO -> "<=";
            case EQUAL_TO -> "==";
            case NOT_EQUAL_TO -> "!=";
        };
    }

    ComparisonOperator() {
        comparator = switch (this) {
            case GREATER_THAN -> (x, y) -> x > y;
            case LESS_THAN -> (x, y) -> x < y;
            case GREATER_THAN_EQUAL_TO -> (x, y) -> x >= y;
            case LESS_THAN_EQUAL_TO -> (x, y) -> x <= y;
            case EQUAL_TO -> (x, y) -> x == y;
            case NOT_EQUAL_TO -> (x, y) -> x != y;
        };
    }
}
