package com.pocketcombats.openskill.model;

import com.pocketcombats.openskill.math.Gaussian;

final class ThurstoneMostellerUtil {

    private ThurstoneMostellerUtil() {
    }

    private static final double EPSILON = 2.220446049250313e-16;

    public static double phiMajor(double x) {
        return Gaussian.cdf(x);
    }

    public static double phiMinor(double x) {
        return Gaussian.pdf(x);
    }

    /**
     * The function `V` (67) is a part of the update rules formulas
     * as defined in <a href="https://jmlr.csail.mit.edu/papers/volume12/weng11a/weng11a.pdf">A Bayesian Approximation Method for Online Ranking paper</a>
     */
    public static double v(double x, double t) {
        double xt = x - t;
        double denom = phiMajor(xt);
        return (denom < EPSILON) ? -xt : phiMinor(xt) / denom;
    }

    /**
     * The function `W` (67) is a part of the update rules formulas
     * as defined in <a href="https://jmlr.csail.mit.edu/papers/volume12/weng11a/weng11a.pdf">A Bayesian Approximation Method for Online Ranking paper</a>
     */
    public static double w(double x, double t) {
        double xt = x - t;
        double denom = phiMajor(xt);
        if (denom < EPSILON) {
            return (x < 0) ? 1.0 : 0.0;
        }

        return v(x, t) * (v(x, t) + xt);
    }

    /**
     * The function `~V` (68) is a part of the update rules formulas
     * as defined in <a href="https://jmlr.csail.mit.edu/papers/volume12/weng11a/weng11a.pdf">A Bayesian Approximation Method for Online Ranking paper</a>
     */
    public static double vt(double x, double t) {
        double xx = Math.abs(x);
        double b = phiMajor(t - xx) - phiMajor(-t - xx);

        if (b < 1e-5) {
            return x < 0 ? -x - t : -x + t;
        }

        double a = phiMinor(-t - xx) - phiMinor(t - xx);

        return ((x < 0) ? -a : a) / b;
    }

    /**
     * The function `~W` (69) is a part of the update rules formulas as defined in
     * <a href="https://jmlr.csail.mit.edu/papers/volume12/weng11a/weng11a.pdf">A Bayesian Approximation Method for Online Ranking paper</a>
     */
    public static double wt(double x, double t) {
        double xx = Math.abs(x);
        double b = phiMajor(t - xx) - phiMajor(-t - xx);

        if (b < EPSILON) {
            return 1.0;
        }
        return ((t - xx) * phiMinor(t - xx) + (t + xx) * phiMinor(-t - xx)) / b + vt(x, t) * vt(x, t);
    }
}
