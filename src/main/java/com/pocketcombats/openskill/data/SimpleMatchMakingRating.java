package com.pocketcombats.openskill.data;

import java.io.Serializable;

public record SimpleMatchMakingRating(
        double mu,
        double sigma
) implements Serializable, MatchMakingRating {
}
