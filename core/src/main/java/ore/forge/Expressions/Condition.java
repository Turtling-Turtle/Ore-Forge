package ore.forge.Expressions;

import ore.forge.Expressions.Operands.MethodBasedOperand;
import ore.forge.Expressions.Operands.NumericOreProperties;
import ore.forge.Expressions.Operands.StringOreProperty;
import ore.forge.Expressions.Operands.ValueOfInfluence;
import ore.forge.Expressions.Operators.ComparisonOperator;
import ore.forge.Expressions.Operators.LogicalOperator;
import ore.forge.Ore;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Condition implements BooleanExpression<Ore> {
    private final BooleanExpression<Ore> expression;
    private final static Pattern pattern = Pattern.compile("(([A-Z_]+\\.)([A-Z_]+)\\(([^)]+)\\))|\\{([^}]*)}|\\(|\\)|<->|[<>]=?|==|!=|&&|\\|\\||!|[a-zA-Z_]+|([-+]?\\d*\\.?\\d+(?:[eE][-+]?\\d+)?)");

    private Condition(BooleanExpression<Ore> expression) {
        this.expression = expression;
    }

    private enum Parenthesis {
        LEFT("("),
        RIGHT(")");

        final String symbol;

        Parenthesis(String s) {
            symbol = s;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    public static Condition parseCondition(String condition) {
        Matcher matcher = pattern.matcher(condition);
        Deque<Object> operators = new ArrayDeque<>();
        Deque<Object> operands = new ArrayDeque<>();

        while (matcher.find()) {
            String token = matcher.group();
            token = token.trim();
            if (token.isEmpty() || token.equals(" ")) {
                //ignore " "
                continue;
            } else if (token.equals("(")) {
                operators.push(Parenthesis.LEFT);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && operators.peek() != Parenthesis.LEFT) {
                    buildExpression(operators, operands);
                }
                if (operators.isEmpty() || !operators.peek().equals(Parenthesis.LEFT)) {
                    throw new IllegalArgumentException("Mismatched parenthesis in: " + condition);
                }
                operators.pop(); //remove "("
            } else if (token.contains("{") && token.contains("}")) {
                token = token.replace("{", "").replace("}", "");
                ore.forge.Expressions.Function function = ore.forge.Expressions.Function.parseFunction(token);
                operands.push(function);
            } else if (token.contains("(") && token.contains(")") && matcher.group(2).charAt(matcher.group(2).length() - 1) == '.') {//Method verification.
                var argumentSource = matcher.group(2);
                argumentSource = argumentSource.substring(0, argumentSource.length() - 1);
                if (MethodBasedOperand.isCollection(argumentSource)) { //Verify that collection is valid.
                    var method = matcher.group(3);
                    if (MethodBasedOperand.methodIsValid(method)) { //Verify that method is valid
                        if (method.equals("GET_COUNT")) { //Determine return type of method(number vs boolean)
                            operands.push(new NumericMethodOperand(matcher.group(4), MethodBasedOperand.valueOf(argumentSource)));
                        } else {
                            operands.push(new BooleanMethodOperand(matcher.group(4), MethodBasedOperand.valueOf(argumentSource)));
                        }
                    }
                }
            } else if (ComparisonOperator.isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(ComparisonOperator.fromSymbol(token))) {
                    buildExpression(operators, operands);
                }
                operators.push(ComparisonOperator.fromSymbol(token));
            } else if (LogicalOperator.isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(LogicalOperator.fromString(token))) {
                    buildExpression(operators, operands);
                }
                operators.push(LogicalOperator.fromString(token));
            } else if (NumericOreProperties.isProperty(token)) {
                operands.push(NumericOreProperties.valueOf(token));
            } else if (ore.forge.Expressions.Function.isNumeric(token)) {
                operands.push(new ore.forge.Expressions.Function.Constant(Double.parseDouble(token)));
            } else if (StringOreProperty.isProperty(token)) {
                operands.push(StringOreProperty.fromString(token));
            } else if (ValueOfInfluence.isValue(token)) {
                operands.push(ValueOfInfluence.valueOf(token));
            } else {
                operands.push(new StringConstant(token));
            }
        }
        while (!operators.isEmpty()) {
            if (operators.peek() == Parenthesis.LEFT) {
                throw new IllegalArgumentException("Mismatched parenthesis in: " + condition);
            }
            buildExpression(operators, operands);
        }
        if (operands.peek() instanceof BooleanExpression) {
            throw new IllegalStateException("Final parsed expression is not a BooleanExpression: " + operands.peek());
        }
        return new Condition((BooleanExpression<Ore>) operands.pop());
    }

    private static int precedence(Object operator) {
        if (operator instanceof LogicalOperator logicalOperator) {
            return switch (logicalOperator) {
                case BICONDITIONAL -> 0;
                case OR -> 1;
                case XOR -> 2;
                case AND -> 3;
                case NOT -> 4;

            };
        } else if (operator instanceof ComparisonOperator) {
            return 5;
        } else if (operator == Parenthesis.LEFT) {
            return 0;
        }
        throw new IllegalArgumentException("Unknown operator: " + operator);
    }


    private static void buildExpression(Deque<Object> operators, Deque<Object> operands) {
        var operator = operators.pop();
        if (operator instanceof ComparisonOperator comparisonOperator) {
            var right = operands.pop();
            var left = operands.pop();
            operands.push(new Comparison(comparisonOperator, (Function) left, (Function) right));
        } else if (operator instanceof LogicalOperator logicalOperator) {
            var right = operands.pop();
            if (operands.isEmpty() || logicalOperator == LogicalOperator.NOT) {
                operands.push(new LogicalExpression(null, (BooleanExpression<Ore>) right, logicalOperator));
                return;
            }
            var left = operands.pop();
            operands.push(new LogicalExpression((BooleanExpression<Ore>) left, (BooleanExpression<Ore>) right, logicalOperator));
        } else {
            throw new IllegalArgumentException("IDK how we made it here");
        }
    }

    @Override
    public boolean evaluate(Ore ore) {
        return expression.evaluate(ore);
    }

    private record Comparison<E, V extends Comparable<V>>(ComparisonOperator comparisonOperator, Function<E, V> left,
                                                          Function<E, V> right) implements BooleanExpression<E> {
        @Override
        public boolean evaluate(E ore) {
            return comparisonOperator.compare(left.apply(ore), right.apply(ore));
        }

    }

    private record LogicalExpression(BooleanExpression<Ore> left, BooleanExpression<Ore> right,
                                     LogicalOperator operator) implements BooleanExpression<Ore> {
        @Override
        public boolean evaluate(Ore element) {
            //For logical NOT, only
            if (left != null) {
                return operator.evaluate(left.evaluate(element), right.evaluate(element));
            } else {
                return operator.evaluate(false, right.evaluate(element));
            }

        }

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

    public record NumericMethodOperand(String targetID, MethodBasedOperand source) implements NumericOperand {
        @Override
        public double calculate(Ore ore) {
            return source.calculate(ore, targetID);
        }
    }

    public record BooleanMethodOperand(String targetID, MethodBasedOperand source) implements BooleanExpression<Ore> {
        @Override
        public boolean evaluate(Ore ore) {
            return source.evaluate(ore, targetID);
        }
    }
}
