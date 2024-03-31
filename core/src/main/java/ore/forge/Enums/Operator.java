package ore.forge.Enums;

public enum Operator {
    ADD {
        @Override
        public final double apply(double x, double y) {
            return x + y;
        }
    },
    SUBTRACT {
        @Override
        public final double apply(double x, double y) {
            return x - y;
        }
    },
    MULTIPLY {
        @Override
        public final double apply(double x, double y) {
          return x * y;
        }
    },
    DIVIDE {
        @Override
        public final double apply(double x, double y) {
            return x / y;
        }
    },
    EXPONENT {
        @Override
        public final double apply(double x, double y) {
            return Math.pow(x, y);
        }
    },
    ASSIGNMENT {//Used for setting values.
        @Override
        public final double apply(double x, double y) {
          return y;
      }
    },
    MODULO {
        @Override
        public final double apply(double x, double y) {
            return x % y;
        }
    };

    public abstract double apply(double x, double y);

}
