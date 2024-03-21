package ore.forge;

@FunctionalInterface

public interface BinomialFunction<X, Y, R> {
    R apply(X x, Y y);
}
