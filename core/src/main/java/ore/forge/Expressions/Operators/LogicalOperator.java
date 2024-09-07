package ore.forge.Expressions.Operators;

public enum LogicalOperator {
    NOT,
    XOR,
    AND,
    OR,
    BICONDITIONAL;


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
            case "AND", "&&" -> LogicalOperator.AND;
            case "OR", "||" -> LogicalOperator.OR;
            case "<->" -> LogicalOperator.BICONDITIONAL;
            default -> throw new IllegalStateException("Unexpected value: " + string);
        };
    }

    public static boolean isOperator(String string) {
        return switch (string) {
            case "NOT", "!",  "XOR", "^", "AND", "&&", "OR", "||", "->", "<->" -> true;
            default -> false;
        };
    }

    public String toString() {
        return switch (this) {
            case NOT -> "!";
            case XOR -> "^";
            case AND -> "&&";
            case OR -> "||";
            case BICONDITIONAL -> "<->";
        };
    }

    private final BooleanOperator operator;
    LogicalOperator() {
        operator = switch (this) {
            case NOT -> (left, right) -> !right;
            case XOR -> (left, right) -> left ^ right;
            case AND -> (left, right) -> left && right;
            case OR -> (left, right) -> left || right;
            case BICONDITIONAL -> (left, right) -> (left && right) || (!left && !right);
        };
    }
}
