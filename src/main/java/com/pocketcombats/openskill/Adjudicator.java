package com.pocketcombats.openskill;

import com.pocketcombats.openskill.data.PlayerResult;
import com.pocketcombats.openskill.data.RatingAdjustment;
import com.pocketcombats.openskill.data.TeamResult;
import com.pocketcombats.openskill.model.AdjustmentFactors;
import com.pocketcombats.openskill.model.RatingModel;

import java.util.List;

public class Adjudicator<T> {

    private final RatingModel ratingModel;

    private final double tauSquared;
    private final double kappa;
    private final boolean limitSigma;

    public Adjudicator(
            RatingModelConfig config,
            RatingModel ratingModel
    ) {
        this.ratingModel = ratingModel;

        this.tauSquared = config.tau() * config.tau();
        this.kappa = config.kappa();
        this.limitSigma = config.limitSigma();
    }

    public List<RatingAdjustment<T>> rate(TeamResult<T> teamResult, List<TeamResult<T>> opponents) {
        AdjustmentFactors adjustmentFactors =
                ratingModel.calculateAdjustmentFactors(teamResult, opponents);
        return teamResult.players().stream()
                .map(playerResult ->
                        calculatePlayerRatingAdjustment(teamResult, adjustmentFactors, playerResult)
                )
                .toList();
    }

    public List<RatingAdjustment<T>> rate(List<TeamResult<T>> teamResults) {
        return teamResults.stream()
                .flatMap(teamResult -> {
                    List<TeamResult<T>> opponents = teamResults.stream()
                            .filter(r -> !r.equals(teamResult))
                            .toList();
                    AdjustmentFactors adjustmentFactors =
                            ratingModel.calculateAdjustmentFactors(teamResult, opponents);
                    return teamResult.players().stream()
                            .map(playerResult ->
                                    calculatePlayerRatingAdjustment(teamResult, adjustmentFactors, playerResult)
                            );
                })
                .toList();
    }

    private RatingAdjustment<T> calculatePlayerRatingAdjustment(
            TeamResult<T> teamResult,
            AdjustmentFactors adjustmentFactors,
            PlayerResult<T> playerResult
    ) {
        double mu = playerResult.mu();
        double sigma;
        double teamSigmaSquared = teamResult.sigma() * teamResult.sigma();
        // Calculate adjusted sigma including tau
        double adjustedSigmaSquared = (playerResult.sigma() * playerResult.sigma()) + tauSquared;
        double adjustedSigma = Math.sqrt(adjustedSigmaSquared);

        double omega = adjustmentFactors.omega();
        double delta = adjustmentFactors.delta();

        double weight = omega > 0
                ? playerResult.weight()
                : 1 / playerResult.weight();
        mu += (adjustedSigmaSquared / teamSigmaSquared) * omega * weight;
        sigma = adjustedSigma * Math.sqrt(
                Math.max(
                        1 - (adjustedSigmaSquared / teamSigmaSquared)
                                * delta * weight,
                        kappa
                )
        );

        if (limitSigma) {
            // Ensure player's sigma is not greater than the original sigma
            sigma = Math.min(sigma, playerResult.sigma());
        }
        return new RatingAdjustment<>(playerResult.id(), mu, sigma);
    }
}
