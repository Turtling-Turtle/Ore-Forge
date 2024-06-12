package ore.forge.BreakInfinity;

import com.badlogic.gdx.math.MathUtils;

/**
 * We need this lookup table because Math.pow(10, exponent)
 * when exponent's absolute value is large is slightly inaccurate.
 * You can fix it with the power of math... or just make a lookup table.
 * Faster AND simpler!
 */
public class PowerOf10 {

    /**
     * Instantiate all the powers of 10. Only to be run at the beginning of the program.
     * @return an array containing every representable power of 10, from 1e-324 to 1e308.
     */
    private static double[] cache() {
        double[] out = new double[Constants.DOUBLE_EXP_MAX - Constants.DOUBLE_EXP_MIN];
        for (int i = 0; i < out.length; i++) {
            out[i] = Double.parseDouble("1e" + (i - indexOf0InPowersOf10));
        }
        return out;
    }
    private static final double[] powersOf10 = cache();

    private static final int indexOf0InPowersOf10 = 323;

    public static double lookup(long power) {
        // If power is too big to be an int, something has gone horribly wrong anyways.
        if (power < Constants.DOUBLE_EXP_MIN) {
            return 0;
        } else if (power > Constants.DOUBLE_EXP_MAX) {
            return Double.POSITIVE_INFINITY;
        }

        return powersOf10[(int)power + indexOf0InPowersOf10];
    }

    public static void main(String[] args) {
        System.out.println(lookup(308));
        System.out.println(lookup(-323));
        System.out.println(lookup(0));
    }
}
