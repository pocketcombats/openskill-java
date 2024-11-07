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
class BradleyTerryFullTest {

    private final AtomicInteger playerId = new AtomicInteger(1);
    private JsonNode data;
    private double mu;
    private double sigma;

    @BeforeAll
    public void loadTestData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream("data/bradleyterryfull.json")) {
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
        RatingModel model = new BradleyTerryFull(config);
        List<AdjustmentFactors> adjustmentFactors = ValidationHelper.calculateAdjustments(model, teamA, teamB);
        assertThat(adjustmentFactors).isEqualTo(List.of(
                new AdjustmentFactors(4.022909389092442, 0.0416647689523492),
                new AdjustmentFactors(-8.045818778184886, 0.11784576265110741)
        ));
    }

    @Test
    public void testTies() {
        RatingModelConfig config = RatingModelConfig.builder().build();
        TeamResult<Integer> teamA = teamResult(config, 1, 1);
        TeamResult<Integer> teamB = teamResult(config, 2, 2);
        TeamResult<Integer> teamC = teamResult(config, 1, 3);
        RatingModel model = new BradleyTerryFull(config);
        List<AdjustmentFactors> adjustmentFactors = ValidationHelper.calculateAdjustments(model, teamA, teamB, teamC);
        assertThat(adjustmentFactors).isEqualTo(List.of(
                new AdjustmentFactors(4.936853784210042, 0.06769175547859306),
                new AdjustmentFactors(-12.649106030233364, 0.17576732666851166),
                new AdjustmentFactors(4.16309769271992, 00.24164889680403812)

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
