package com.pocketcombats.openskill.util;

import com.pocketcombats.openskill.RatingModelConfig;
import com.pocketcombats.openskill.aggregate.DefaultTeamRatingAggregator;
import com.pocketcombats.openskill.data.MatchMakingRating;
import com.pocketcombats.openskill.data.PlayerResult;
import com.pocketcombats.openskill.data.SimplePlayerResult;
import com.pocketcombats.openskill.data.SimpleTeamResult;
import com.pocketcombats.openskill.data.TeamResult;
import com.pocketcombats.openskill.model.AdjustmentFactors;
import com.pocketcombats.openskill.model.RatingModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class ValidationHelper {

    private ValidationHelper() {
    }

    public static List<AdjustmentFactors> calculateAdjustments(
            RatingModel model,
            TeamResult<?>... teamResults
    ) {
        return Arrays.stream(teamResults)
                .map(teamResult -> {
                    List<TeamResult<?>> opponents = Arrays.stream(teamResults)
                            .filter(r -> !r.equals(teamResult))
                            .toList();
                    return model.calculateAdjustmentFactors(teamResult, opponents);
                })
                .toList();
    }

    public static TeamResult<Integer> teamResult(
            AtomicInteger playerIdCounter,
            RatingModelConfig config,
            int teamRank,
            List<MatchMakingRating> teamPlayers
    ) {
        return teamResult(
                playerIdCounter,
                config,
                teamRank,
                teamPlayers,
                Collections.nCopies(teamPlayers.size(), 1.0)
        );
    }

    public static TeamResult<Integer> teamResult(
            AtomicInteger playerIdCounter,
            RatingModelConfig config,
            int teamRank,
            List<MatchMakingRating> teamPlayers,
            List<Double> playerWeights
    ) {
        assert playerWeights.size() == teamPlayers.size();
        MatchMakingRating teamRating = new DefaultTeamRatingAggregator(config).computeTeamRating(teamPlayers);
        List<PlayerResult<Integer>> playerScores = new ArrayList<>(teamPlayers.size());
        for (int i = 0, c = teamPlayers.size(); i < c; i++) {
            var playerRating = teamPlayers.get(i);
            var weight = playerWeights.get(i);
            playerScores.add(
                    new SimplePlayerResult<>(
                            playerIdCounter.getAndIncrement(),
                            playerRating.mu(),
                            playerRating.sigma(),
                            weight
                    )
            );
        }
        return new SimpleTeamResult<>(
                teamRating.mu(),
                teamRating.sigma(),
                teamRank,
                1.0,
                playerScores
        );
    }
}
