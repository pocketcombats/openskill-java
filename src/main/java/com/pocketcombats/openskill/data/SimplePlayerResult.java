package com.pocketcombats.openskill.data;

import java.io.Serializable;

public record SimplePlayerResult<T>(
        T id,
        double mu,
        double sigma,
        double weight
) implements PlayerResult<T>, Serializable {

    public SimplePlayerResult(T id, double mu, double sigma) {
        this(id, mu, sigma, 1.0);
    }
}
