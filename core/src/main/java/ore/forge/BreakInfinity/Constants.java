package ore.forge.BreakInfinity;

class Constants {
    /**
     * For example: if two exponents are more than 17 apart,
     * consider adding them together pointless, just return the larger one
     */
    public static final int MAX_SIGNIFICANT_DIGITS = 17;

    /**
     * Largest possible exponent
     */
    public static final int EXP_LIMIT = Integer.MAX_VALUE - MAX_SIGNIFICANT_DIGITS;

    /**
     * The largest exponent that can appear in a Number, though not all mantissas are valid here.
     */
    public static final int DOUBLE_EXP_MAX = 308;

    /**
     * The smallest exponent that can appear in a Number, though not all mantissas are valid here.
     */
    public static final int DOUBLE_EXP_MIN = -324;

    /**
     * Tolerance which is used for Number conversion to compensate floating-point error.
     */
    public static final double ROUND_TOLERANCE = 1e-10;
}
