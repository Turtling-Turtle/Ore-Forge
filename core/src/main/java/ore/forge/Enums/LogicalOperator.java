package ore.forge.Enums;

public enum LogicalOperator {
    NOT,
    XOR,
    AND,
    OR;

    public boolean evaluate(boolean left, boolean right) {
        return operator.evaluate(left, right);
    }

    private interface BooleanOperator {
        boolean evaluate(boolean left, boolean right);
    }

    public static LogicalOperator fromString(String string) {
        string = string.toUpperCase();
        return switch (string) {
            case "NOT", "!" -> LogicalOperator.NOT;
            case "XOR", "^" -> LogicalOperator.XOR;
            case "AND", "&", "&&" -> LogicalOperator.AND;
            case "OR", "|", "||" -> LogicalOperator.OR;
            default -> throw new IllegalStateException("Unexpected value: " + string);
        };
    }

    public static boolean isOperator(String string) {
        return switch (string) {
            case "NOT", "!",  "XOR", "^", "AND", "&", "&&", "OR", "|", "||" -> true;
            default -> false;
        };
    }

    private final BooleanOperator operator;
    LogicalOperator() {
        operator = switch (this) {
            case NOT -> (left, right) -> !right;
            case XOR -> (left, right) -> left ^ right;
            case AND -> (left, right) -> left && right;
            case OR -> (left, right) -> left || right;
        };
    }
}
