package ore.forge.BreakInfinity;



import java.text.DecimalFormat;
import java.util.Objects;

/**
 * A BigDouble's value is simply mantissa * 10 ^ exponent.
 */
@SuppressWarnings("unused" )
public class BigDouble implements Comparable<BigDouble> {
    private double mantissa;
    private long exponent;

    private BigDouble(double mantissa, long exponent, PrivateConstructorArg unused) {
        this.mantissa = mantissa;
        this.exponent = exponent;
    }

    /**
     * Create a BigDouble by specifying the mantissa and exponent seperately.
     * @param mantissa A floating-point number between [1, 10) OR exactly 0.
     *                 Other values will be normalized to within this range.
     * @param exponent A long number of any value. Represents the exponent.
     */
    public BigDouble(double mantissa, long exponent) {
        BigDouble other = normalize(mantissa, exponent);
        this.mantissa = other.mantissa;
        this.exponent = other.exponent;
    }

    public BigDouble(@NotNull BigDouble other) {
        mantissa = other.mantissa;
        exponent = other.exponent;
    }

    /**
     * Create a BigDouble from a primitive number.
     * @param value a number to convert to a BigDouble.
     */
    public BigDouble(double value) {
        // Java hates direct assignment to this. Fine.
        BigDouble other;
        //SAFETY: Handle Infinity and NaN in a somewhat meaningful way.
        if (Double.isNaN(value)) {
            other = NaN;
        } else if (Double.isInfinite(value)) {
            if (value > 0) other = POSITIVE_INFINITY;
            else other = NEGATIVE_INFINITY;
        } else if (value == 0) {
            other = ZERO;
        } else {
            other = normalize(value, 0);
        }
        this.mantissa = other.mantissa;
        this.exponent = other.exponent;
    }

    /**
     * Create a BigDouble from a properly formatted
     * @param value A String of the form X.XXeYYY, where
     *              X.XX is the mantissa and YYY is the exponent.
     */
    public BigDouble(String value) {
        this(BigDouble.parseBigDouble(value));
    }

    private static BigDouble normalize(double mantissa, long exponent) {
        if (mantissa >= 1 && mantissa < 10 || !isFinite(mantissa)) {
            return fromMantissaExponentNoNormalize(mantissa, exponent);
        }
        if (mantissa == 0.0) {
            return ZERO;
        }

        int tempExponent = (int) Math.floor(Math.log10(Math.abs(mantissa)));
        //SAFETY: handle 5e-324, -5e-324 separately
        if (tempExponent == Constants.DOUBLE_EXP_MIN) {
            mantissa = mantissa * 10 / 1e-323;
        } else {
            mantissa = mantissa / PowerOf10.lookup(tempExponent);
        }

        return fromMantissaExponentNoNormalize(mantissa, exponent + tempExponent);
    }

    public static boolean isFinite(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }


    private static BigDouble fromMantissaExponentNoNormalize(double mantissa, long exponent) {
        return new BigDouble(mantissa, exponent, new PrivateConstructorArg());
    }

    /**
     * The singular canonical value representing 0.0.
     */
    public static final BigDouble ZERO = fromMantissaExponentNoNormalize(0, 0);

    /**
     * The BigDouble value for 1.0.
     */
    public static final BigDouble ONE
            = fromMantissaExponentNoNormalize(1, 0);

    /**
     * The BigDouble value representing a numerical error in either parsing or mathematical operations.
     */
    public static final BigDouble NaN
            = fromMantissaExponentNoNormalize(Double.NaN, Long.MIN_VALUE);

    /**
     * Check if a BigDouble is Not a Number (NaN).
     * @param value A BigDouble to check.
     * @return Whether the value is NaN.
     */
    public static boolean isNaN(BigDouble value) {
        return Double.isNaN(value.mantissa);
    }

    /**
     * The singular canonical representation of a number too large to represent using a BigDouble.
     */
    public static final BigDouble POSITIVE_INFINITY
            = fromMantissaExponentNoNormalize(Double.POSITIVE_INFINITY, 0);

    /**
     * Determine if a BigDouble is Positive Infinity.
     * @param value A BigDouble to check.
     * @return Whether the value is Positive Infinity.
     */
    public static boolean isPositiveInfinity(BigDouble value) {
        return Double.isInfinite(value.mantissa) && value.mantissa > 0;
    }

    /**
     * The singular canonical representation of a negative number too small to represent using a BigDouble.
     */
    public static final BigDouble NEGATIVE_INFINITY
            = fromMantissaExponentNoNormalize(Double.NEGATIVE_INFINITY, 0);

    /**
     * Determine if a BigDouble is Negative Infinity.
     * @param value A BigDouble to check.
     * @return Whether the value is Negative Infinity.
     */
    public static boolean isNegativeInfinity(BigDouble value) {
        return Double.isInfinite(value.mantissa) && value.mantissa < 0;
    }

    /**
     * Determine if a BigDouble is Infinite.
     * @param value A BigDouble to check.
     * @return Whether the value is Infinite.
     */
    public static boolean isInfinite(BigDouble value) {
        return Double.isInfinite(value.mantissa);
    }

    /**
     * Determine if a BigDouble is not Infinite.
     * @param value A BigDouble to check.
     * @return Whether the value is not Infinite.
     */
    public static boolean isFinite(BigDouble value) {
        return !isInfinite(value);
    }

    /**
     * Parse a String that is either a valid Number or of the form X.XXeYYY
     * for some values X.XX and YYY.
     * @param value A string to parse into a BigDouble
     * @return A BigDouble equivalent to the value provided.
     * @throws RuntimeException if the string is malformed or invalid.
     */
    public static BigDouble parseBigDouble(String value) {
        if (value.indexOf('e') != -1) {
            var parts = value.split("e" );
            var mantissa = Double.parseDouble(parts[0]);
            var exponent = Long.parseLong(parts[1]);
            return normalize(mantissa, exponent);
        }

        if (value.equals("NaN" )) {
            return NaN;
        }

        BigDouble result = new BigDouble(Double.parseDouble(value));
        if (isNaN(result)) {
            throw new RuntimeException("Invalid argument: " + value);
        }

        return result;
    }

    /**
     * Get this BigDouble's mantissa. A double with absolute value between [1, 10) OR exactly 0.
     * @return The mantissa.
     */
    public double getMantissa() {
        return mantissa;
    }

    /**
     * Get this BigDouble's exponent. A long value.
     * @return the exponent.
     */
    public long getExponent() {
        return exponent;
    }

    /**
     *
     * @return The Mantissa.
     * @see #getMantissa()
     */
    public double m() {
        return mantissa;
    }

    /**
     *
     * @return The Exponent
     * @see #getExponent()
     */
    public double e() {
        return exponent;
    }

    /**
     *
     * @return A positive BigDouble with equivalent magnitude to this BigDouble.
     */
    public BigDouble abs() {
        return fromMantissaExponentNoNormalize(Math.abs(mantissa), exponent);
    }

    /**
     *
     * @param value A value to take the absolute value of.
     * @return A positive BigDouble with equivalent magnitude to this value.
     * @see #abs() Delegates to abs() with proper conversion.
     */
    public static BigDouble abs(BigDouble value) {
         return value.abs();
    }
    /**
     *
     * @param value A value to take the absolute value of.
     * @return A positive BigDouble with equivalent magnitude to this value.
     * @see #abs() Delegates to abs() with proper conversion.
     */
    public static BigDouble abs(double value) {
        return new BigDouble(value).abs();
    }
    /**
     *
     * @param value A value to take the absolute value of.
     * @return A positive BigDouble with equivalent magnitude to this value.
     * @see #abs() Delegates to abs() with proper conversion.
     */
    public static BigDouble abs(String value) {
        return BigDouble.parseBigDouble(value).abs();
    }

    /* TODO: The Original JS version uses a ton of typing shenanigans to avoid needing
     * to declare several methods. We don't have that luxury. I'll ignore it for now,
     * But when it becomes a sufficiently big problem I'll address it.
     */

