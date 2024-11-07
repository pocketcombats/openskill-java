package com.pocketcombats.openskill.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocketcombats.openskill.RatingModelConfig;
import com.pocketcombats.openskill.data.MatchMakingRating;
import com.pocketcombats.openskill.data.SimpleMatchMakingRating;
import com.pocketcombats.openskill.data.TeamResult;
import com.pocketcombats.openskill.util.ValidationHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ThurstoneMostellerFullTest {

    private final AtomicInteger playerId = new AtomicInteger(1);
    private JsonNode data;
    private double mu;
    private double sigma;

    @BeforeAll
    public void loadTestData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream("data/thurstonemostellerfull.json")) {
            data = objectMapper.readTree(is);
        }
        mu = data.get("model").get("mu").asDouble();
        sigma = data.get("model").get("sigma").asDouble();
    }

    @Test
    public void testNormalResults() {
        RatingModelConfig config = RatingModelConfig.builder().build();
        TeamResult<Integer> teamA = teamResult(config, 1, 1);
        TeamResult<Integer> teamB = teamResult(config, 2, 2);
        RatingModel model = new ThurstoneMostellerFull(config);
        List<AdjustmentFactors> adjustmentFactors = ValidationHelper.calculateAdjustments(model, teamA, teamB);
        assertThat(adjustmentFactors).isEqualTo(List.of(
                new AdjustmentFactors(7.1427027479494996, 0.12197226761537648),
                new AdjustmentFactors(-14.285405495898996, 0.3449896701901321)
        ));
    }

    @Test
    public void testTies() {
        RatingModelConfig config = RatingModelConfig.builder().build();
        TeamResult<Integer> teamA = teamResult(config, 1, 1);
        TeamResult<Integer> teamB = teamResult(config, 2, 2);
        TeamResult<Integer> teamC = teamResult(config, 1, 3);
        RatingModel model = new ThurstoneMostellerFull(config);
        List<AdjustmentFactors> adjustmentFactors = ValidationHelper.calculateAdjustments(model, teamA, teamB, teamC);
        assertThat(adjustmentFactors).isEqualTo(List.of(
                new AdjustmentFactors(15.412813725411178, 0.2231609774670968),
                new AdjustmentFactors(-15.983106349989516, 0.42002841495490706),
                new AdjustmentFactors(-22.263781651249257, 0.663646936556056)

        ));
    }

    private TeamResult<Integer> teamResult(RatingModelConfig config, int rank, int playersCount) {
        List<MatchMakingRating> players = new ArrayList<>(playersCount);
        for (int i = 0; i < playersCount; i++) {
            players.add(new SimpleMatchMakingRating(mu, sigma));
        }
        return ValidationHelper.teamResult(playerId, config, rank, players);
    }
}
