package com.pocketcombats.openskill;

import com.pocketcombats.openskill.aggregate.TeamRatingAggregator;

import java.io.Serializable;

/**
 * Configuration class for rating models, defining parameters that control
 * the model's behavior and adjustments in player ratings after matches.
 *
 * @param beta       Determines the spread of performance outcomes. A higher beta means outcomes
 *                   are less predictable based on skill differences, while a lower beta increases the weight of skill
 *                   in determining match resultsThe scaling factor for uncertainty in skills (sigma).
 * @param limitSigma Specifies whether the model should limit the sigma
 *                   (uncertainty) value growth for player ratings.
 * @param kappa      Arbitrary small positive real number that is used to
 *                   prevent the variance of the posterior distribution from
 *                   becoming too small or negative.
 * @param tau        Defines the dynamic adjustment of the rating's uncertainty (sigma).
 *                   Higher tau values allow for faster changes in sigma.
 *                   The recommended value is sigma percent.
 * @param balance    Instructs {@link TeamRatingAggregator} to modify its assumptions about users on the tail ends
 *                   of the skill distribution. With balance turned on, the higher the rating a player has,
 *                   it’s assumed it’s a much more monumental achievement. The inverse is true for lower rated players.
 *                   Not all {@link TeamRatingAggregator} implementations may support this setting.
 */
public record RatingModelConfig(
        double beta,
        boolean limitSigma,
        double kappa,
        double tau,
        Balance balance
) implements Serializable {

    public record Balance(
            boolean enable,
            double z,
            double alpha,
            double target
    ) implements Serializable {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private double beta = 25.0 / 6;
        private boolean limitSigma = false;
        private double kappa = 1E-4;
        private double tau = 25.0 / 300;
        private boolean balance = false;
        private double z = 3.0;
        private double alpha = 1.0;
        private double target = 0.0;

        public RatingModelConfig build() {
            return new RatingModelConfig(
                    beta,
                    limitSigma,
                    kappa,
                    tau,
                    new Balance(
                            balance,
                            z, alpha, target
                    )
            );
        }

        public Builder setBeta(double beta) {
            this.beta = beta;
            return this;
        }

        public Builder setLimitSigma(boolean limitSigma) {
            this.limitSigma = limitSigma;
            return this;
        }

        public Builder setKappa(double kappa) {
            assert kappa > 0;
            this.kappa = kappa;
            return this;
        }

        public Builder setTau(double tau) {
            this.tau = tau;
            return this;
        }

        public Builder setBalance(boolean balance) {
            this.balance = balance;
            return this;
        }

        public Builder setZ(double z) {
            this.z = z;
            return this;
        }

        public Builder setAlpha(double alpha) {
            this.alpha = alpha;
            return this;
        }

        public Builder setTarget(double target) {
            this.target = target;
            return this;
        }
    }
}
