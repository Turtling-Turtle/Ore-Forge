package ore.forge.Strategies;

import com.badlogic.gdx.utils.Queue;
import ore.forge.Enums.*;
import ore.forge.Ore;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @author Nathan Ulmen
* Represents a Boolean Expression, evaluates an argument, returning a booelan result.
* Arguments can be numeric or String based(comparing IDs/names,types, etc.)
* {@link ore.forge.Strategies.Function} are supported as operands/arguments and can be embedded into the expression as an argument however they must be wrapped in {}.
* Supported Logical Operators: NOT(!), XOR(^), AND(&&), OR(||).
* Supported Comparsion Operators: >, <, >=, <=, ==, !=.
*/
public class Condition {
    private interface BooleanExpression {
        boolean evaluate(Ore ore);
    }
    private final static Pattern pattern = Pattern.compile("\\{([^}]*)}|\\(|\\)|[<>]=?|==|!=|&&|\\|\\||!|[a-zA-Z_ ]+|\\d+(\\\\.\\\\d+)?"); //Might be broken because I updated Function Regex
    private final ArrayList<BooleanExpression> expressions;
    private final Stack<LogicalOperator> logicalOperators;

    private Condition(ArrayList<BooleanExpression> expressions, Stack<LogicalOperator> logicalOperators) {
        this.expressions = expressions;
        this.logicalOperators = logicalOperators;
    }

    //TODO: Implement an internal state machine for what type of Operand to expect next.
    public static Condition parseCondition(String condition) {
        Matcher matcher = pattern.matcher(condition);
        Stack<LogicalOperator> logicalOperators = new Stack<>();
        Stack<ComparisonOperator> comparisonOperators = new Stack<>();
        Stack<Object> operands = new Stack<>();
        while (matcher.find()) {
            String token = matcher.group();
            token = token.trim();
            if (token.isEmpty() || token.equals(" ")) {
                //ignore " "
            } else if (ComparisonOperator.isOperator(token)) {
                comparisonOperators.push(ComparisonOperator.fromSymbol(token));
            } else if (LogicalOperator.isOperator(token)) {
                logicalOperators.push(LogicalOperator.fromString(token));
            } else if (NumericOreProperties.isProperty(token)) {
                operands.push(NumericOreProperties.valueOf(token));
            } else if (Function.isNumeric(token)) {
                operands.push(new Function.Constant(Double.parseDouble(token)));
            } else if (token.contains("{") && token.contains("}")) {
                token = token.replace("{", "").replace("}", "");
                Function function = Function.parseFunction(token);
                operands.push(function);
            } else if (StringOreProperty.isProperty(token)) {
                operands.push(StringOreProperty.fromString(token));
            } else if (ValueOfInfluence.isValue(token)) {
                operands.push(ValueOfInfluence.valueOf(token));
            } else {
                StringConstant constant = new StringConstant(token);
                operands.push(constant);
            }
        }
        return buildFromRPN(logicalOperators, comparisonOperators, operands);
    }

