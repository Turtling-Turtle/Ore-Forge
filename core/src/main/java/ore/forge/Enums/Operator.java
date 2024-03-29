package ore.forge.Enums;

public enum Operator {
    ADD {
        public final double apply(double x, double y) {
            return x + y;
        }
    },
    SUBTRACT {
        public final double apply(double x, double y) {
            return x - y;
        }
    },
    MULTIPLY {
        public final double apply(double x, double y) {
          return x * y;
        }
    },
    DIVIDE {
        public final double apply(double x, double y) {
            return x / y;
        }
    },
    EXPONENT {
        public final double apply(double x, double y) {
            return Math.pow(x, y);
        }
    },
    ASSIGNMENT {//Used for setting values.
      public final double apply(double x, double y) {
          return y;
      }
    },
    MODULO {
        public final double apply(double x, double y) {
            return x % y;
        }
    };

    public abstract double apply(double x, double y);

}
