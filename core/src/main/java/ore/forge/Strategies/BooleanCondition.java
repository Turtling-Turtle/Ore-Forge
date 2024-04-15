package ore.forge.Strategies;

import ore.forge.Enums.ComparisonOperator;
import ore.forge.Enums.LogicalOperator;

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
//((x > y or y <= z) && (z > t or f > a or d > b))
public class BooleanCondition {
    private final static Pattern pattern = Pattern.compile("");
    private final BooleanCondition[] conditions;
    private final ComparisonOperator comparisonOperator;
    private final LogicalOperator logicalOperator;

    public BooleanCondition(E left, E right, ComparisonOperator comparisonOperator) {
        this.comparisonOperator = comparisonOperator;

    }

    public static BooleanCondition parseCondition(String condition) {
        condition = condition.replace("\\s", "");
        Matcher matcher = pattern.matcher(condition);
        Stack<LogicalOperator> logicalOperators = new Stack<>();
        Stack<ComparisonOperator> comparisonOperators = new Stack<>();
        Stack<?> operands = new Stack<>();

        while (matcher.find()) {

        }
        return null;
    }

    public boolean evaluate() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
