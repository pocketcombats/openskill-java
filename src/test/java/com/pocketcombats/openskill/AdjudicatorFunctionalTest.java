package com.pocketcombats.openskill;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocketcombats.openskill.data.MatchMakingRating;
import com.pocketcombats.openskill.data.RatingAdjustment;
import com.pocketcombats.openskill.data.SimpleMatchMakingRating;
import com.pocketcombats.openskill.model.BradleyTerryFull;
import com.pocketcombats.openskill.model.PlackettLuce;
import com.pocketcombats.openskill.model.ThurstoneMostellerFull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.pocketcombats.openskill.util.ValidationHelper.teamResult;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdjudicatorFunctionalTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private JsonNode thurstoneMostellerFullData;
    private JsonNode bradleyTerryFullData;
    private JsonNode plackettLuceData;

    @BeforeAll
    public void loadTestData() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream("data/thurstonemostellerfull.json")) {
            thurstoneMostellerFullData = objectMapper.readTree(is);
        }
        try (InputStream is = classloader.getResourceAsStream("data/bradleyterryfull.json")) {
            bradleyTerryFullData = objectMapper.readTree(is);
        }
        try (InputStream is = classloader.getResourceAsStream("data/plackettluce.json")) {
            plackettLuceData = objectMapper.readTree(is);
        }
    }

    // Thurstone-Mosteller

    @Test
    public void testStandardThurstoneMostellerFull() {
        AtomicInteger id = new AtomicInteger(1);
        RatingModelConfig config = new RatingModelConfig.Builder().build();

        double mu = thurstoneMostellerFullData.get("model").get("mu").asDouble();
        double sigma = thurstoneMostellerFullData.get("model").get("sigma").asDouble();
        List<MatchMakingRating> teamAPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> teamBPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<RatingAdjustment<Integer>> ratingAdjustments = new Adjudicator<Integer>(
                config,
                new ThurstoneMostellerFull(config)
        ).rate(
                List.of(
                        teamResult(id, config, 1, teamAPlayers),
                        teamResult(id, config, 2, teamBPlayers)
                )
        );
        checkExpected(thurstoneMostellerFullData, "normal", ratingAdjustments);
    }

    @Test
    public void testLimitSigmaThurstoneMostellerFull() {
        AtomicInteger id = new AtomicInteger(1);
        RatingModelConfig config = new RatingModelConfig.Builder().setLimitSigma(true).build();

        double mu = thurstoneMostellerFullData.get("model").get("mu").asDouble();
        double sigma = thurstoneMostellerFullData.get("model").get("sigma").asDouble();
        List<MatchMakingRating> teamAPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> teamBPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> teamCPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );

        List<RatingAdjustment<Integer>> ratingAdjustments = new Adjudicator<Integer>(
                config,
                new ThurstoneMostellerFull(config)
        ).rate(
                List.of(
                        teamResult(id, config, 2, teamAPlayers),
                        teamResult(id, config, 1, teamBPlayers),
                        teamResult(id, config, 3, teamCPlayers)
                )
        );
        checkExpected(thurstoneMostellerFullData, "limit_sigma", ratingAdjustments);
    }

    @Test
    public void testTiesThurstoneMostellerFull() {
        AtomicInteger id = new AtomicInteger(1);
        RatingModelConfig config = new RatingModelConfig.Builder().setLimitSigma(true).build();

        double mu = thurstoneMostellerFullData.get("model").get("mu").asDouble();
        double sigma = thurstoneMostellerFullData.get("model").get("sigma").asDouble();
        List<MatchMakingRating> teamAPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> teamBPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> teamCPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );

        List<RatingAdjustment<Integer>> ratingAdjustments = new Adjudicator<Integer>(
                config,
                new ThurstoneMostellerFull(config)
        ).rate(
                List.of(
                        teamResult(id, config, 1, teamAPlayers),
                        teamResult(id, config, 2, teamBPlayers),
                        teamResult(id, config, 1, teamCPlayers)
                )
        );
        checkExpected(thurstoneMostellerFullData, "ties", ratingAdjustments);
    }

    @Test
    public void testBalanceThurstoneMostellerFull() {
        AtomicInteger id = new AtomicInteger(1);
        RatingModelConfig config = new RatingModelConfig.Builder().setBalance(true).build();

        double mu = thurstoneMostellerFullData.get("model").get("mu").asDouble();
        double sigma = thurstoneMostellerFullData.get("model").get("sigma").asDouble();
        List<MatchMakingRating> teamAPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> teamBPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<RatingAdjustment<Integer>> ratingAdjustments = new Adjudicator<Integer>(
                config,
                new ThurstoneMostellerFull(config)
        ).rate(
                List.of(
                        teamResult(id, config, 1, teamAPlayers),
                        teamResult(id, config, 2, teamBPlayers)
                )
        );
        // While it does match test dataset, this test is actually useless.
        // The whole point in "balance" settings is to incorporate ordinals into calculations
        // which is meaningless if all players have the exact same mu and sigma.
        checkExpected(thurstoneMostellerFullData, "balance", ratingAdjustments);
    }

    @Test
    public void testRealBalanceThurstoneMostellerFull() {
        AtomicInteger id = new AtomicInteger(1);

        RatingModelConfig config = new RatingModelConfig.Builder().setBalance(true).build();
        List<MatchMakingRating> teamAPlayers = List.of(
                new SimpleMatchMakingRating(19.04, 7.53),
                new SimpleMatchMakingRating(22.01, 6.18)
        );
        List<MatchMakingRating> teamBPlayers = List.of(
                new SimpleMatchMakingRating(24.03, 8.901),
                new SimpleMatchMakingRating(19.7, 5.07)
        );
        List<RatingAdjustment<Integer>> ratingAdjustments = new Adjudicator<Integer>(
                config,
                new ThurstoneMostellerFull(config)
        ).rate(
                List.of(
                        teamResult(id, config, 1, teamAPlayers),
                        teamResult(id, config, 2, teamBPlayers)
                )
        );

        assertThat(ratingAdjustments)
                .usingElementComparator(new RatingAdjustmentApproximateComparator())
                .isEqualTo(List.of(
                        new RatingAdjustment<>(1, 21.307660705559808, 7.330915700610215),
                        new RatingAdjustment<>(2, 23.537533857501394, 6.070725855776011),
                        new RatingAdjustment<>(3, 20.861522684036647, 8.552930082731905),
                        new RatingAdjustment<>(4, 18.671822331013587, 5.00713350441074)
                ));
    }

    @Test
    public void testPlayerWeightsThurstoneMostellerFull() {
        AtomicInteger id = new AtomicInteger(1);
        RatingModelConfig config = new RatingModelConfig.Builder().setBalance(true).build();

        double mu = thurstoneMostellerFullData.get("model").get("mu").asDouble();
        double sigma = thurstoneMostellerFullData.get("model").get("sigma").asDouble();
        List<MatchMakingRating> team1Players = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> team2Players = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> team3Players = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> team4Players = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );

        List<RatingAdjustment<Integer>> ratingAdjustments = new Adjudicator<Integer>(
                config,
                new ThurstoneMostellerFull(config)
        ).rate(
                List.of(
                        teamResult(id, config, 2, team1Players, List.of(2.0, 1.0, 1.0)),
                        teamResult(id, config, 1, team2Players, List.of(1.0, 2.0)),
                        teamResult(id, config, 4, team3Players, List.of(1.0, 1.0, 2.0)),
                        teamResult(id, config, 3, team4Players, List.of(1.0, 2.0))
                )
        );
        checkExpected(thurstoneMostellerFullData, "weights", ratingAdjustments);
    }

    // Bradley-Terry

    @Test
    public void testRanksBradleyTerryFull() {
        AtomicInteger id = new AtomicInteger(1);
        RatingModelConfig config = new RatingModelConfig.Builder().build();

        double mu = bradleyTerryFullData.get("model").get("mu").asDouble();
        double sigma = bradleyTerryFullData.get("model").get("sigma").asDouble();
        List<MatchMakingRating> team1Players = List.of(
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> team2Players = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> team3Players = List.of(
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> team4Players = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<RatingAdjustment<Integer>> ratingAdjustments = new Adjudicator<Integer>(
                config,
                new BradleyTerryFull(config)
        ).rate(
                List.of(
                        teamResult(id, config, 2, team1Players),
                        teamResult(id, config, 1, team2Players),
                        teamResult(id, config, 4, team3Players),
                        teamResult(id, config, 3, team4Players)
                )
        );
        checkExpected(bradleyTerryFullData, "ranks", ratingAdjustments);
    }

    @Test
    public void testLimitSigmaBradleyTerryFull() {
        AtomicInteger id = new AtomicInteger(1);
        RatingModelConfig config = new RatingModelConfig.Builder().setLimitSigma(true).build();

        double mu = bradleyTerryFullData.get("model").get("mu").asDouble();
        double sigma = bradleyTerryFullData.get("model").get("sigma").asDouble();
        List<MatchMakingRating> teamAPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> teamBPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> teamCPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );

        List<RatingAdjustment<Integer>> ratingAdjustments = new Adjudicator<Integer>(
                config,
                new BradleyTerryFull(config)
        ).rate(
                List.of(
                        teamResult(id, config, 2, teamAPlayers),
                        teamResult(id, config, 1, teamBPlayers),
                        teamResult(id, config, 3, teamCPlayers)
                )
        );
        checkExpected(bradleyTerryFullData, "limit_sigma", ratingAdjustments);
    }

    @Test
    public void testTiesBradleyTerryFull() {
        AtomicInteger id = new AtomicInteger(1);
        RatingModelConfig config = new RatingModelConfig.Builder().build();

        double mu = bradleyTerryFullData.get("model").get("mu").asDouble();
        double sigma = bradleyTerryFullData.get("model").get("sigma").asDouble();
        List<MatchMakingRating> team1Players = List.of(
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> team2Players = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> team3Players = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<RatingAdjustment<Integer>> ratingAdjustments = new Adjudicator<Integer>(
                config,
                new BradleyTerryFull(config)
        ).rate(
                List.of(
                        teamResult(id, config, 1, team1Players),
                        teamResult(id, config, 2, team2Players),
                        teamResult(id, config, 1, team3Players)
                )
        );
        checkExpected(bradleyTerryFullData, "ties", ratingAdjustments);
    }

    // Plackett-Luce

    @Test
    public void testRanksPlackettLuce() {
        AtomicInteger id = new AtomicInteger(1);
        RatingModelConfig config = new RatingModelConfig.Builder().build();

        double mu = plackettLuceData.get("model").get("mu").asDouble();
        double sigma = plackettLuceData.get("model").get("sigma").asDouble();
        List<MatchMakingRating> team1Players = List.of(
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> team2Players = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> team3Players = List.of(
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> team4Players = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<RatingAdjustment<Integer>> ratingAdjustments = new Adjudicator<Integer>(
                config,
                new PlackettLuce(config)
        ).rate(
                List.of(
                        teamResult(id, config, 2, team1Players),
                        teamResult(id, config, 1, team2Players),
                        teamResult(id, config, 4, team3Players),
                        teamResult(id, config, 3, team4Players)
                )
        );
        checkExpected(plackettLuceData, "ranks", ratingAdjustments);
    }

    @Test
    public void testLimitSigmaPlackettLuce() {
        AtomicInteger id = new AtomicInteger(1);
        RatingModelConfig config = new RatingModelConfig.Builder().setLimitSigma(true).build();

        double mu = plackettLuceData.get("model").get("mu").asDouble();
        double sigma = plackettLuceData.get("model").get("sigma").asDouble();
        List<MatchMakingRating> teamAPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> teamBPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> teamCPlayers = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );

        List<RatingAdjustment<Integer>> ratingAdjustments = new Adjudicator<Integer>(
                config,
                new PlackettLuce(config)
        ).rate(
                List.of(
                        teamResult(id, config, 2, teamAPlayers),
                        teamResult(id, config, 1, teamBPlayers),
                        teamResult(id, config, 3, teamCPlayers)
                )
        );
        checkExpected(plackettLuceData, "limit_sigma", ratingAdjustments);
    }

    @Test
    public void testTiesPlackettLuce() {
        AtomicInteger id = new AtomicInteger(1);
        RatingModelConfig config = new RatingModelConfig.Builder().build();

        double mu = plackettLuceData.get("model").get("mu").asDouble();
        double sigma = plackettLuceData.get("model").get("sigma").asDouble();
        List<MatchMakingRating> team1Players = List.of(
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> team2Players = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<MatchMakingRating> team3Players = List.of(
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma),
                new SimpleMatchMakingRating(mu, sigma)
        );
        List<RatingAdjustment<Integer>> ratingAdjustments = new Adjudicator<Integer>(
                config,
                new PlackettLuce(config)
        ).rate(
                List.of(
                        teamResult(id, config, 1, team1Players),
                        teamResult(id, config, 2, team2Players),
                        teamResult(id, config, 1, team3Players)
                )
        );
        checkExpected(plackettLuceData, "ties", ratingAdjustments);
    }

    public static void checkExpected(
            JsonNode data,
            String key,
            List<? extends RatingAdjustment<?>> actualRatingAdjustments
    ) throws AssertionFailedError {
        JsonNode expectedResults = data.get(key);
        assertMatches(expectedResults, actualRatingAdjustments);
    }

    private static void assertMatches(
            JsonNode expectedTeamResults,
            List<? extends RatingAdjustment<?>> actualRatingAdjustments
    ) {
        List<RatingAdjustment<?>> expectedRatings = new ArrayList<>(actualRatingAdjustments.size());
        int expectedId = 1;
        for (var teamNode : expectedTeamResults) {
            for (var playerNode : teamNode) {
                expectedRatings.add(
                        new RatingAdjustment<>(
                                expectedId++,
                                playerNode.get("mu").asDouble(),
                                playerNode.get("sigma").asDouble()
                        )
                );
            }
        }
        assertThat(actualRatingAdjustments)
                .usingElementComparator(new RatingAdjustmentApproximateComparator())
                .isEqualTo(expectedRatings);
    }

    private static class RatingAdjustmentApproximateComparator implements Comparator<RatingAdjustment<?>> {

        private static final double EPSILON = 1e-14;

        @Override
        public int compare(RatingAdjustment<?> o1, RatingAdjustment<?> o2) {
            double muDiff = Math.abs(o1.mu() - o2.mu());
            if (muDiff > EPSILON) {
                return Double.compare(o1.mu(), o2.mu());
            }
            double sigmaDiff = Math.abs(o1.sigma() - o2.sigma());
            if (sigmaDiff > EPSILON) {
                return Double.compare(o1.sigma(), o2.sigma());
            }
            return 0;
        }
    }
}
