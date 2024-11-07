package com.pocketcombats.openskill.aggregate;

import com.pocketcombats.openskill.RatingModelConfig;
import com.pocketcombats.openskill.data.MatchMakingRating;
import com.pocketcombats.openskill.data.SimpleMatchMakingRating;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultTeamRatingAggregator implements TeamRatingAggregator {

    private final double kappa;
    private final double tauSquared;
    private final boolean balance;
    private final double alpha;
    private final double z;
    private final double target;

    public DefaultTeamRatingAggregator(RatingModelConfig config) {
        this.kappa = config.kappa();
        this.tauSquared = config.tau() * config.tau();
        this.balance = config.balance().enable();
        this.alpha = config.balance().alpha();
        this.z = config.balance().z();
        this.target = config.balance().target();
    }

    @Override
    public MatchMakingRating computeTeamRating(Collection<? extends MatchMakingRating> playerRatings) {
        if (balance) {
            return computeBalanced(playerRatings);
        } else {
            return new SimpleMatchMakingRating(
                    playerRatings.stream().mapToDouble(MatchMakingRating::mu).sum(),
                    Math.sqrt(playerRatings.stream().mapToDouble(this::adjustedSigmaSquared).sum())
            );
        }
    }

    private MatchMakingRating computeBalanced(Collection<? extends MatchMakingRating> playerRatings) {
        Map<MatchMakingRating, Double> playerOrdinals = playerRatings.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        this::ordinal,
                        (o1, o2) -> o1,
                        IdentityHashMap::new
                ));
        double maxOrdinal = playerOrdinals.values().stream()
                .max(Double::compareTo)
                .orElseThrow();
        double teamMu = playerRatings.stream()
                .mapToDouble(playerRating -> {
                    // Calculate balance weight based on ordinal difference
                    double ordinalDiff = maxOrdinal - playerOrdinals.get(playerRating);
                    double balanceWeight = 1.0 + (ordinalDiff / (maxOrdinal + kappa));
                    return playerRating.mu() * balanceWeight;
                }).sum();
        double teamSigma = Math.sqrt(
                playerRatings.stream()
                        .mapToDouble(playerRating -> {
                            // Calculate balance weight based on ordinal difference
                            double ordinalDiff = maxOrdinal - playerOrdinals.get(playerRating);
                            double balanceWeight = 1.0 + (ordinalDiff / (maxOrdinal + kappa));
                            return adjustedSigmaSquared(playerRating) * balanceWeight;
                        }).sum()
        );
        return new SimpleMatchMakingRating(teamMu, teamSigma);
    }

    protected double adjustedSigmaSquared(MatchMakingRating playerRating) {
        return (playerRating.sigma() * playerRating.sigma()) + tauSquared;
    }

    private double ordinal(MatchMakingRating playerRating) {
        return alpha * (playerRating.mu() - z * playerRating.sigma()) + (target / alpha);
    }
}
