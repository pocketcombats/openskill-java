package com.pocketcombats.openskill.data;

public interface PlayerResult<T> extends MatchMakingRating {

    T id();

    /**
     * Player contribution to the match result.
     * Should be greater than 0. You can use 0.1 for players who didn't influence the match
     * outcome and 1.0 as neutral, or balanced, impact.
     */
    double weight();
}
