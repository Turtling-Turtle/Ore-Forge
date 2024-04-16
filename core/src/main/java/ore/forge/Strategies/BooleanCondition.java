package ore.forge.Strategies;

import com.badlogic.gdx.utils.Queue;
import ore.forge.Enums.ComparisonOperator;
import ore.forge.Enums.LogicalOperator;
import ore.forge.Enums.NumericOreProperties;
import ore.forge.Enums.StringOreProperty;
import ore.forge.Ore;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**@author Nathan Ulmen
* Represents a Boolean Expression, evaluates an argument, returning a booelan result.
* Arguments can be numeric or String based(comparing IDs/names,types, etc.)
* {@link ore.forge.Strategies.Function} are supported as operands/arguments and can be embeded into the expression as an argument however they must be wrapped in {}.
* Supported Logical Operators: NOT(!), XOR(^), AND(&&), OR(||).
* Supported Comparsion Operators: >, <, >=, <=, ==, !=.
* */

public class BooleanCondition {
    private interface BooleanExpression {
        boolean evaluate(Ore ore);
    }
    private final static Pattern pattern = Pattern.compile("\\{([^}]*)}|\\(|\\)|[<>]=?|==|!=|&&|\\|\\||!|[a-zA-Z_ ]+|\\d+(\\\\.\\\\d+)?");
    private final Queue<BooleanExpression> expressions;
    private final Stack<LogicalOperator> logicalOperators;

    private BooleanCondition(Queue<BooleanExpression> expressions, Stack<LogicalOperator> logicalOperators) {
        this.expressions = expressions;
        this.logicalOperators = logicalOperators;
    }

    //TODO: Implement an internal state machine for what type of Operand to expect next.
    public static BooleanCondition parseCondition(String condition) {
        Matcher matcher = pattern.matcher(condition);
        Stack<LogicalOperator> logicalOperators = new Stack<>();
        Stack<ComparisonOperator> comparisonOperators = new Stack<>();
        Stack<Object> operands = new Stack<>();
        while (matcher.find()) {
            String token = matcher.group();
            token = token.trim();
            if (token.isEmpty() || token.equals(" ")) {
               //ignore " "
            }else if (ComparisonOperator.isOperator(token)) {
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
            } else {
                StringConstant constant = new StringConstant(token);
                operands.push(constant);
            }
        }
        return buildFromRPN(logicalOperators, comparisonOperators, operands);

    }

    private static BooleanCondition buildFromRPN(Stack<LogicalOperator> logicalOperators, Stack<ComparisonOperator> comparisonOperators, Stack<Object> operands) {
        Queue<BooleanExpression> expressionQueue = new Queue<>();
        while (!operands.isEmpty() && operands.size() - 2 >= 0) {
            if (operands.peek() instanceof NumericOperand) {
                NumericOperand right = (NumericOperand) operands.pop();
                ComparisonOperator comparisonOperator = comparisonOperators.pop();
                NumericOperand left = (NumericOperand) operands.pop();
                NumericExpression expression = new NumericExpression(left, right, comparisonOperator);
                expressionQueue.addFirst(expression);
            } else if (operands.peek() instanceof StringOperand) {
                StringOperand right = (StringOperand) operands.pop();
                StringOperand left = (StringOperand) operands.pop();
                ComparisonOperator comparisonOperator = comparisonOperators.pop();
                StringExpression expression = new StringExpression(left, right, comparisonOperator);
                expressionQueue.addFirst(expression);
            }

        }
        return new BooleanCondition(expressionQueue, logicalOperators);
    }

    public boolean evaluate(Ore ore) {
        Stack<Boolean> results = new Stack<>();
        for (BooleanExpression expression : expressions) {
            results.push(expression.evaluate(ore));
        }
        if (logicalOperators != null && !logicalOperators.isEmpty()) {
            for (LogicalOperator operator : logicalOperators) {
                switch (operator) {
                    case AND, OR, XOR:
                        Boolean right = results.pop();
                        Boolean left = results.pop();
                        results.push(operator.evaluate(left, right));
                        break;
                    case NOT:
                        Boolean result = results.pop();
                        results.push(operator.evaluate(false, result));
                        break;
                }
            }
        }
        return results.pop();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public record StringConstant(String string) implements StringOperand{
        @Override
        public String asString(Ore ore) {
            return string;
        }
    }

    public record NumericExpression(NumericOperand left, NumericOperand right, ComparisonOperator operator) implements BooleanExpression {
        public boolean evaluate(Ore ore) {
            return operator.evaluate(left.calculate(ore), right.calculate(ore));
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
    }

    public static void main(String[] args) {
        StringConstant stringConstant = new StringConstant("Yes");
        StringConstant stringConstant2 = new StringConstant("No");
        StringExpression stringExpression = new StringExpression(stringConstant, stringConstant2, ComparisonOperator.EQUAL_TO);
        Queue<BooleanExpression> expressionQueue = new Queue<>();
        expressionQueue.addFirst(stringExpression);
//        Stack<LogicalOperator> logicalOperators = new Stack<>();
//        logicalOperators.push(LogicalOperator.NOT);
        BooleanCondition condition = new BooleanCondition(expressionQueue, null);
        String conditionString = "{((ORE_VALUE * 2) + 1)} > 100 && NAME == test";
        BooleanCondition condition2 = BooleanCondition.parseCondition(conditionString);
        Ore ore = new Ore();
        ore.applyBaseStats(100, 0, 1, "test", "0", null);
        System.out.println(condition2.evaluate(ore));
    }

}