    /**
     *
     * @return A BigDouble with equivalent magnitude to this value but the opposite sign.
     * value.neg().signum() == -value.signum()
     */
    public BigDouble neg() {
        return fromMantissaExponentNoNormalize(-mantissa, exponent);
    }
    /**
     *
     * @param value A value to negate.
     * @return A negated BigDouble.
     * @see #neg() Delegates to neg() with proper conversion.
     */
    public static BigDouble neg(BigDouble value) {
        return value.neg();
    }
    /**
     *
     * @param value A value to negate.
     * @return A negated BigDouble.
     * @see #neg() Delegates to neg() with proper conversion.
     */
    public static BigDouble neg(double value) {
        return new BigDouble(value).neg();
    }
    /**
     *
     * @param value A value to negate.
     * @return A negated BigDouble.
     * @see #neg() Delegates to neg() with proper conversion.
     */
    public static BigDouble neg(String value) {
        return BigDouble.parseBigDouble(value).neg();
    }
    public BigDouble negate() {
        return neg();
    }
    /**
     * @param value A value to negate.
     * @return A negated BigDouble.
     * @see #neg() Delegates to neg()
     */
    public static BigDouble negate(BigDouble value) {
        return value.neg();
    }
    /**
     * @param value A value to negate.
     * @return A negated BigDouble.
     * @see #neg() Delegates to neg() with proper conversion.
     */
    public static BigDouble negate(double value) {
        return new BigDouble(value).neg();
    }
    /**
     *
     * @param value A value to negate.
     * @return A negated BigDouble.
     * @see #neg() Delegates to neg() with proper conversion.
     */
    public static BigDouble negate(String value) {
        return BigDouble.parseBigDouble(value).neg();
    }
    /**
     * @return A negated BigDouble.
     * @see #neg() Delegates to neg()
     */
    public BigDouble negated() {
        return neg();
    }
    /**
     * @param value A value to negate.
     * @return A negated BigDouble.
     * @see #neg() Delegates to neg() with proper conversion.
     */
    public static BigDouble negated(BigDouble value) {
        return value.neg();
    }
    /**
     * @param value A value to negate.
     * @return A negated BigDouble.
     * @see #neg() Delegates to neg() with proper conversion.
     */
    public static BigDouble negated(double value) {
        return new BigDouble(value).neg();
    }
    /**
     * @param value A value to negate.
     * @return A negated BigDouble.
     * @see #neg() Delegates to neg() with proper conversion.
     */
    public static BigDouble negated(String value) {
        return BigDouble.parseBigDouble(value).neg();
    }


    /**
     * @return the signum function of the BigDouble; zero if the argument is zero,
     * 1.0 if the argument is greater than zero, -1.0 if the argument is less than zero.
     * Special Cases:
     * <ul><li>If the argument is NaN, then the result is NaN.</ul>
     */
    public double signum() {
        return Math.signum(mantissa);
    }
    /**
     * @param value a value to get the sign of.
     * @return the sign of this BigDouble.
     * @see #signum() Delegates to signum() with proper conversion.
     */
    public static double signum(BigDouble value) {
        return value.signum();
    }
    /**
     * @param value a value to get the sign of.
     * @return the sign of this BigDouble.
     * @see #signum() Delegates to signum() with proper conversion.
     */
    public static double signum(double value) {
        return new BigDouble(value).signum();
    }
    /**
     * @param value a value to get the sign of.
     * @return the sign of this BigDouble.
     * @see #signum() Delegates to signum() with proper conversion.
     */
    public static double signum(String value) {
        return BigDouble.parseBigDouble(value).signum();
    }
    /**
     * @return the sign of this BigDouble.
     * @see #signum() Delegates to signum()
     */
    public double sign() {
        return signum();
    }
    /**
     * @param value a value to get the sign of.
     * @return the sign of this BigDouble.
     * @see #signum() Delegates to signum() with proper conversion.
     */
    public static double sign(BigDouble value) {
        return value.signum();
    }
    /**
     * @return the sign of this BigDouble.
     * @see #signum() Delegates to signum() with proper conversion.
     */
    public static double sign(double value) {
        return new BigDouble(value).signum();
    }
    /**
     * @param value a value to get the sign of.
     * @return the sign of this BigDouble.
     * @see #signum() Delegates to signum() with proper conversion.
     */
    public static double sign(String value) {
        return BigDouble.parseBigDouble(value).signum();
    }
    /**
     * @return the sign of this BigDouble.
     * @see #signum() Delegates to signum()
     */
    public double sgn() {
        return signum();
    }
    /**
     * @param value a value to get the sign of.
     * @return the sign of this BigDouble.
     * @see #signum() Delegates to signum() with proper conversion.
     */
    public static double sgn(BigDouble value) {
        return value.signum();
    }
    /**
     * @param value a value to get the sign of.
     * @return the sign of this BigDouble.
     * @see #signum() Delegates to signum() with proper conversion.
     */
    public static double sgn(double value) {
        return new BigDouble(value).signum();
    }
    /**
     * @param value a value to get the sign of.
     * @return the sign of this BigDouble.
     * @see #signum() Delegates to signum() with proper conversion.
     */
    public static double sgn(String value) {
        return BigDouble.parseBigDouble(value).signum();
    }

    /**
     * Returns the closest long to the argument, with ties rounding to positive infinity.
     * Special cases:
     * <ul><li>If the argument is NaN, the result is NaN.
     * <li>If the argument is negative infinity or any value less than or equal to -1 * 10 ^ 17 the result is itself.
     * <li>If the argument is positive infinity or any value greater than or equal to 1 * 10 ^ 17 the result is itself.</ul>
     * @return the value of the BigDouble rounded to the nearest whole number.
     */
    public BigDouble round() {
        if (exponent < -1) {
            return ZERO;
        }
        if (exponent < Constants.MAX_SIGNIFICANT_DIGITS) {
            // Let Math deal with it.
            return new BigDouble(Math.round(toDouble()));
        }
        return this;
    }

    /**
     * @see #round() Delegates to round() with proper conversion.
     */
    public static BigDouble round(BigDouble value) {
        return value.round();
    }
    /**
     * @see #round() Delegates to round() with proper conversion.
     */
    public static BigDouble round(double value) {
        return new BigDouble(value).round();
    }
    /**
     * @see #round() Delegates to round() with proper conversion.
     */
    public static BigDouble round(String value) {
        return BigDouble.parseBigDouble(value).round();
    }

    /**
     * @return the largest (closest to positive infinity)
     * BigDouble value that is less than or equal to the
     * argument and is equal to a mathematical integer. Special cases:
     * <ul><li>If the argument value is already equal to a
     * mathematical integer, then the result is the same as the
     * argument.  <li>If the argument is NaN or an infinity,
     * then the result is the same as the argument.</ul>*/
    public BigDouble floor() {
        if (isInfinite(this)) return this;

        if (exponent < -1) {
            return Math.signum(mantissa) >= 0 ? ZERO : ONE.neg();
        }
        if (exponent < Constants.MAX_SIGNIFICANT_DIGITS) {
            return new BigDouble(Math.floor(toDouble()));
        }
        return this;
    }
    /**
     * @see #floor() Delgates to floor() with proper conversion.
     */
    public static BigDouble floor(BigDouble value) {
        return value.floor();
    }
    /**
     * @see #floor() Delgates to floor() with proper conversion.
     */
    public static BigDouble floor(double value) {
        return new BigDouble(value).floor();
    }
    /**
     * @see #floor() Delgates to floor() with proper conversion.
     */
    public static BigDouble floor(String value) {
        return BigDouble.parseBigDouble(value).floor();
    }

    /**
     * @return the smallest (closest to negative infinity)
     * BigDouble value that is greater than or equal to the
     * argument and is equal to a mathematical integer. Special cases:
     * <ul><li>If the argument value is already equal to a
     * mathematical integer, then the result is the same as the
     * argument.  <li>If the argument is NaN or an infinity,
     * then the result is the same as the argument.
     * <li>If the argument value is less than zero but
     * greater than -1.0, then the result is zero.</ul> Note
     * that the value of {@code x.ceil()} is exactly the
     * value of {@code x.neg().floor().neg()}.
     */
    public BigDouble ceil() {
        if (isInfinite(this)) return this;

        if (exponent < -1) {
            return Math.signum(mantissa) > 0 ? ONE : ZERO;
        }
        if (exponent < Constants.MAX_SIGNIFICANT_DIGITS) {
            return new BigDouble(Math.ceil(toDouble()));
        }
        return this;
    }
    /**
     * @see #ceil() Delgates to ceil() with proper conversion.
     */
    public static BigDouble ceil(BigDouble value) {
        return value.ceil();
    }
    /**
     * @see #ceil() Delgates to ceil() with proper conversion.
     */
    public static BigDouble ceil(double value) {
        return new BigDouble(value).ceil();
    }
    /**
     * @see #ceil() Delgates to ceil() with proper conversion.
     */
    public static BigDouble ceil(String value) {
        return BigDouble.parseBigDouble(value).ceil();
    }

    /**
     * Returns the smallest magnitude (closest to zero)
     * BigDouble value that is less than or equal to the
     * argument and is equal to a mathematical integer. Special cases:
     * <ul><li>If the argument value is already equal to a
     * mathematical integer, then the result is the same as the
     * argument.  <li>If the argument is NaN or an infinity,
     * then the result is the same as the argument.
     * <li>If the argument value is less than 1.0 but
     * greater than -1.0, then the result is zero.</ul>
     */
    public BigDouble trunc() {
        if (exponent < 0) return ZERO;

        if (exponent < Constants.MAX_SIGNIFICANT_DIGITS) {
            // Math.trunc doesn't exist.
            double value = toDouble();
            if (value > 0) return new BigDouble(Math.floor(value));
            return new BigDouble(Math.ceil(value));
        }
        return this;
    }
    /**
     * @see #trunc() Delegates to trunc() with proper conversion.
     */
    public static BigDouble trunc(BigDouble value) {
        return value.trunc();
    }
    /**
     * @see #trunc() Delegates to trunc() with proper conversion.
     */
    public static BigDouble trunc(double value) {
        return new BigDouble(value).trunc();
    }
    /**
     * @see #trunc() Delegates to trunc() with proper conversion.
     */
    public static BigDouble trunc(String value) {
        return BigDouble.parseBigDouble(value).trunc();
    }
    /**
     * @see #trunc() Delegates to trunc()
     */
    public BigDouble truncate() {
        return trunc();
    }
    /**
     * @see #trunc() Delegates to trunc() with proper conversion.
     */
    public static BigDouble truncate(BigDouble value) {
        return value.trunc();
    }
    /**
     * @see #trunc() Delegates to trunc() with proper conversion.
     */
    public static BigDouble truncate(double value) {
        return new BigDouble(value).trunc();
    }
    /**
     * @see #trunc() Delegates to trunc() with proper conversion.
     */
    public static BigDouble truncate(String value) {
        return BigDouble.parseBigDouble(value).trunc();
    }

