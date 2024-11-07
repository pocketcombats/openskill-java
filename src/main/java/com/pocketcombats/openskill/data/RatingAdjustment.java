package com.pocketcombats.openskill.data;

public record RatingAdjustment<T>(
        T playerId,
        double mu,
        double sigma
) {
}
