package com.pocketcombats.openskill.data;

import java.io.Serializable;
import java.util.Collection;

public record SimpleTeamResult<T>(
        double mu,
        double sigma,
        int rank,
        double weight,
        Collection<? extends PlayerResult<T>> players
) implements TeamResult<T>, Serializable {

    public SimpleTeamResult(double mu, double sigma, int rank, Collection<? extends PlayerResult<T>> players) {
        this(mu, sigma, rank, 1.0, players);
    }
}