    /**
     * Adds two numbers together, returning the result as a BigDouble.
     * Note that BigDouble operations are not in-place, and a new BigDouble
     * instance is instantiated as the return value.
     * @param other a value, which may be a number, BigDouble, or valid String.
     * @return the sum of this BigDouble and the other value.
     */
    public BigDouble add(BigDouble other) {
        if (isInfinite(this)) return this;
        if (isInfinite(other)) return other;

        if (this.mantissa == 0) return other;
        if (other.mantissa == 0) return this;

        BigDouble bigger, smaller;

        if (this.exponent >= other.exponent) {
            bigger = this;
            smaller = other;
        } else {
            // Not always true, but in such a case they're close enough that it doesn't matter.
            bigger = other;
            smaller = this;
        }

        if (bigger.exponent - smaller.exponent > Constants.MAX_SIGNIFICANT_DIGITS) {
            return bigger;
        }

        double scalingFactor = PowerOf10.lookup(smaller.exponent - bigger.exponent);
        if (scalingFactor == 0) {
            return bigger;
        }
        // Have to do this because adding numbers that were once integers but scaled down is imprecise.
        // Example: 299 + 18
//            double mantissa = Math.round(
//                    1e14 * bigger.mantissa +
//                            1e14 * smaller.mantissa * scalingFactor
//            );
        return normalize(Math.round(1e14 * bigger.mantissa + 1e14 * smaller.mantissa * scalingFactor),bigger.exponent -14);
    }

    /**
     * @see #add(BigDouble) Delegates to add(BigDouble other) with proper conversion.
     */
    public BigDouble add(double other) {
        return this.add(new BigDouble(other));
    }
    /**
     * @see #add(BigDouble) Delegates to add(BigDouble other) with proper conversion.
     */
    public BigDouble add(String other) {
        return this.add(BigDouble.parseBigDouble(other));
    }
    /**
     * @see #add(BigDouble) Delegates to add(BigDouble other)
     */
    public BigDouble plus(BigDouble other) {
        return add(other);
    }
    /**
     * @see #add(BigDouble) Delegates to add(BigDouble other) with proper conversion.
     */
    public BigDouble plus(double other) {
        return this.plus(new BigDouble(other));
    }
    /**
     * @see #add(BigDouble) Delegates to add(BigDouble other) with proper conversion.
     */
    public BigDouble plus(String other) {
        return this.plus(BigDouble.parseBigDouble(other));
    }

    /**
     * Subtracts the provided value from this BigDouble, returning the result as a BigDouble.
     * Note that BigDouble operations are not in-place, and a new BigDouble
     * instance is instantiated as the return value.
     * @param other a value to subtract, which may be a number, BigDouble, or valid String.
     * @return the difference of this BigDouble and the other value.
     */
    public BigDouble sub(BigDouble other) {
        return add(other.neg());
    }
    /**
     * @see #sub(BigDouble) Delegates to sub(BigDouble other) with proper conversion.
     */
    public BigDouble sub(double other) {
        return this.sub(new BigDouble(other));
    }
    /**
     * @see #sub(BigDouble) Delegates to sub(BigDouble other) with proper conversion.
     */
    public BigDouble sub(String other) {
        return this.sub(BigDouble.parseBigDouble(other));
    }
    /**
     * @see #sub(BigDouble) Delegates to sub(BigDouble other)
     */
    public BigDouble subtract(BigDouble other) {
        return sub(other);
    }
    /**
     * @see #sub(BigDouble) Delegates to sub(BigDouble other) with proper conversion.
     */
    public BigDouble subtract(double other) {
        return this.subtract(new BigDouble(other));
    }
    /**
     * @see #sub(BigDouble) Delegates to sub(BigDouble other) with proper conversion.
     */
    public BigDouble subtract(String other) {
        return this.subtract(BigDouble.parseBigDouble(other));
    }
    /**
     * @see #sub(BigDouble) Delegates to sub(BigDouble other)
     */
    public BigDouble minus(BigDouble other) {
        return sub(other);
    }
    /**
     * @see #sub(BigDouble) Delegates to sub(BigDouble other) with proper conversion.
     */
    public BigDouble minus(double other) {
        return this.minus(new BigDouble(other));
    }
    /**
     * @see #sub(BigDouble) Delegates to sub(BigDouble other) with proper conversion.
     */
    public BigDouble minus(String other) {
        return this.minus(BigDouble.parseBigDouble(other));
    }

    /**
     * Multiply two numbers together, returning the result as a BigDouble.
     * Note that BigDouble operations are not in-place, and a new BigDouble
     * instance is instantiated as the return value.
     * @param other a value to multiply, which may be a number, BigDouble, or valid String.
     * @return the product of this BigDouble and the other value.
     */
    public BigDouble mul(BigDouble other) {
        return normalize(
                this.mantissa * other.mantissa,
                this.exponent + other.exponent
        );
    }
    /**
     * @see #mul(BigDouble) Delegates to mul(BigDouble other) with proper conversion.
     */
    public BigDouble mul(double other) {
        return this.mul(new BigDouble(other));
    }
    /**
     * @see #mul(BigDouble) Delegates to mul(BigDouble other) with proper conversion.
     */
    public BigDouble mul(String other) {
        return this.mul(BigDouble.parseBigDouble(other));
    }
    /**
     * @see #mul(BigDouble) Delegates to mul(BigDouble other).
     */
    public BigDouble multiply(BigDouble other) {
        return mul(other);
    }
    /**
     * @see #mul(BigDouble) Delegates to mul(BigDouble other) with proper conversion.
     */
    public BigDouble multiply(double other) {
        return this.multiply(new BigDouble(other));
    }
    /**
     * @see #mul(BigDouble) Delegates to mul(BigDouble other) with proper conversion.
     */
    public BigDouble multiply(String other) {
        return this.multiply(BigDouble.parseBigDouble(other));
    }
    /**
     * @see #mul(BigDouble) Delegates to mul(BigDouble other)
     */
    public BigDouble times(BigDouble other) {
        return mul(other);
    }
    /**
     * @see #mul(BigDouble) Delegates to mul(BigDouble other) with proper conversion.
     */
    public BigDouble times(double other) {
        return this.times(new BigDouble(other));
    }
    /**
     * @see #mul(BigDouble) Delegates to mul(BigDouble other) with proper conversion.
     */
    public BigDouble times(String other) {
        return this.times(BigDouble.parseBigDouble(other));
    }

    /**
     * Divides this BigDouble by the provided value, returning the result as a BigDouble.
     * Note that BigDouble operations are not in-place, and a new BigDouble
     * instance is instantiated as the return value. <p> Special cases: <ul><li>If the
     * provided value is 0.0, the result will be NaN.</ul>
     * @param other a value to subtract, which may be a number, BigDouble, or valid String.
     * @return the quotient of this BigDouble and the other value.
     */
    public BigDouble div(BigDouble other) {
        return mul(other.recip());
    }
    /**
     * @see #div(BigDouble) Delegates to div(BigDouble other) with proper conversion.
     */
    public BigDouble div(double other) {
        return this.div(new BigDouble(other));
    }
    /**
     * @see #div(BigDouble) Delegates to div(BigDouble other) with proper conversion.
     */
    public BigDouble div(String other) {
        return this.div(BigDouble.parseBigDouble(other));
    }
    /**
     * @see #div(BigDouble) Delegates to div(BigDouble other).
     */
    public BigDouble divide(BigDouble other) {
        return div(other);
    }
    /**
     * @see #div(BigDouble) Delegates to div(BigDouble other) with proper conversion.
     */
    public BigDouble divide(double other) {
        return this.divide(new BigDouble(other));
    }
    /**
     * @see #div(BigDouble) Delegates to div(BigDouble other) with proper conversion.
     */
    public BigDouble divide(String other) {
        return this.divide(BigDouble.parseBigDouble(other));
    }
    // NOTE: If we do add in all the things, divideBy and dividedBy don't get statics.
    /**
     * @see #div(BigDouble) Delegates to div(BigDouble other)
     */
    public BigDouble divideBy(BigDouble other) {
        return div(other);
    }
    /**
     * @see #div(BigDouble) Delegates to div(BigDouble other)
     */
    public BigDouble divideBy(double other) {
        return this.divideBy(new BigDouble(other));
    }
    /**
     * @see #div(BigDouble) Delegates to div(BigDouble other) with proper conversion.
     */
    public BigDouble divideBy(String other) {
        return this.divideBy(BigDouble.parseBigDouble(other));
    }
    /**
     * @see #div(BigDouble) Delegates to div(BigDouble other).
     */
    public BigDouble dividedBy(BigDouble other) {
        return div(other);
    }
    /**
     * @see #div(BigDouble) Delegates to div(BigDouble other) with proper conversion.
     */
    public BigDouble dividedBy(double other) {
        return this.dividedBy(new BigDouble(other));
    }
    /**
     * @see #div(BigDouble) Delegates to div(BigDouble other) with proper conversion.
     */
    public BigDouble dividedBy(String other) {
        return this.dividedBy(BigDouble.parseBigDouble(other));
    }

