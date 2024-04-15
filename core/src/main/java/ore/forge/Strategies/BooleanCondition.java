package ore.forge.Strategies;

import ore.forge.Enums.ComparisonOperator;
import ore.forge.Enums.LogicalOperator;
import ore.forge.Enums.OreProperty;
import ore.forge.Ore;

import java.util.Objects;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
*@author Nathan Ulmen
* A condition evaluates an argument.
* An argument can be numeric or string based(comparing IDs/oreType)
* Supports logical Operators
* */

// ,(comma) tells us to create sub expression,
// ) tells us to end that expression
// & and or tells
// (x > y or y <= z & )
// x > y and y <= z are both expressions
// https://en.wikipedia.org/wiki/Order_of_operations#Programming_languages
// https://stackoverflow.com/questions/12494568/boolean-operators-precedence
//Priority for logical operators(left has the highest priority, left has lowest)
// NOT(!) > XOR(^) > AND(&&) > OR(||)
// >, <, ==, etc are "comparison"/"relational" operators, not the same as logical operators.
// Expression for comparisons, XYXZ for Logical Operations
// Functions embeded inside a condition must be wrapped by {}.
public class BooleanCondition {
    private interface BooleanExpression {
        boolean evaluate(Ore ore);
    }
    private final static Pattern pattern = Pattern.compile("");
    private BooleanCondition[] conditions;
    private ComparisonOperator comparisonOperator;
    private LogicalOperator logicalOperator;

    public static BooleanCondition parseCondition(String condition) {
        condition = condition.replace("\\s", "");
        Matcher matcher = pattern.matcher(condition);
        Stack<LogicalOperator> logicalOperators = new Stack<>();
        Stack<ComparisonOperator> comparisonOperators = new Stack<>();
        Stack<Object> operands = new Stack<>();
        while (matcher.find()) {
            String token = matcher.group();
            if (ComparisonOperator.isOperator(token)) {
                comparisonOperators.push(ComparisonOperator.fromSymbol(token));
            } else if (LogicalOperator.isOperator(token)) {
                logicalOperators.push(LogicalOperator.fromString(token));
            } else if (OreProperty.isProperty(token)) {
                operands.push(OreProperty.valueOf(token));
            } else if (Function.isNumeric(token)) {
                operands.push(Double.parseDouble(token));
            } else if (token.contains("{") && token.contains("}")) {
                token = token.replace("{", "").replace("}", "");
                Function function = Function.parseFunction(token);
                operands.push(function);
            }
        }
        return null;
        //now that it's been parsed into RPN we can "compile" it.
    }



    public boolean evaluate() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static void main(String[] args) {

    }

    private class NumericExpression implements BooleanExpression {
        private final Operand left, right;
        private final ComparisonOperator comparisonOperator;

        public NumericExpression(Operand left, Operand right, ComparisonOperator operator) {
            this.left = left;
            this.right = right;
            this.comparisonOperator = operator;
        }

        public boolean evaluate(Ore ore) {
            return comparisonOperator.evaluate(left.calculate(ore), right.calculate(ore));
        }

    }

    private record StringExpression(String left, String right) implements BooleanExpression {
        @Override
        public boolean evaluate(Ore ore) {
            return left.equals(right);
        }
    }

}
