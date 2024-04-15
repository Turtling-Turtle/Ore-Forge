package ore.forge.Enums;

public enum LogicalOperator {
    NOT,
    XOR,
    AND,
    OR;

    private boolean evaluate(boolean left, boolean right) {
        return operator.evaluate(left, right);
    }

    private interface BooleanOperator {
        boolean evaluate(boolean left, boolean right);
    }

    public LogicalOperator fromString(String string) {
        return switch (string) {
            case "NOT", "not", "!" -> LogicalOperator.NOT;
            case "XOR", "xor", "^" -> LogicalOperator.XOR;
            case "AND", "and", "&", "&&" -> LogicalOperator.AND;
            case "OR", "or", "|", "||" -> LogicalOperator.OR;
            default -> throw new IllegalStateException("Unexpected value: " + string);
        };
    }
    private final BooleanOperator operator;
    LogicalOperator() {
        operator = switch (this) {
            case NOT -> ((left, right) -> left != right);
            case XOR -> ((left, right) -> left ^ right);
            case AND -> ((left, right) -> left && right);
            case OR -> ((left, right) -> left || right);
        };
    }
}
