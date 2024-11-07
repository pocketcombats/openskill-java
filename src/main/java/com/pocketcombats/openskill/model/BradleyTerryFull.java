package com.pocketcombats.openskill.model;

import com.pocketcombats.openskill.RatingModelConfig;
import com.pocketcombats.openskill.data.TeamResult;

import java.util.List;

/**
 * <a href="https://real-statistics.com/reliability/bradley-terry-model/">Bradley–Terry Model</a> implementation
 */
public class BradleyTerryFull implements RatingModel {

    private final double betaSquared;

    public BradleyTerryFull(double beta) {
        this.betaSquared = beta * beta;
    }

    public BradleyTerryFull(RatingModelConfig config) {
        this(config.beta());
    }

    @Override
    public AdjustmentFactors calculateAdjustmentFactors(TeamResult<?> teamResult, List<? extends TeamResult<?>> opponentTeamResults) {
        double teamOmega = 0.0;
        double teamDelta = 0.0;
        double teamSigmaSquared = teamResult.sigma() * teamResult.sigma();

        for (TeamResult<?> opponentResult : opponentTeamResults) {
            double opponentTeamSigmaSquared = opponentResult.sigma() * opponentResult.sigma();
            // This variable represents a combined standard deviation (σ) measure, incorporating the variances of
            // two competing teams, crucial for determining the likelihood of one team winning over another.
            double cIq = Math.sqrt(teamSigmaSquared + opponentTeamSigmaSquared + 2 * betaSquared);
            // The probability that team beats opponent, derived from the logistic function applied to the
            // difference in team skills
            double piq = 1 / (1 + Math.exp((opponentResult.mu() - teamResult.mu()) / cIq));
            // A component used in adjusting omega and delta, representing the ratio of a team's variance to
            // the combined variance (cIq)
            double sigmaSquaredToCiq = teamSigmaSquared / cIq;

            double s = 0.0;
            if (opponentResult.rank() > teamResult.rank()) {
                s = 1.0;
            } else if (opponentResult.rank() == teamResult.rank()) {
                s = 0.5;
            }

            teamOmega += sigmaSquaredToCiq * (s - piq);
            double gammaValue = teamResult.sigma() / cIq;
            teamDelta += ((gammaValue * sigmaSquaredToCiq) / cIq) * piq * (1 - piq);
        }
        return new AdjustmentFactors(teamOmega, teamDelta);
    }
}
