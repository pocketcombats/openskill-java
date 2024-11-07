package com.pocketcombats.openskill.model;

import com.pocketcombats.openskill.data.TeamResult;

import java.util.List;

public interface RatingModel {

    AdjustmentFactors calculateAdjustmentFactors(
            TeamResult<?> teamResult,
            List<? extends TeamResult<?>> opponentTeamResults
    );
}
