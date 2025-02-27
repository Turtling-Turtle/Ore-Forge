package ore.forge.Expressions;

import ore.forge.Ore;

import java.util.function.Function;

public interface NumericOperand extends Function<Ore, Double> {
    double calculate(Ore ore);

    default Double apply(Ore ore) {
        return calculate(ore);
    }
}