    private static Condition buildFromRPN(Stack<LogicalOperator> logicalOperators, Stack<ComparisonOperator> comparisonOperators, Stack<Object> operands) {
        ArrayList<BooleanExpression> expressionQueue = new ArrayList<>(); //We treat this like a Queue.
        while (!operands.isEmpty() && operands.size() - 2 >= 0) {
            if (operands.peek() instanceof NumericOperand) {
                NumericOperand right = (NumericOperand) operands.pop();
                ComparisonOperator comparisonOperator = comparisonOperators.pop();
                NumericOperand left = (NumericOperand) operands.pop();
                NumericExpression expression = new NumericExpression(left, right, comparisonOperator);
                expressionQueue.add(expression);
            } else if (operands.peek() instanceof StringOperand) {
                StringOperand right = (StringOperand) operands.pop();
                StringOperand left = (StringOperand) operands.pop();
                ComparisonOperator comparisonOperator = comparisonOperators.pop();
                StringExpression expression = new StringExpression(left, right, comparisonOperator);
                expressionQueue.add(expression);
            }
        }
        return new Condition(expressionQueue, logicalOperators);
    }

//    TODO FIX BUG with indexes and everything.
    public boolean evaluate(Ore ore) {
        int top = 0;
        boolean[] results = new boolean[expressions.size()];//emulates a stack, so we can work with boolean primitive.
        for (BooleanExpression expression : expressions) {
            results[top++] = expression.evaluate(ore);
        }
        if (logicalOperators != null && !logicalOperators.isEmpty()) {
            for (LogicalOperator operator : logicalOperators) {
                switch (operator) {
                    case AND, OR, XOR:
                        boolean right = results[--top];
                        boolean left = results[--top];
                        results[top++] = operator.evaluate(left, right);
                        break;
                    case NOT:
                        boolean result = results[--top];
                        results[top++] = operator.evaluate(false, result); //Not.evaluate() method only evaluates right argument
                        break;
                }
            }
        }
        return results[--top];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int logicalIndex = 0;
        for (int i = expressions.size() - 1; i >= 0; i--) {
            builder.append(expressions.get(i).toString()).append(" ");
            if (logicalIndex < logicalOperators.size()) {
                builder.append(logicalOperators.get(logicalIndex++).toString()).append(" ");
            }
        }
        return builder.toString();
    }

    public record StringConstant(String string) implements StringOperand {
        @Override
        public String asString(Ore ore) {
            return string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    public record NumericExpression(NumericOperand left, NumericOperand right, ComparisonOperator operator) implements BooleanExpression {
        @Override
        public boolean evaluate(Ore ore) {
            return operator.evaluate(left.calculate(ore), right.calculate(ore));
        }

        @Override
        public String toString() {
            return left + " " + operator.asSymbol() + " " + right;
        }
    }

    public record StringExpression(StringOperand left, StringOperand right, ComparisonOperator operator) implements BooleanExpression {
        @Override
        public boolean evaluate(Ore ore) {
            if (operator == ComparisonOperator.EQUAL_TO) {
                return left.asString(ore).equals(right.asString(ore));
            } else if (operator == ComparisonOperator.NOT_EQUAL_TO) {
                return !left.asString(ore).equals(right.asString(ore));
            } else {
                throw new RuntimeException("invalid comparison operator: " + operator);
            }
        }

        @Override
        public String toString() {
            return left + " " + operator.asSymbol() + " " + right;
        }
    }

    public static void main(String[] args) {
        StringConstant stringConstant = new StringConstant("Yes");
        StringConstant stringConstant2 = new StringConstant("No");
        StringExpression stringExpression = new StringExpression(stringConstant, stringConstant2, ComparisonOperator.EQUAL_TO);
        Queue<BooleanExpression> expressionQueue = new Queue<>();
        expressionQueue.addFirst(stringExpression);
        ArrayList<Condition> holder = new ArrayList<>(100_000);
//        Stack<LogicalOperator> logicalOperators = new Stack<>();
//        logicalOperators.push(LogicalOperator.NOT);
//        Condition condition = new Condition(expressionQueue, null);

        String conditionString = "MULTIORE < 3";
//        String conditionString = "{((ORE_VALUE * 2) + 1)} > 100 && NAME == test";
        Condition condition2 = Condition.parseCondition(conditionString);
        Ore ore = new Ore();
        ore.applyBaseStats(100, 0, 1, "test", "0", null);
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 100_000; i++) {
            Condition condition = Condition.parseCondition(conditionString);
            holder.add(condition);
        }
        System.out.println(condition2.toString());
        System.out.println(System.currentTimeMillis() - t1 + "ms to parse");
        t1 = System.nanoTime();
        for (Condition condition1 : holder) {
            condition1.evaluate(ore);
        }
        // ~253.226 nanoseconds to evaluate the expression.
        System.out.println(System.nanoTime() - t1 + " nano sec to evaluate condition " + holder.size() + " times");
        System.out.println(condition2.evaluate(ore));
    }

}