    /**
     * Returns the reciprocal of this value. <p>Special cases: <ul><li>If the
     * provided value is 0.0, the result will be NaN.<li>If the provided value
     * is Infinite, the result will also be infinite.</ul>
     * @return the sum of this BigDouble and the other value.
     */
    public BigDouble recip() {
        return normalize(1 / mantissa, -exponent);
    }
    /**
     * @see #recip() Delegates to recip() with proper conversion.
     */
    public static BigDouble recip(double value) {
        return new BigDouble(value).recip();
    }
    /**
     * @see #recip() Delegates to recip() with proper conversion.
     */
    public static BigDouble recip(String value) {
        return BigDouble.parseBigDouble(value).recip();
    }
    /**
     * @see #recip() Delegates to recip().
     */
    public BigDouble reciprocal() {
        return recip();
    }
    /**
     * @see #recip() Delegates to recip() with proper conversion.
     */
    public static BigDouble reciprocal(double value) {
        return new BigDouble(value).reciprocal();
    }
    /**
     * @see #recip() Delegates to recip() with proper conversion.
     */
    public static BigDouble reciprocal(String value) {
        return BigDouble.parseBigDouble(value).reciprocal();
    }
    /**
     * @see #recip() Delegates to recip().
     */
    public BigDouble reciprocate() {
        return recip();
    }
    /**
     * @see #recip() Delegates to recip() with proper conversion.
     */
    public static BigDouble reciprocate(double value) {
        return new BigDouble(value).reciprocate();
    }
    /**
     * @see #recip() Delegates to recip() with proper conversion.
     */
    public static BigDouble reciprocate(String value) {
        return BigDouble.parseBigDouble(value).reciprocate();
    }

    @Override
    public int compareTo(@NotNull BigDouble other) {
        if (isNaN(this)) {
            if (isNaN(other)) return 0;
            return -1;
        }
        if (isNaN(other)) return 1;

        if (this.mantissa == 0) {
            if (other.mantissa == 0) return 0;
            if (other.mantissa < 0) return 1;
            return -1;
        }
        if (other.mantissa == 0) {
            if (this.mantissa < 0) return -1;
            return 1;
        }

        if (this.mantissa > 0) {
            if (other.mantissa < 0) return 1;
            if (this.exponent > other.exponent) return 1;
            if (this.exponent < other.exponent) return -1;
            return Double.compare(this.mantissa, other.mantissa);
        }

        if (other.mantissa > 0) return -1;
        if (this.exponent > other.exponent) return -1;
        if (this.exponent < other.exponent) return -1;
        return Double.compare(this.mantissa, other.mantissa);
    }
    public int cmp(BigDouble other) {
        return compareTo(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mantissa, exponent);
    }

    /**
     * Indicates whether some value is "equal to" this one.
     * @param obj An object to check for equivalence.
     * @return Whether the object is equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != BigDouble.class) return false;
        return equals((BigDouble) obj);
    }

    /**
     * Determine if two BigDouble values are exactly equal to each other.
     * Two BigDoubles are equivalent if and only if both their mantissa and exponent are the same.
     * @param other The other value to compare. Can be a String, Double, or BigDouble,
     *              and will be converted appropriately.
     * @return
     */
    public boolean equals(BigDouble other) {
        return this.exponent == other.exponent && this.mantissa == other.mantissa;
    }
    /**
     * @see #equals(BigDouble) Delegates to equals(BigDouble) with proper conversion.
     */
    public boolean equals(double other) {
        return this.equals(new BigDouble(other));
    }
    /**
     * @see #equals(BigDouble) Delegates to equals(BigDouble) with proper conversion.
     */
    public boolean equals(String other) {
        return this.equals(BigDouble.parseBigDouble(other));
    }
    /**
     * @see #equals(BigDouble) Delegates to equals(BigDouble).
     */
    public boolean eq(BigDouble other) {
        return equals(other);
    }
    /**
     * @see #equals(BigDouble) Delegates to equals(BigDouble) with proper conversion.
     */
    public boolean eq(double other) {
        return this.eq(new BigDouble(other));
    }
    /**
     * @see #equals(BigDouble) Delegates to equals(BigDouble) with proper conversion.
     */
    public boolean eq(String other) {
        return this.eq(BigDouble.parseBigDouble(other));
    }


    /**
     * @see #equals(BigDouble) Returns the opposite of equals(BigDouble).
     */
    public boolean neq(BigDouble other) {
        return !equals(other);
    }
    /**
     * @see #neq(BigDouble) Delegates to neq(BigDouble) with proper conversion.
     */
    public boolean neq(double other) {
        return this.neq(new BigDouble(other));
    }
    /**
     * @see #neq(BigDouble) Delegates to neq(BigDouble) with proper conversion.
     */
    public boolean neq(String other) {
        return this.neq(BigDouble.parseBigDouble(other));
    }
    /**
     * @see #neq(BigDouble) Delegates to neq(BigDouble).
     */
    public boolean notEquals(BigDouble other) {
        return !equals(other);
    }
    /**
     * @see #neq(BigDouble) Delegates to neq(BigDouble) with proper conversion.
     */
    public boolean notEquals(double other) {
        return this.notEquals(new BigDouble(other));
    }
    /**
     * @see #neq(BigDouble) Delegates to neq(BigDouble) with proper conversion.
     */
    public boolean notEquals(String other) {
        return this.notEquals(BigDouble.parseBigDouble(other));
    }

    // NOTE: maybe I could get away with the extant CompareTo method doing the work for me.
    /**
     * Determine if this BigDouble is less than the provided value.
     * @param other The other value to compare. Can be a String, Double, or BigDouble,
     *              and will be converted appropriately.
     * @return true if and only if this BigDouble is less than the provided value, false otherwise.
     */
    public boolean lt(BigDouble other) {
        return compareTo(other) < 0;
    }
    /**
     * @see #lt(BigDouble) Delegates to lt(BigDouble) with proper conversion.
     */
    public boolean lt(double other) {
        return this.lt(new BigDouble(other));
    }
    /**
     * @see #lt(BigDouble) Delegates to lt(BigDouble) with proper conversion.
     */
    public boolean lt(String other) {
        return this.lt(BigDouble.parseBigDouble(other));
    }
    /**
     * @see #lt(BigDouble) Delegates to lt(BigDouble).
     */
    public boolean lessThan(BigDouble other) {
        return lt(other);
    }
    /**
     * @see #lt(BigDouble) Delegates to lt(BigDouble) with proper conversion.
     */
    public boolean lessThan(double other) {
        return this.lessThan(new BigDouble(other));
    }
    /**
     * @see #lt(BigDouble) Delegates to lt(BigDouble) with proper conversion.
     */
    public boolean lessThan(String other) {
        return this.lessThan(BigDouble.parseBigDouble(other));
    }

    /**
     * Determine if this BigDouble is less than or equal to the provided value.
     * @param other The other value to compare. Can be a String, Double, or BigDouble,
     *              and will be converted appropriately.
     * @return true if and only if this BigDouble is less than or equal to
     * the provided value, false otherwise.
     */
    public boolean lte(BigDouble other) {
        return compareTo(other) <= 0;
    }
    /**
     * @see #lte(BigDouble) Delegates to lte(BigDouble) with proper conversion.
     */
    public boolean lte(double other) {
        return this.lte(new BigDouble(other));
    }
    /**
     * @see #lte(BigDouble) Delegates to lte(BigDouble) with proper conversion.
     */
    public boolean lte(String other) {
        return this.lte(BigDouble.parseBigDouble(other));
    }
    /**
     * @see #lte(BigDouble) Delegates to lte(BigDouble).
     */
    public boolean lessThanOrEqualTo(BigDouble other) {
        return lte(other);
    }
    /**
     * @see #lte(BigDouble) Delegates to lte(BigDouble) with proper conversion.
     */
    public boolean lessThanOrEqualTo(double other) {
        return this.lessThanOrEqualTo(new BigDouble(other));
    }
    /**
     * @see #lte(BigDouble) Delegates to lte(BigDouble) with proper conversion.
     */
    public boolean lessThanOrEqualTo(String other) {
        return this.lessThanOrEqualTo(BigDouble.parseBigDouble(other));
    }

