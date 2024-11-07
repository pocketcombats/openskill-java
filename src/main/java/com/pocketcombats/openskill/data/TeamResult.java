package com.pocketcombats.openskill.data;

import java.util.Collection;

public interface TeamResult<T> extends MatchMakingRating {

    /**
     * Team rank, 1 for the first place, 2 for the second place etc.
     */
    int rank();

    /**
     * Weight of the overall team result. Can be, for example, victory confidence.
     */
    double weight();

    Collection<? extends PlayerResult<T>> players();
}
