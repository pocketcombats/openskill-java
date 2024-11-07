package com.pocketcombats.openskill.aggregate;

import com.pocketcombats.openskill.data.MatchMakingRating;

import java.util.Collection;

public interface TeamRatingAggregator {

    /**
     * Computes the aggregated mu and sigma for the entire team based on individual player ratings.
     *
     * @param playerRatings a list of {@link MatchMakingRating} representing the players on the team
     * @return the computed team rating
     */
    MatchMakingRating computeTeamRating(Collection<? extends MatchMakingRating> playerRatings);
}
