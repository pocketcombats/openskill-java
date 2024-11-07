package com.pocketcombats.openskill.math;

public final class Gaussian {

    private static final double SQRT2 = Math.sqrt(2);
    private static final double TAU_SQRT = Math.sqrt(2.0 * Math.PI);

    private Gaussian() {
    }

    // Probability density function
    public static double pdf(double x) {
        return Math.exp(-x * x / 2.0) / TAU_SQRT;
    }

    // Cumulative density function
    public static double cdf(double x) {
        var mu = 0.0;
        var sigma = 1.0;

        return 0.5 * (1.0 + erf((x - mu) / (sigma * SQRT2)));
    }

    /**
     * Approximation of the error function (erf).
     *
     * @see <a href="https://github.com/dougthor42/PyErf">erf python implementation</a>
     */
    private static double erf(double x) {
        if (x == 0) {
            return 0;
        }
        if (x >= MAX_VAL) {
            return 1;
        }
        if (x <= -MAX_VAL) {
            return -1;
        }

        if (Math.abs(x) > 1) {
            return 1 - erfc(x);
        }

        double z = x * x;
        return x * polevl(z, T, 4) / p1evl(z, U, 5);
    }

    // Constants T and U from the original function
    private static final double[] T = {
            9.60497373987051638749E0,
            9.00260197203842689217E1,
            2.23200534594684319226E3,
            7.00332514112805075473E3,
            5.55923013010394962768E4
    };

    private static final double[] U = {
            3.35617141647503099647E1,
            5.21357949780152679795E2,
            4.59432382970980127987E3,
            2.26290000613890934246E4,
            4.92673942608635921086E4
    };

    private static final double MAX_VAL = 6.0;

    private static double polevl(double x, double[] coefs, int N) {
        double ans = 0;
        int power = coefs.length - 1;  // Start from the highest degree
        for (double coef : coefs) {
            ans += coef * Math.pow(x, power);
            power--;
        }
        return ans;
    }

    private static double p1evl(double x, double[] coefs, int N) {
        // Add 1 to the beginning of the coefficients array and call `polevl`
        double[] newCoefs = new double[coefs.length + 1];
        newCoefs[0] = 1;
        System.arraycopy(coefs, 0, newCoefs, 1, coefs.length);
        return polevl(x, newCoefs, N);
    }

    // Constants for erfc approximation
    private static final double[] P = {
            2.46196981473530512524E-10,
            5.64189564831068821977E-1,
            7.46321056442269912687E0,
            4.86371970985681366614E1,
            1.96520832956077098242E2,
            5.26445194995477358631E2,
            9.34528527171957607540E2,
            1.02755188689515710272E3,
            5.57535335369399327526E2
    };

    private static final double[] Q = {
            1.32281951154744992508E1,
            8.67072140885989742329E1,
            3.54937778887819891062E2,
            9.75708501743205489753E2,
            1.82390916687909736289E3,
            2.24633760818710981792E3,
            1.65666309194161350182E3,
            5.57535340817727675546E2
    };

    private static final double[] R = {
            5.64189583547755073984E-1,
            1.27536670759978104416E0,
            5.01905042251180477414E0,
            6.16021097993053585195E0,
            7.40974269950448939160E0,
            2.97886665372100240670E0
    };

    private static final double[] S = {
            2.26052863220117276590E0,
            9.39603524938001434673E0,
            1.20489539808096656605E1,
            1.70814450747565897222E1,
            9.60896809063285878198E0,
            3.36907645100081516050E0
    };

    private static double erfc(double a) {
        // Shortcut special cases
        if (a == 0) {
            return 1;
        }
        if (a >= MAX_VAL) {
            return 0;
        }
        if (a <= -MAX_VAL) {
            return 2;
        }

        double x = Math.abs(a);  // Use absolute value of `a`
        double z = -a * a;
        z = Math.exp(z);

        double p, q;
        if (x < 8) {
            p = polevl(x, P, 8);
            q = p1evl(x, Q, 8);
        } else {
            p = polevl(x, R, 5);
            q = p1evl(x, S, 6);
        }

        double y = (z * p) / q;

        if (a < 0) {
            y = 2 - y;
        }

        return y;
    }
}
