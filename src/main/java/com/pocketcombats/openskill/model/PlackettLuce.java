package com.pocketcombats.openskill.model;

import com.pocketcombats.openskill.RatingModelConfig;
import com.pocketcombats.openskill.data.TeamResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlackettLuce implements RatingModel {

    private final double betaSquared;

    public PlackettLuce(double beta) {
        this.betaSquared = beta * beta;
    }

    public PlackettLuce(RatingModelConfig config) {
        this(config.beta());
    }

    @Override
    public AdjustmentFactors calculateAdjustmentFactors(
            TeamResult<?> teamResult,
            List<? extends TeamResult<?>> opponentTeamResults
    ) {
        List<TeamResult<?>> allTeamResults = new ArrayList<>(opponentTeamResults);
        allTeamResults.add(teamResult);
        double omega = 0.0;
        double delta = 0.0;
        double teamSigmaSquared = teamResult.sigma() * teamResult.sigma();
        // A combined variance measure that incorporates the variances of all teams,
        // used for normalizing skill differences
        double c = calculateC(allTeamResults);
        // Represents the sum of exponential skill ratings for teams, used to normalize
        // the probability of ranking orders
        Map<? extends TeamResult<?>, Double> sumQMap = calculateSumQ(allTeamResults, c);
        // Represents a normalization factor used in this model, similar to the calculation of sumQ, to adjust for
        // the number of competing teams and their ranks
        Map<Integer, Integer> aMap = calculateA(allTeamResults);
        double muOverC = Math.exp(teamResult.mu() / c);
        for (var allTeamResult : allTeamResults) {
            double muOverCOverSumQ = muOverC / sumQMap.get(allTeamResult);

            int a = aMap.get(allTeamResult.rank());
            if (teamResult.rank() >= allTeamResult.rank()) {
                delta += muOverCOverSumQ * (1 - muOverCOverSumQ) / a;
                if (teamResult.equals(allTeamResult)) {
                    omega += (1 - muOverCOverSumQ) / a;
                } else {
                    omega -= muOverCOverSumQ / a;
                }
            }
        }
        omega *= teamSigmaSquared / c;
        delta *= teamSigmaSquared / (c * c);

        double gamma = teamResult.sigma() / c;
        delta *= gamma;
        return new AdjustmentFactors(omega, delta);
    }

    /**
     * Calculate the square root of the collective team sigma.
     */
    private double calculateC(List<? extends TeamResult<?>> teamResults) {
        double sum = teamResults.stream()
                .mapToDouble(result -> result.sigma() * result.sigma() + betaSquared)
                .sum();
        return Math.sqrt(sum);
    }


    private static Map<? extends TeamResult<?>, Double> calculateSumQ(
            Iterable<? extends TeamResult<?>> teamResults,
            double c
    ) {
        Map<TeamResult<?>, Double> sumQ = new HashMap<>();

        for (var teamI : teamResults) {
            double summed = Math.exp(teamI.mu() / c);

            for (TeamResult<?> teamQResult : teamResults) {
                if (teamI.rank() >= teamQResult.rank()) {
                    sumQ.merge(teamQResult, summed, Double::sum);
                }
            }
        }

        return sumQ;
    }

    private static Map<Integer, Integer> calculateA(Collection<? extends TeamResult<?>> teamResults) {
        Map<Integer, Integer> aMap = new HashMap<>();
        for (var teamResult : teamResults) {
            aMap.compute(teamResult.rank(), (k, v) -> v == null ? 1 : v + 1);
        }
        return aMap;
    }
}
