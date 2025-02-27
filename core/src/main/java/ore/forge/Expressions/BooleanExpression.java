package ore.forge.Expressions;

import java.util.function.Function;

public interface BooleanExpression<E> extends Function<E, Boolean> {
    boolean evaluate(E element);

    default Boolean apply(E element) {
        return evaluate(element);
    }
}