    /**
     * Determine if this BigDouble is greater than the provided value.
     * @param other The other value to compare. Can be a String, Double, or BigDouble,
     *              and will be converted appropriately.
     * @return true if and only if this BigDouble is greater than the provided value,
     * false otherwise.
     */
    public boolean gt(BigDouble other) {
        return compareTo(other) > 0;
    }
    /**
     * @see #gt(BigDouble) Delegates to gt(BigDouble) with proper conversion.
     */
    public boolean gt(double other) {
        return this.gt(new BigDouble(other));
    }
    /**
     * @see #gt(BigDouble) Delegates to gt(BigDouble) with proper conversion.
     */
    public boolean gt(String other) {
        return this.gt(BigDouble.parseBigDouble(other));
    }
    /**
     * @see #gt(BigDouble) Delegates to gt(BigDouble).
     */
    public boolean greaterThan(BigDouble other) {
        return gt(other);
    }
    /**
     * @see #gt(BigDouble) Delegates to gt(BigDouble) with proper conversion.
     */
    public boolean greaterThan(double other) {
        return this.greaterThan(new BigDouble(other));
    }
    /**
     * @see #gt(BigDouble) Delegates to gt(BigDouble) with proper conversion.
     */
    public boolean greaterThan(String other) {
        return this.greaterThan(BigDouble.parseBigDouble(other));
    }

    /**
     * Determine if this BigDouble is greater than or equal to the provided value.
     * @param other The other value to compare. Can be a String, Double, or BigDouble,
     *              and will be converted appropriately.
     * @return true if and only if this BigDouble is greater than or equal to
     * the provided value, false otherwise.
     */
    public boolean gte(BigDouble other) {
        return compareTo(other) >= 0;
    }
    /**
     * @see #gte(BigDouble) Delegates to gte(BigDouble) with proper conversion.
     */
    public boolean gte(double other) {
        return this.gte(new BigDouble(other));
    }
    /**
     * @see #gte(BigDouble) Delegates to gte(BigDouble) with proper conversion.
     */
    public boolean gte(String other) {
        return this.gte(BigDouble.parseBigDouble(other));
    }
    /**
     * @see #gte(BigDouble) Delegates to gte(BigDouble).
     */
    public boolean greaterThanOrEqualTo(BigDouble other) {
        return gte(other);
    }
    /**
     * @see #gte(BigDouble) Delegates to gte(BigDouble) with proper conversion.
     */
    public boolean greaterThanOrEqualTo(double other) {
        return this.greaterThanOrEqualTo(new BigDouble(other));
    }
    /**
     * @see #gte(BigDouble) Delegates to gte(BigDouble) with proper conversion.
     */
    public boolean greaterThanOrEqualTo(String other) {
        return this.greaterThanOrEqualTo(BigDouble.parseBigDouble(other));
    }

    /**
     * Returns the greater of this BigDouble or the other value.
     * @param other The value to compare this BigDouble to. Can be a String, Double,
     *              or BigDouble, and will be converted appropriately.
     * @return The greater value, as a BigDouble.
     */
    public BigDouble max(BigDouble other) {
        return compareTo(other) > 0 ? this : other;
    }
    /**
     * @see #max(BigDouble) Delegates to max(BigDouble) with proper conversion.
     */
    public BigDouble max(double other) {
        return this.max(new BigDouble(other));
    }
    /**
     * @see #max(BigDouble) Delegates to max(BigDouble) with proper conversion.
     */
    public BigDouble max(String other) {
        return this.max(BigDouble.parseBigDouble(other));
    }

    /**
     * Returns the smallest of this BigDouble or the other value.
     * @param other The value to compare this BigDouble to. Can be a String, Double,
     *              or BigDouble, and will be converted appropriately.
     * @return The smaller value, as a BigDouble.
     */
    public BigDouble min(BigDouble other) {
        return compareTo(other) < 0 ? this : other;
    }
    /**
     * @see #min(BigDouble) Delegates to min(BigDouble) with proper conversion.
     */
    public BigDouble min(double other) {
        return this.min(new BigDouble(other));
    }
    /**
     * @see #min(BigDouble) Delegates to min(BigDouble) with proper conversion.
     */
    public BigDouble min(String other) {
        return this.min(BigDouble.parseBigDouble(other));
    }

    /**
     * Determine if a value is within two bounds. If it is below or above those bounds,
     * return the value of lower or higher, respectively.
     * @param lower The lower bound that this BigDouble may be.
     * @param higher The upper bound that this BigDouble may be.
     * @return This BigDouble value, unless outside the bounds defined by lower and higher.
     */
    public BigDouble clamp(BigDouble lower, BigDouble higher) {
        return max(lower).min(higher);
    }

    /**
     * Clamp a value such that it must be at least "value".
     * @param other A lower bound that this BigDouble should be above.
     * @return This BigDouble, unless less than "other", in which case "other" is returned.
     */
    public BigDouble clampMin(BigDouble other) {
        return max(other);
    }

    /**
     * Clamp a value such that it must be at most "value".
     * @param other An upper bound that this BigDouble should be below.
     * @return This BigDouble, unless greater than "other", in which case "other" is returned.
     */
    public BigDouble clampMax(BigDouble other) {
        return min(other);
    }

    // It's operators like this one that make me realize how much of a pain it will be
    // to properly overload everything later. 9 methods each.


    public int cmp_tolerance(BigDouble other, BigDouble tolerance) {
        return eq_tolerance(other, tolerance) ? 0 : cmp(other);
    }
    /**
     * @see #cmp_tolerance(BigDouble, BigDouble)  Delegates to cmp_tolerance(BigDouble, BigDouble).
     */
    public int compare_tolerance(BigDouble other, BigDouble tolerance) {
        return cmp_tolerance(other, tolerance);
    }

    /**
     * Determine if two values are reasonably close together. If the magnitude of the
     * difference between the two values is less than tolerance, then the values will
     * be treated as equal.
     * @param other The value to compare this BigDouble to.
     * @param tolerance The maximum amount that the values can differ by while being equivalent.
     * @return Whether the values are within tolerance of each other.
     */
    public boolean eq_tolerance(BigDouble other, BigDouble tolerance) {
        return sub(other).abs().lte(
                this.abs().max(other.abs()).mul(tolerance)
        );
    }

    /**
     * @see #eq_tolerance(BigDouble, BigDouble)   Delegates to eq_tolerance(BigDouble, BigDouble).
     */
    public boolean equals_tolerance(BigDouble other, BigDouble tolerance) {
        return eq_tolerance(other, tolerance);
    }

    public boolean neq_tolerance(BigDouble other, BigDouble tolerance) {
        return !eq_tolerance(other, tolerance);
    }
    public boolean notEquals_tolerance(BigDouble other, BigDouble tolerance) {
        return neq_tolerance(other, tolerance);
    }

    public boolean lt_tolerance(BigDouble other, BigDouble tolerance) {
        return !eq_tolerance(other, tolerance) && lt(other);
    }

    public boolean lte_tolerance(BigDouble other, BigDouble tolerance) {
        return eq_tolerance(other, tolerance) || lt(other);
    }

    public boolean gt_tolerance(BigDouble other, BigDouble tolerance) {
        return !eq_tolerance(other, tolerance) && gt(other);
    }

    public boolean gte_tolerance(BigDouble other, BigDouble tolerance) {
        return eq_tolerance(other, tolerance) || gt(other);
    }

    /**
     * Returns the base 10 logarithm of this BigDouble value.
     * Special cases:
     *
     * <ul><li>If the argument is NaN or less than zero, then the result
     * is NaN.
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     * <li>If the argument is positive zero or negative zero, then the
     * result is negative infinity.
     * <li>If the argument is equal to 10<sup><i>n</i></sup> for
     * integer <i>n</i>, then the result is <i>n</i>. In particular,
     * if the argument is {@code 1.0} (10<sup>0</sup>), then the
     * result is positive zero.
     * </ul>
     */
    public double log10() {
        return exponent + Math.log10(mantissa);
    }

    /**
     * @see #log10() Delegates to log10().
     */
    public static double log10(BigDouble value) {
        return value.log10();
    }
    /**
     * @see #log10() Delegates to log10() with proper conversion.
     */
    public static double log10(double value) {
        return new BigDouble(value).log10();
    }
    /**
     * @see #log10() Delegates to log10() with proper conversion.
     */
    public static double log10(String value) {
        return BigDouble.parseBigDouble(value).log10();
    }

    /**
     * Returns the base 10 logarithm of the magnitude of this BigDouble value.
     * Special cases:
     *
     * <ul><li>If the argument is NaN, then the result is NaN.
     * <li>If the argument is infinite, then the result is
     * positive infinity.
     * <li>If the argument is positive zero or negative zero, then the
     * result is negative infinity.
     * </ul>
     */
    public double absLog10() {
        return exponent + Math.log10(Math.abs(mantissa));
    }
    /**
     * @see #absLog10()  Delegates to absLog10().
     */
    public static double absLog10(BigDouble value) {
        return value.absLog10();
    }
    /**
     * @see #absLog10()  Delegates to absLog10() with proper conversion.
     */
    public static double absLog10(double value) {
        return new BigDouble(value).absLog10();
    }
    /**
     * @see #absLog10()  Delegates to absLog10() with proper conversion.
     */
    public static double absLog10(String value) {
        return BigDouble.parseBigDouble(value).absLog10();
    }

