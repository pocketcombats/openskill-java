package com.pocketcombats.openskill;

import com.pocketcombats.openskill.data.MatchMakingRating;
import com.pocketcombats.openskill.math.Gaussian;

public class QualityEvaluator {

    private final double betaSquared;

    public QualityEvaluator(double beta) {
        this.betaSquared = beta * beta;
    }

    public QualityEvaluator(RatingModelConfig config) {
        this(config.beta());
    }

    public double evaluateQuality(MatchMakingRating teamA, MatchMakingRating teamB) {
        // Shortcut
        if (!isWithinAcceptableRange(teamA, teamB)) {
            return 0;
        }

        return evaluateCompetitiveness(teamA, teamB);
    }

    private static boolean isWithinAcceptableRange(MatchMakingRating teamA, MatchMakingRating teamB) {
        double maxSigma = Math.max(teamA.sigma(), teamB.sigma());
        return Math.abs(teamA.mu() - teamB.mu()) <= 3 * maxSigma;
    }

    private double evaluateCompetitiveness(MatchMakingRating teamA, MatchMakingRating teamB) {
        double deltaMu = teamA.mu() - teamB.mu();
        if (deltaMu < 1E-3) {
            return 1;
        }
        double sigmaCombined = Math.sqrt(teamA.sigma() * teamA.sigma() + teamB.sigma() * teamB.sigma() + betaSquared);
        double z = deltaMu / sigmaCombined;
        double teamAWinChance = Gaussian.cdf(z);
        return 1 - Math.abs(teamAWinChance - 0.5) * 2;
    }
}
