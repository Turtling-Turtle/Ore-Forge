package ore.forge;

public class BigNumber {
    private double value;
    private int exponent;

    public BigNumber(double value, int exponent) {
        this.value = value;
        this.exponent = exponent;
    }

    public BigNumber(double value) {
        this.value = value;
        this.exponent = 1;
    }

    public BigNumber add(BigNumber other) {

        return null;
    }

    public BigNumber subtract(BigNumber other) {


        return null;
    }

    public BigNumber multiply(BigNumber other) {

        return null;
    }

    public BigNumber divide(BigNumber other) {

        return null;
    }

    public BigNumber pow(int exponent) {

        return null;
    }

    public BigNumber modulo(BigNumber other) {

        return null;
    }

    public boolean isGreaterThan(BigNumber other) {
        return false;
    }


    public boolean isLessThan(BigNumber other) {

        return false;
    }


    public boolean isEqual(BigNumber other) {

        return false;
    }


    public int getExponent() {
        return exponent;
    }

    public String toString() {
        return value + "E" + exponent;
    }

}
