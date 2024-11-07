package com.pocketcombats.openskill;

import com.pocketcombats.openskill.data.SimpleMatchMakingRating;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QualityEvaluatorTest {

    private final double beta = 25 / 6.0;

    @Test
    public void testAbsoluteFairness() {
        double quality = new QualityEvaluator(beta).evaluateQuality(
                new SimpleMatchMakingRating(51, 25 / 3.0),
                new SimpleMatchMakingRating(51, 25 / 3.0)
        );
        assertThat(quality).isGreaterThan(0.9);
    }

    @Test
    public void testBalanced() {
        double quality = new QualityEvaluator(beta).evaluateQuality(
                new SimpleMatchMakingRating(51, 25 / 4.0),
                new SimpleMatchMakingRating(55, 25 / 3.0)
        );
        assertThat(quality).isGreaterThan(0.7);
    }

    @Test
    public void testUnbalanced() {
        double quality = new QualityEvaluator(beta).evaluateQuality(
                new SimpleMatchMakingRating(52, 25 / 4.0),
                new SimpleMatchMakingRating(25, 25 / 3.0)
        );
        assertThat(quality).isLessThan(0.1);
    }
}
