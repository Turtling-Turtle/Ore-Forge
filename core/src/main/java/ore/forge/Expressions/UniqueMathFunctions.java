package ore.forge.Expressions;

import ore.forge.Ore;

public enum UniqueMathFunctions {
    LN, LOG, SQRT; //SIN, COS, TAN;

    private interface FunctionBehavior {
        double calculate(Function function, Ore ore);
    }

    private final FunctionBehavior behavior;

    public double calculate(Function function, Ore ore) {
        return behavior.calculate(function, ore);
    }


    public static UniqueMathFunctions fromSymbol(String symbol) {
//        return switch (symbol.toLowerCase()) {
//            case "ln", "ln(", "ln()" -> LN;
//            case "log", "log(", "log()" -> LOG;
//            case "sqrt", "sqrt(", "sqrt()" -> SQRT;
//            default -> throw new IllegalStateException("Unexpected symbol: " + symbol);
//        };
        if (symbol.contains("ln(")) return UniqueMathFunctions.LN;
        if (symbol.contains("log(")) return UniqueMathFunctions.LOG;
        if (symbol.contains("sqrt(")) return UniqueMathFunctions.SQRT;
        throw new IllegalArgumentException("Unknown symbol: " + symbol);
    }

    public String asSymbol() {
        return switch (this) {
            case LN -> "ln";
            case LOG -> "log";
            case SQRT -> "sqrt";
        };
    }

    public static boolean isMathFunction(String symbol) {
        return symbol.contains("ln(") || symbol.contains("log(") || symbol.contains("sqrt(");
    }

    UniqueMathFunctions() {
        behavior = switch (this) {
            case LN -> (function, ore) -> Math.log(function.calculate(ore));
            case LOG -> (function, ore) -> Math.log10(function.calculate(ore));
            case SQRT -> (function, ore) -> Math.sqrt(function.calculate(ore));
        };
    }

    public record SpecialFunction(Function function, UniqueMathFunctions specialFunction) implements NumericOperand {
        @Override
        public double calculate(Ore ore) {
            return specialFunction.calculate(function, ore);
        }

        public String toString() {
            return specialFunction.asSymbol() + "(" + function.toString() + ")";
        }
    }

}
