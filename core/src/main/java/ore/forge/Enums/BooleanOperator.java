package ore.forge.Enums;

public enum BooleanOperator {
    GREATER_THAN {
        public boolean evaluate(double x, double y) {
            return x > y;
        }
    },
    LESS_THAN {
        @Override
        public boolean evaluate(double x, double y) {
            return x < y;
        }
    },
    GREATER_THAN_EQUAL_TO {
        @Override
        public boolean evaluate(double x, double y) {
            return x >= y;
        }
    },
    LESS_THAN_EQUAL_TO {
        @Override
        public boolean evaluate(double x, double y) {
            return x <= y;
        }
    },
    EQUAL_TO {
        @Override
        public boolean evaluate(double x, double y) {
            return x == y;
        }
    };

    public abstract boolean evaluate(double x, double y);
}
