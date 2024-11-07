package com.pocketcombats.openskill.aggregate;

import com.pocketcombats.openskill.data.MatchMakingRating;
import com.pocketcombats.openskill.data.SimpleMatchMakingRating;

import java.util.Collection;

/**
 * Uses weighted average for mu calculation and harmonic mean for sigma.
 */
public class WeightedTeamRatingAggregator implements TeamRatingAggregator {

    @Override
    public MatchMakingRating computeTeamRating(Collection<? extends MatchMakingRating> playerRatings) {
        return new SimpleMatchMakingRating(computeTeamMu(playerRatings), computeTeamSigma(playerRatings));
    }

    /**
     * Calculates the weighted average mu for a team, where each character's mu contributes based on their
     * certainty (inverse of their sigma squared). This method gives characters with lower sigma (higher certainty)
     * a greater influence on the team’s overall skill level.
     */
    private static double computeTeamMu(Collection<? extends MatchMakingRating> ratings) {
        double weightedMuSum = 0.0;
        double weightSum = 0.0;

        for (var rating : ratings) {
            // Weight inversely proportional to sigma squared
            double weight = 1 / (rating.sigma() * rating.sigma());
            weightedMuSum += rating.mu() * weight;
            weightSum += weight;
        }

        return weightedMuSum / weightSum;
    }

    /**
     * Calculates the effective sigma for a team, considering the uncertainty (sigma) of each character.
     * This method combines characters’ sigmas in such a way that higher individual uncertainties increase
     * the team’s overall sigma, reflecting greater unpredictability.
     */
    private static double computeTeamSigma(Collection<? extends MatchMakingRating> ratings) {
        double weightSum = 0.0;

        // Calculate the sum of inverse variances for each player's sigma
        for (var rating : ratings) {
            weightSum += 1 / (rating.sigma() * rating.sigma());
        }

        return Math.sqrt(1 / weightSum);
    }
}