    /**
     * Returns the base 10 logarithm of a BigDouble value, clamped to 0.
     * Special cases:
     *
     * <ul><li>If the argument is NaN, then the result
     * is NaN.
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     * <li>If the argument is less than 1.0, the result is 0.0.
     * <li>If the argument is equal to 10<sup><i>n</i></sup> for
     * integer <i>n</i>, then the result is <i>n</i>. In particular,
     * if the argument is {@code 1.0} (10<sup>0</sup>), then the
     * result is positive zero.
     * </ul>
     */
    public double pLog10() {
        return mantissa <= 0 || exponent < 0 ? 0 : log10();
    }
    /**
     * @see #pLog10()  Delegates to pLog10().
     */
    public static double pLog10(BigDouble value) {
        return value.pLog10();
    }
    /**
     * @see #pLog10()  Delegates to pLog10() with proper conversion.
     */
    public static double pLog10(double value) {
        return new BigDouble(value).pLog10();
    }
    /**
     * @see #pLog10()  Delegates to pLog10() with proper conversion.
     */
    public static double pLog10(String value) {
        return BigDouble.parseBigDouble(value).pLog10();
    }

    /**
     * Returns the natural logarithm of this BigDouble value.
     * Special cases:
     *
     * <ul><li>If the argument is NaN or less than zero, then the result
     * is NaN.
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     * <li>If the argument is positive zero or negative zero, then the
     * result is negative infinity.
     * <li>If the argument is equal to 10<sup><i>n</i></sup> for
     * integer <i>n</i>, then the result is <i>n</i>. In particular,
     * if the argument is {@code 1.0} (10<sup>0</sup>), then the
     * result is positive zero.
     * </ul>
     */
    public double log() {
        return 2.302585092994046 * log10();
    }
    /**
     * @see #log()  Delegates to log().
     */
    public double logarithm() {
        return log();
    }
    /**
     * @see #log()  Delegates to log() with proper conversion.
     */
    public static double logarithm(BigDouble value) {
        return value.logarithm();
    }

    /**
     * Returns the logarithm of this BigDouble value, with the given base.
     * Special cases:
     *
     * <ul><li>If the argument is NaN or less than zero, then the result
     * is NaN.
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     * <li>If the argument is positive zero or negative zero, then the
     * result is negative infinity.
     * <li>If the argument is equal to 10<sup><i>n</i></sup> for
     * integer <i>n</i>, then the result is <i>n</i>. In particular,
     * if the argument is {@code 1.0} (10<sup>0</sup>), then the
     * result is positive zero.
     * <li>If the base is less than 1, the result will equal
     * this.recip().log(1 / base).
     * </ul>
     * @param base the base for this logarithm.
     * @return the logarithm base B of this BigDouble.
     */
    public double log(double base) {
        // UN-SAFETY: Most incremental game cases are log(number := 1 or greater, base := 2 or greater).
        // We assume this to be true and thus only need to return a number, not a Decimal,
        // and don't do any other kind of error checking.

        // Also, Math.LN10 = 2.302585092994046
        return 2.302585092994046 / Math.log(base) * log10();
    }
    /**
     * @see #log(double)  Delegates to log(double).
     */
    public double logarithm(double base) {
        return log(base);
    }

    /**
     * Returns the base 10 logarithm of this BigDouble value.
     * Special cases:
     *
     * <ul><li>If the argument is NaN or less than zero, then the result
     * is NaN.
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     * <li>If the argument is positive zero or negative zero, then the
     * result is negative infinity.
     * <li>If the argument is equal to 10<sup><i>n</i></sup> for
     * integer <i>n</i>, then the result is <i>n</i>. In particular,
     * if the argument is {@code 1.0} (10<sup>0</sup>), then the
     * result is positive zero.
     * </ul>
     */
    public double log2() {
        return 3.321928094887362 * log10();
    }

    /**
     * @see #log()  Delegates to log().
     */
    public double ln() {
        return log();
    }
    /**
     * @see #log()  Delegates to log() with proper conversion.
     */
    public static double ln(BigDouble value) {
        return value.ln();
    }
    /**
     * @see #log()  Delegates to log() with proper conversion.
     */
    public static double ln(double value) {
        return new BigDouble(value).ln();
    }
    /**
     * @see #log()  Delegates to log() with proper conversion.
     */
    public static double ln(String value) {
        return BigDouble.parseBigDouble(value).ln();
    }

    /**
     * Create a new BigDouble that is 10 ^ value.
     * Equivalent to new BigDouble(1, value).
     * @param value the value 10 is being raised to.
     * @return A bigDouble of the form 1e+Value.
     */
    public static BigDouble pow10(long value) {
        return fromMantissaExponentNoNormalize(1, value);
    }

    /**
     * Create a new BigDouble value that is 10^value.
     * @param value The value that 10 is being raised to.
     * @return A BigDouble equal to 10^value.
     */
    public static BigDouble pow10(double value) {
        long valueAsLong = (long) value;
        // UN-SAFETY: if value is larger than a long, then the program will break anyway.
        double residual = value - valueAsLong;
        if (Math.abs(residual) < Constants.ROUND_TOLERANCE) {
            return fromMantissaExponentNoNormalize(1, valueAsLong);
        }
        return normalize(Math.pow(10, residual), valueAsLong);
    }
    /**
     * @see #pow10(double)  Delegates to pow10(double) with proper conversion.
     */
    public static BigDouble pow10(BigDouble value) {
        return pow10(value.toDouble());
    }

    /**
     * @see #pow(double)  Delegates to pow(double) with proper conversion.
     */
    public BigDouble pow(BigDouble power) {
        // UN-SAFETY: if power > Double.MAX_VALUE,
        // anything raised to it is either 0 or infinite.

        return pow(power.toDouble());
    }

    /**
     * Raise a BigDouble to the given power.
     * <p>Special cases:
     * <ul><li>If power is negative, and this BigDouble is not an integer,
     * returns NaN.
     * <li>If power is less than zero and not an integer, return NaN.
     * <li>If this BigDouble is negative, and power is not an integer,
     * return NaN.
     * <li>If this BigDouble is negative, and power is an odd integer,
     * the result will be negative.
     * </ul>
     * @param power the value to raise this BigDouble to.
     * @return The result as a BigDouble.
     */
    public BigDouble pow(double power) {
        // GUARD: 0 ^ Anything = 0, except 0.
        if (mantissa == 0) return power == 0 ? ONE : this;

        // GUARD: -XXX ^ 2.5 = NaN
        boolean powerIsInteger =
                Math.abs(power) < 9007199254740991L
                && Math.floor(power) == power;

        if (mantissa < 0 && !powerIsInteger) return NaN;

        // FAIL-FAST: 10 ^ x can be computed quickly.
        boolean is10 = exponent == 1 && mantissa - 1 < Double.MIN_VALUE;
        if (is10) return pow10(power);

        // FAST-TRACK: if (exponent * value) is an int and mantissa ^ value < e308,
        // Then we can do a very fast method.
        double temp = exponent * power;
        double newMantissa;
        if (Math.abs(temp) < 9007199254740991L && Math.floor(temp) == temp) {
            newMantissa = Math.pow(mantissa, power);
            if (Double.isFinite(newMantissa) && newMantissa != 0) {
                return normalize(newMantissa, (long)temp);
            }
        }

        long newExponent = (long) temp;
        double residue = temp - newExponent;
        newMantissa = Math.pow(10, power * Math.log10(mantissa) + residue);
        if (Double.isFinite(newMantissa) && newMantissa != 0) {
            return normalize(newMantissa, newExponent);
        }

        // Dumb math time: pow10(pow * this.log10())
        BigDouble result = BigDouble.pow10(power * this.absLog10());
        if (sign() == -1 && power % 2 == 1) {
            // We did NaN checking at the beginning.
            return result.neg();
        }
        return result;
    }

    /**
     * Returns Euler's number <i>e</i> raised to the power of a
     * {@code double} value.  Special cases:
     * <ul><li>If the argument is NaN, the result is NaN.
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     * <li>If the argument is negative infinity, then the result is
     * positive zero.
     * <li>If the argument is zero, then the result is {@code 1.0}.
     * </ul>
     * @return The value e^a, where a is this BigDouble.
     */
    public BigDouble exp() {
        double x = toDouble();
        if (-706 < x && x < 709) return new BigDouble(Math.exp(x));
        return new BigDouble(Math.E).pow(this);
    }
    /**
     * @see #exp()  Delegates to exp() with proper conversion.
     */
    public static BigDouble exp(BigDouble value) {
        return value.exp();
    }
    /**
     * @see #exp()  Delegates to exp() with proper conversion.
     */
    public static BigDouble exp(double value) {
        return new BigDouble(value).exp();
    }
    /**
     * @see #exp()  Delegates to exp() with proper conversion.
     */
    public static BigDouble exp(String value) {
        return BigDouble.parseBigDouble(value).exp();
    }

    /**
     * Squares this BigDouble.
     * @return the square of this BigDouble.
     */
    public BigDouble sqr() {
        return normalize(mantissa * mantissa, exponent * 2);
    }
    /**
     * @see #sqr()  Delegates to sqr() with proper conversion.
     */
    public static BigDouble sqr(BigDouble value) {
        return value.sqr();
    }
    /**
     * @see #sqr()  Delegates to sqr() with proper conversion.
     */
    public static BigDouble sqr(double value) {
        return new BigDouble(value).sqr();
    }
    /**
     * @see #sqr()  Delegates to sqr() with proper conversion.
     */
    public static BigDouble sqr(String value) {
        return BigDouble.parseBigDouble(value).sqr();
    }

