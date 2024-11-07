package com.pocketcombats.openskill.model;

import com.pocketcombats.openskill.RatingModelConfig;
import com.pocketcombats.openskill.data.TeamResult;

import java.util.List;

import static com.pocketcombats.openskill.model.ThurstoneMostellerUtil.*;

/**
 * The Thurstone-Mosteller with Full Pairing model assumes a single
 * scalar value to represent player performance and enables rating updates
 * based on match outcomes. Additionally, it employs a maximum likelihood
 * estimation approach to estimate the ratings of players as per the
 * observed outcomes. These assumptions contribute to the model's ability
 * to estimate ratings accurately and provide a well-founded ranking
 * of players.
 */
public class ThurstoneMostellerFull implements RatingModel {

    private final double betaSquared;
    private final double kappa;

    public ThurstoneMostellerFull(RatingModelConfig config) {
        this.betaSquared = config.beta() * config.beta();
        this.kappa = config.kappa();
    }

    @Override
    public AdjustmentFactors calculateAdjustmentFactors(
            TeamResult<?> teamResult,
            List<? extends TeamResult<?>> opponentTeamResults
    ) {
        double teamOmega = 0.0;
        double teamDelta = 0.0;
        double teamSigmaSquared = teamResult.sigma() * teamResult.sigma();

        for (TeamResult<?> opponentResult : opponentTeamResults) {
            // combined standard deviation (σ) measure, incorporating the variances of
            // two competing teams
            double ciq = calculateCiq(teamSigmaSquared, opponentResult.sigma() * opponentResult.sigma());
            // difference in the mean skills of two teams, scaled by the combined uncertainty (ciq)
            double deltaMu = (teamResult.mu() - opponentResult.mu()) / ciq;
            double sigmaSquaredToCiq = teamSigmaSquared / ciq;
            // impact of performance uncertainty on the outcome, closely related
            // to the probability density functions used in Bayesian inference
            double gamma = teamResult.sigma() / ciq;

            if (opponentResult.rank() > teamResult.rank()) {
                teamOmega += sigmaSquaredToCiq * v(deltaMu, kappa / ciq);
                teamDelta += ((gamma * sigmaSquaredToCiq) / ciq) * w(deltaMu, kappa / ciq);
            } else if (opponentResult.rank() < teamResult.rank()) {
                teamOmega += -sigmaSquaredToCiq * v(-deltaMu, kappa / ciq);
                teamDelta += ((gamma * sigmaSquaredToCiq) / ciq) * w(-deltaMu, kappa / ciq);
            } else {
                teamOmega += sigmaSquaredToCiq * vt(deltaMu, kappa / ciq);
                teamDelta += ((gamma * sigmaSquaredToCiq) / ciq) * wt(deltaMu, kappa / ciq);
            }
        }
        return new AdjustmentFactors(teamOmega, teamDelta);
    }

    /**
     * Calculate combined sigma for two teams (ciq).
     *
     * @return The combined sigma for two teams
     */
    private double calculateCiq(double teamASigmaSquared, double teamBSigmaSquared) {
        return Math.sqrt(teamASigmaSquared + teamBSigmaSquared + 2 * betaSquared);
    }
}
