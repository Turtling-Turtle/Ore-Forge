package ore.forge.Enums;


public enum ComparisonOperator {
    GREATER_THAN, LESS_THAN, GREATER_THAN_EQUAL_TO, LESS_THAN_EQUAL_TO, EQUAL_TO;

    public final boolean evaluate(double left, double right) {
        return comparator.evaluate(left, right);
    }

    @FunctionalInterface
    private interface DoubleBooleanEvaluator {
        boolean evaluate(double x, double y);
    }

    private final DoubleBooleanEvaluator comparator;
    private static ComparisonOperator fromSymbol(String symbol) {
        return switch(symbol) {
            case ">" -> GREATER_THAN;
            case "<" -> LESS_THAN;
            case ">=" -> GREATER_THAN_EQUAL_TO;
            case "<=" -> LESS_THAN_EQUAL_TO;
            case "==" -> EQUAL_TO;
            default -> throw new IllegalStateException("Unexpected value: " + symbol);
        };
    }

    ComparisonOperator() {
        comparator = switch (this) {
            case GREATER_THAN -> (x, y) -> x > y;
            case LESS_THAN -> (x, y) -> x < y;
            case GREATER_THAN_EQUAL_TO -> (x, y) -> x >= y;
            case LESS_THAN_EQUAL_TO -> (x, y) -> x <= y;
            case EQUAL_TO -> (x, y) -> x == y;
        };
    }
}