    /**
     * Returns the correctly rounded positive square root of this
     * BigDouble value.
     * <p>Special cases:
     * <ul><li>If the argument is NaN or less than zero, then the result
     * is NaN.
     * <li>If the argument is positive infinity, then the result is positive
     * infinity.
     * <li>If the argument is positive zero or negative zero, then the
     * result is the same as the argument.</ul>
     * Otherwise, the result is a BigDouble approximately equal to
     * the true mathematical square root of this BigDouble's value.
     *
     * @return  the positive square root of this BigDouble.
     *          If the argument is NaN or less than zero, the result is NaN.
     */
    public BigDouble sqrt() {
        if (mantissa < 0) return NaN;
        if (exponent % 2 != 0) {
            // Mod of a negative number is negative, so != could be +1 or -1.
            return normalize(
                    Math.sqrt(mantissa) * 3.16227766016838,
                    (exponent - 1) / 2
            );
        }
        return normalize(Math.sqrt(mantissa), exponent / 2);
    }
    /**
     * @see #sqrt()  Delegates to sqrt() with proper conversion.
     */
    public static BigDouble sqrt(BigDouble value) {
        return value.sqrt();
    }
    /**
     * @see #sqrt()  Delegates to sqrt() with proper conversion.
     */
    public static BigDouble sqrt(double value) {
        return new BigDouble(Math.sqrt(value));
    }
    /**
     * @see #sqrt()  Delegates to sqrt() with proper conversion.
     */
    public static BigDouble sqrt(String value) {
        return BigDouble.parseBigDouble(value).sqrt();
    }

    /**
     * Cubes this BigDouble. The cube will have the same
     * sign as the original.
     * @return the cube of this BigDouble.
     */
    public BigDouble cube() {
        return normalize(
                mantissa * mantissa * mantissa,
                exponent * 3
        );
    }
    /**
     * @see #cube()  Delegates to cube() with proper conversion.
     */
    public static BigDouble cube(BigDouble value) {
        return value.cube();
    }
    /**
     * @see #cube()  Delegates to cube() with proper conversion.
     */
    public static BigDouble cube(double value) {
        return new BigDouble(value).cube();
    }
    /**
     * @see #cube()  Delegates to cube() with proper conversion.
     */
    public static BigDouble cube(String value) {
        return BigDouble.parseBigDouble(value).cube();
    }

    /**
     * Returns the cube root of this BigDouble value.  For
     * positive finite {@code x}, {@code x.neg().cbrt() ==
     * x.cbrt().neg()}; that is, the cube root of a negative value is
     * the negative of the cube root of that value's magnitude.
     *
     * <p>Special cases:
     *
     * <ul>
     *
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is infinite, then the result is an infinity
     * with the same sign as the argument.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     *
     * </ul>
     *
     * @return  the cube root of this BigDouble.
     */
    public BigDouble cbrt() {
        int sign = mantissa > 0 ? 1 : -1;
        double newMantissa = Math.cbrt(mantissa);

        return switch ((int) (exponent % 3)) {
            case 1, -2 -> normalize(
                    newMantissa * 2.154434690031883,
                    (long) Math.floor(exponent / 3.0)
            );
            case 2, -1 -> normalize(
                    newMantissa * 4.641588833612778,
                    (long) Math.floor(exponent / 3.0)
            );
            default -> // 0
                    normalize(newMantissa, exponent / 3);
        };
    }
    /**
     * @see #cbrt()  Delegates to cbrt() with proper conversion.
     */
    public static BigDouble cbrt(BigDouble value) {
        return value.cbrt();
    }
    /**
     * @see #cbrt()  Delegates to cbrt() with proper conversion.
     */
    public static BigDouble cbrt(double value) {
        return new BigDouble(Math.cbrt(value));
    }
    /**
     * @see #cbrt()  Delegates to cbrt() with proper conversion.
     */
    public static BigDouble cbrt(String value) {
        return BigDouble.parseBigDouble(value).cbrt();
    }

    /**
     * If you're willing to spend 'resourcesAvailable' and want to buy something
     * with exponentially increasing cost each purchase (start at priceStart,
     * multiply by priceRatio, already own currentOwned), how much of it can you buy?
     * Adapted from Trimps source code.
     */
    public static BigDouble affordGeometricSeries(
            // Thanks, I hate that this has 4
            BigDouble resourcesAvailable,
            BigDouble priceStart,
            BigDouble priceRatio,
            long currentOwned
    ) {
        BigDouble actualStart = priceStart.mul(priceRatio.pow(currentOwned));

        return new BigDouble(Math.floor(
                resourcesAvailable.div(actualStart).mul(priceRatio.sub(ONE)).add(ONE).log10()
                / priceRatio.log10()
        ));
    }
    public static BigDouble affordGeometricSeries(
            BigDouble resourcesAvailable,
            double priceStart,
            double priceRatio,
            long currentOwned
    ) {
        return affordGeometricSeries(
                resourcesAvailable,
                new BigDouble(priceStart),
                new BigDouble(priceRatio),
                currentOwned
        );
    }

    /**
     * How much resource would it cost to buy (numItems) items if you already have currentOwned,
     * the initial price is priceStart, and it multiplies by priceRatio each purchase?
     */
    public static BigDouble sumGeometricSeries(
            int numItems,
            BigDouble priceStart,
            BigDouble priceRatio,
            int currentOwned
    ) {
        return priceStart
                .mul(priceRatio.pow(currentOwned))
                .mul(ONE.sub(priceRatio.pow(numItems)))
                .div(ONE.sub(priceRatio));
    }

    /**
     * If you're willing to spend 'resourcesAvailable' and want to buy something with additively
     * increasing cost each purchase (start at priceStart, add by priceAdd, already own currentOwned),
     * how much of it can you buy?
     */
    public static BigDouble affordArithmeticSeries(
            BigDouble resourcesAvailable,
            BigDouble priceStart,
            BigDouble priceAdd,
            int currentOwned
    ) {
        BigDouble actualStart = priceStart.add(priceAdd.mul(currentOwned));
        BigDouble b = actualStart.sub(priceAdd.div(2));
        BigDouble b2 = b.pow(2);

        return b.neg()
                .add(b2.add(priceAdd.mul(resourcesAvailable).mul(2)).sqrt())
                .div(priceAdd)
                .floor();
    }

    /**
     * How much resource would it cost to buy (numItems) items if you already have currentOwned,
     * the initial price is priceStart and it adds priceAdd each purchase?
     * Adapted from <a href="http://www.mathwords.com/a/arithmetic_series.htm">...</a>
     */
    public static BigDouble sumArithmeticSeries(
            int numItems,
            BigDouble priceStart,
            BigDouble priceAdd,
            int currentOwned
    ) {
        BigDouble actualStart = priceStart.add(priceAdd.mul(currentOwned));

        // (n/2)*(2*a+(n-1)*d)
        // numItems
        return new BigDouble(numItems)
                .div(2)
                .mul(actualStart.mul(2).plus(new BigDouble(numItems).sub(ONE).mul(priceAdd)));
    }

    /**
     * When comparing two purchases that cost (resource) and increase your resource/sec by (deltaRpS),
     * the lowest efficiency score is the better one to purchase.
     * From Frozen Cookies:
     * <a href="http://cookieclicker.wikia.com/wiki/Frozen_Cookies_(JavaScript_Add-on)#Efficiency.3F_What.27s_that.3F">...</a>
     */
    public static BigDouble efficiencyOfPurchase(
            BigDouble cost, BigDouble currentRpS, BigDouble deltaRpS
    ) {
        return cost.div(currentRpS).add(cost.div(deltaRpS));
    }

    private static BigDouble randomDecimalForTesting(long absMaxExponent) {
        // NOTE: This doesn't follow any kind of sane random distribution, so use this for testing purposes only.
        // 5% of the time, have a mantissa of 0
        if (Math.random() * 20 < 1) {
            return fromMantissaExponentNoNormalize(0, 0);
        }
        double mantissa = Math.random() * 10;
        // 10% of the time, have a simple mantissa
        if (Math.random() * 10 < 1) {
            mantissa = Math.round(mantissa);
        }
        mantissa *= Math.signum(Math.random() * 2 - 1);
        long exponent = (long) Math.floor(Math.random() * absMaxExponent * 2) - absMaxExponent;
        return normalize(mantissa, exponent);

        /*
          Examples:
          randomly test pow:
          var a = Decimal.randomDecimalForTesting(1000);
          var pow = Math.random()*20-10;
          if (Math.random()*2 < 1) { pow = Math.round(pow); }
          var result = Decimal.pow(a, pow);
          ["(" + a.toString() + ")^" + pow.toString(), result.toString()]
          randomly test add:
          var a = Decimal.randomDecimalForTesting(1000);
          var b = Decimal.randomDecimalForTesting(17);
          var c = a.mul(b);
          var result = a.add(c);
          [a.toString() + "+" + c.toString(), result.toString()]
        */
    }

