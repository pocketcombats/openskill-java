package com.pocketcombats.openskill.model;

/**
 * Adjustment factors used in rating updates for players or teams after a match.
 *
 * @param omega the mean adjustment for a player's skill (mu) based on the match outcome
 * @param delta the variance adjustment for a player's skill (sigma) based on the match outcome
 */
public record AdjustmentFactors(
        double omega,
        double delta
) {
}
