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
class PlackettLuceTest {

    private final AtomicInteger playerId = new AtomicInteger(1);
    private JsonNode data;
    private double mu;
    private double sigma;

    @BeforeAll
    public void loadTestData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream("data/plackettluce.json")) {
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
        RatingModel model = new PlackettLuce(config);
        List<AdjustmentFactors> adjustmentFactors = ValidationHelper.calculateAdjustments(model, teamA, teamB);
        assertThat(adjustmentFactors).isEqualTo(List.of(
                new AdjustmentFactors(1.3438502541982293, 0.01429592770173385),
                new AdjustmentFactors(-2.6877005083964596, 0.04043498968499449)
        ));
    }

    @Test
    public void testTies() {
        RatingModelConfig config = RatingModelConfig.builder().build();
        TeamResult<Integer> teamA = teamResult(config, 1, 1);
        TeamResult<Integer> teamB = teamResult(config, 2, 2);
        TeamResult<Integer> teamC = teamResult(config, 1, 3);
        RatingModel model = new PlackettLuce(config);
        List<AdjustmentFactors> adjustmentFactors = ValidationHelper.calculateAdjustments(model, teamA, teamB, teamC);
        assertThat(adjustmentFactors).isEqualTo(List.of(
                new AdjustmentFactors(0.5185179695652332, 0.0030747957534617626),
                new AdjustmentFactors(-0.6488623101048707, 0.018625557589488178),
                new AdjustmentFactors(-0.5822604435383932, 0.04127750142509829)

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