    /**
     * Convert this value to a double.
     * Special cases:
     * <ul><li>If this value's magnitude is too large to convert to
     * a Double, return an infinity with the same sign as the argument.
     * <li>If this value is too small to convert to a double, return
     * 0.0.
     * <li>If this value is within ROUND_TOLERANCE of an integer,
     * return the value rounded be an exact integer.
     * </ul>
     * @return a double equal to this BigDouble's value.
     */
    public double toDouble() {
        // Problem: in JS, new Decimal(116).toNumber() returns 115.99999999999999.
        // TODO: How to fix in general case? It's clear that if toNumber() is
        //  VERY close to an integer, we want exactly the integer.
        //  But it's not clear how to specifically write that.
        //  So I'll just settle with 'exponent >= 0 and difference between rounded
        //  and not rounded < 1e-9' as a quick fix.

        // UN-SAFETY: It still eventually fails.
        // Since there's no way to know for sure we started with an integer,
        // all we can do is decide what tradeoff we want between 'yeah I think
        // this used to be an integer' and 'pfft, who needs THAT many decimal
        // places tracked' by changing ROUND_TOLERANCE.
        // https://github.com/Patashu/break_infinity.js/issues/52
        // Currently starts failing at 800002. Workaround is to do .Round()
        // AFTER toNumber() if you are confident you started with an integer.

        // var result = this.m*Math.pow(10, this.e);

        if (isInfinite(this)) {
            return this.mantissa;
        }

        if (exponent > Constants.DOUBLE_EXP_MAX) {
            return mantissa > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }
        if (exponent < Constants.DOUBLE_EXP_MIN) {
            return 0;
        }
        // SAFETY: again, handle 5e-324, -5e-324 separately
        if (exponent == Constants.DOUBLE_EXP_MIN) {
            return mantissa > 0 ? 5e-324 : -5e-324;
        }

        double result = mantissa * PowerOf10.lookup(exponent);
        if (exponent < 0 || Double.isInfinite(result)) {
            return result;
        }
        double resultRounded = Math.round(result);
        if (Math.abs(resultRounded - result) < Constants.ROUND_TOLERANCE) {
            return resultRounded;
        }
        return result;
    }

    /**
     * Get the current Mantissa, truncated to a specific number of decimal places.
     * Formatting always rounds towards 0.
     * Special cases:
     * <ul><li>If this BigDouble is infinite, NaN, or 0, no formatting is applied.
     * <li>Supplying a places value larger than 17 will simply return the mantissa.
     * </ul>
     * @param places The number of places to represent.
     * @return The mantissa, rounded to the specified number of places.
     */
    public double mantissaWithDecimalPlaces(int places) {
        if (isInfinite(this) || isNaN(this)) {
            return mantissa;
        }

        if (mantissa == 0) {
            return 0;
        }

        // TODO: would simple multiplication, rounding, and division be better?
        // Create a DecimalFormat instance with the desired pattern
        DecimalFormat df = new DecimalFormat("#." + "0".repeat(places));

        // Use the format() method to round and format the double
        String formattedValue = df.format(mantissa);
        return Double.parseDouble(formattedValue);
    }

    @Override
    public String toString() {
        if (isInfinite(this)) return Double.toString(mantissa);
        if (exponent <= -Constants.EXP_LIMIT) return "0";

        if (exponent < 21 && exponent > -7) {
            return Double.toString(toDouble());
        }
        return mantissa + "e" + (exponent >= 0 ? "+" : "") + exponent;
    }

    /**
     * Return a string representation of this BigDecimal, forcefully
     * formatted in scientific notation (X.XXeYYY, for some values X.XX and Y)
     * @param places The number of places in the mantissa.
     * @return
     */
    public String toExponential(int places) {
        if (isInfinite(this)) return Double.toString(mantissa);

        if (mantissa == 0 || exponent < -Constants.EXP_LIMIT) {
            return "0" + RepeatZeroes.trailZeroes(places) + "e+0";
        }

        // One case: we have to do it all ourselves!
        // Sorry, no toExponential in Double.

        int len = places + 1;
        int numDigits = (int) Math.max(1, Math.ceil(Math.log10(Math.abs(mantissa))));
        double rounded = Math.round(mantissa * Math.pow(10, len - numDigits)) * Math.pow(10, numDigits - len);

        // Create a DecimalFormat instance with the desired pattern
        DecimalFormat df = new DecimalFormat("#." + "0".repeat((Math.max(len - numDigits, 0))));

        // TODO: wait, doesn't MantissaWithDecimalPlaces do this already?
        return df.format(rounded) + "e" + (exponent >= 0 ? "+" : "") + exponent;
    }

    /**
     * Return a string representation of this BigDecimal, formatted
     * with a specific number of places after the decimal point.
     * Will pad extra positions with zeroes.
     * @param places the number of places after the decimal point to show.
     * @return A string representation, with digits added or removed to reach
     * the specified number of places.
     */
    private String toFixed(int places) {
        if (places < 0) {
            places = Constants.MAX_SIGNIFICANT_DIGITS;
        }
        if (exponent <= -Constants.EXP_LIMIT || mantissa == 0) {
            return "0" + (
                    places > 0 ?
                    RepeatZeroes.padRight(".", places) :
                    ""
            );
        }

        // two cases:
        // 1) exponent is 17 or greater: just print out mantissa with the appropriate number of zeroes after it
        // 2) exponent is 16 or less: use "basic" toFixed

        if (exponent >= Constants.MAX_SIGNIFICANT_DIGITS)
        {
            // TODO: StringBuilder-optimizable, and frankly just bad in general.
            String out =  Double.toString(mantissa)
                    .replace(".", "");
            out = RepeatZeroes.padRight(out, (int)exponent + 1)
                    + (places > 0 ? RepeatZeroes.padRight(".", places+1) : "");
        }

        long multiplier = (long) Math.pow(10, places);
        double roundedValue = Math.round(this.toDouble() * multiplier) / (double) multiplier;

        // Not malformed. I think.
        return String.format("%." + places + "f", roundedValue);
    }

    /**
     * Return a string representation of this BigDecimal, formatted
     * to have at least a specific number of places afer the decimal
     * point regardless of if it is big enough to be represented in
     * Scientific notation or not.
     * @param places The number of places after the decimal point to use.
     * @return A string representation of this BigDouble with the specified
     * number of decimal places.
     */
    public String toPrecision(int places) {
        if (exponent <= -7) {
            return toExponential(places - 1);
        }
        if (places > exponent) {
            return this.toFixed(places - (int)exponent - 1);
        }
        return this.toExponential(places - 1);
    }

    public BigDouble sinh() {
        return this.exp().sub(this.neg().exp()).div(2);
    }
    public static BigDouble sinh(BigDouble value) {
        return value.sinh();
    }
    public static BigDouble sinh(double value) {
        return new BigDouble(value).sinh();
    }
    public static BigDouble sinh(String value) {
        return BigDouble.parseBigDouble(value).sinh();
    }

    public BigDouble cosh() {
        return this.exp().add(this.neg().exp()).div(2);
    }
    public static BigDouble cosh(BigDouble value) {
        return value.cosh();
    }
    public static BigDouble cosh(double value) {
        return new BigDouble(value).cosh();
    }
    public static BigDouble cosh(String value) {
        return BigDouble.parseBigDouble(value).cosh();
    }

    public BigDouble tanh() {
        return sinh().div(cosh());
    }
    public static BigDouble tanh(BigDouble value) {
        return value.tanh();
    }
    public static BigDouble tanh(double value) {
        return new BigDouble(value).tanh();
    }
    public static BigDouble tanh(String value) {
        return BigDouble.parseBigDouble(value).tanh();
    }

    public double asinh() {
        return ln(this.add(sqr().add(ONE).sqrt()));
    }
    public static double asinh(BigDouble value) {
        return value.asinh();
    }
    public static double asinh(double value) {
        return new BigDouble(value).asinh();
    }
    public static double asinh(String value) {
        return BigDouble.parseBigDouble(value).asinh();
    }

    public double acosh() {
        return add(ONE).div(ONE.sub(this)).ln() / 2;
    }
    public static double acosh(BigDouble value) {
        return value.acosh();
    }
    public static double acosh(double value) {
        return new BigDouble(value).acosh();
    }
    public static double acosh(String value) {
        return BigDouble.parseBigDouble(value).acosh();
    }

    public double atanh() {
        if (this.abs().gte(1)) return Double.NaN;
        return ln(this.add(1).div(ONE.sub(this))) / 2;
    }
    public static double atanh(BigDouble value) {
        return value.atanh();
    }
    public static double atanh(double value) {
        return new BigDouble(value).atanh();
    }
    public static double atanh(String value) {
        return BigDouble.parseBigDouble(value).atanh();
    }

    /**
     * Joke function from Realm Grinder
     * @return the Ascension Penalty for the number of
     * ascensions you have done.
     */
    public BigDouble ascensionPenalty(int ascensions) {
        if (ascensions == 0) {
            return this;
        }
        return this.pow(Math.pow(10, -ascensions));
    }

    /**
     * Joke function from Cookie Clicker. It's 'egg'
     * @return egg.
     */
    public BigDouble egg() {
        return this.add(9);
    }

    private static class PrivateConstructorArg { }

    public static void main(String[] args) {
        System.out.println(new BigDouble(2).pow(-3000));
    }


    public void setValue(BigDouble newValue) {
        this.mantissa = newValue.mantissa;
        this.exponent = newValue.exponent;
    }
}
