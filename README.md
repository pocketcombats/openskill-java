# OpenSkill Java

An object-oriented Java implementation of the [Weng-Lin ranking system](https://www.csie.ntu.edu.tw/~cjlin/papers/online_ranking/online_journal.pdf), providing
accurate skill-based ranking for multiplayer games. This library supports multiple ranking models and team-based
competitions.

## Requirements

- Java 16 or higher

## Features

- **Multiple Rating Models**
    - Thurstone-Mosteller: A TrueSkill™-like model ideal for head-to-head competitions
    - Bradley-Terry: Optimized for relative skill comparisons
    - Plackett-Luce: Specialized for multi-participant competitions with ordered finishes

- **Comprehensive Match Support**
    - Individual and team-based competitions
    - Multi-team games (3+ teams)
    - Draw/tie handling
    - Player weights (contribution factors)
    - Match quality assessment

- **Implementation Highlights**
    - Thread-safe
    - Flexible configuration
    - Customizable team rating aggregation
    - Customizable skill uncertainty handling
    - Zero dependencies

## Installation

Add this dependency to your `pom.xml`:

```xml

<dependency>
    <groupId>com.pocketcombats</groupId>
    <artifactId>openskill</artifactId>
    <version>1.0</version>
</dependency>
```

## Quick Start

Here's a simple example of rating a 1v1 match:

```java
// Create configuration
RatingModelConfig config = RatingModelConfig.builder().build();

// Create players with default ratings (mu=25, sigma=25/3)
SimplePlayerResult<String> player1 = new SimplePlayerResult<>("player1", 25.0, 25 / 3.0);
SimplePlayerResult<String> player2 = new SimplePlayerResult<>("player2", 25.0, 25 / 3.0);

// Create teams (1 player per team)
SimpleTeamResult<String> team1 = new SimpleTeamResult<>(
        player1.mu(), player1.sigma(), 1, // mu, sigma, rank (1 = winner)
        List.of(player1)
);
SimpleTeamResult<String> team2 = new SimpleTeamResult<>(
        player2.mu(), player2.sigma(), 2, // mu, sigma, rank (2 = loser)
        List.of(player2)
);

// Calculate new ratings using Thurstone-Mosteller model
Adjudicator<String> adjudicator = new Adjudicator<>(
        config,
        new ThurstoneMostellerFull(config)
);

List<RatingAdjustment<String>> adjustments = adjudicator.rate(List.of(team1, team2));
```

## Configuration

The `RatingModelConfig` class provides extensive customization options:

```java
RatingModelConfig config = RatingModelConfig.builder()
        // Core Parameters
        .setBeta(25.0 / 6.0)      // The scaling factor for uncertainty in skills (sigma) 
        .setKappa(0.0001)         // Update dampening factor
        .setTau(25.0 / 300.0)     // Dynamic uncertainty adjustment

        // Behavioral Controls   
        .setLimitSigma(false)     // Prevent uncertainty growth if true
        .setBalance(true)         // Enable balanced team rating aggregation

        // Balance tuning
        .setZ(3.0)                // Z-score for balance calculation
        .setAlpha(1.0)            // Balance sensitivity
        .setTarget(0.0)           // Target mean difference
        .build();
```

## Advanced Usage

### Team Games with Multiple Players

```java
// Create players with varying skill levels
SimplePlayerResult<String> p1 = new SimplePlayerResult<>("p1", 28.0, 7.0);
SimplePlayerResult<String> p2 = new SimplePlayerResult<>("p2", 27.0, 6.0);
SimplePlayerResult<String> p3 = new SimplePlayerResult<>("p3", 26.0, 5.0);
SimplePlayerResult<String> p4 = new SimplePlayerResult<>("p4", 25.0, 4.0);

// Form teams
SimpleTeamResult<String> teamA = new SimpleTeamResult<>(
        27.5,    // team mu
        6.5,     // team sigma
        1,       // rank (winner)
        List.of(p1, p2)
);

SimpleTeamResult<String> teamB = new SimpleTeamResult<>(
        25.5,    // team mu
        4.5,     // team sigma
        2,       // rank (loser)
        List.of(p3, p4)
);

// Rate using Bradley-Terry model
Adjudicator<String> adjudicator = new Adjudicator<>(
        config,
        new BradleyTerryFull(config)
);

List<RatingAdjustment<String>> adjustments = adjudicator.rate(List.of(teamA, teamB));
```

### Player Weights (Contribution Factors)

```java
SimplePlayerResult<String> player1 = new SimplePlayerResult<>(
        "mvp", 25.0, 8.3, 69.0, 1.5  // Higher weight (1.5) for higher impact
);
SimplePlayerResult<String> player2 = new SimplePlayerResult<>(
        "sub", 25.0, 8.3, 69.0, 0.7  // Lower weight (0.7) for lower impact
);
```

### Match Quality Assessment

```java
QualityEvaluator evaluator = new QualityEvaluator(config);
double matchQuality = evaluator.evaluateQuality(teamA, teamB);

// matchQuality ranges from 0 (unbalanced) to 1 (perfectly balanced)
```

### Different Team Rating Aggregation

```java
// Default aggregator
TeamRatingAggregator defaultAggregator = new DefaultTeamRatingAggregator(config);

// Weighted aggregator (considers uncertainty)
TeamRatingAggregator weightedAggregator = new WeightedTeamRatingAggregator();

MatchMakingRating teamRating = weightedAggregator.computeTeamRating(playerRatings);
```

## Rating Model Selection Guide

Choose your rating model based on your game's characteristics:

- **Thurstone-Mosteller (ThurstoneMostellerFull)**
    - Best for: Traditional competitive games
    - Features: Similar to TrueSkill™
    - Use when: Head-to-head competition is primary

- **Bradley-Terry (BradleyTerryFull)**
    - Best for: Games with clear skill differentials
    - Features: Emphasizes relative skill differences
    - Use when: Pairwise comparison accuracy is crucial

- **Plackett-Luce (PlackettLuce)**
    - Best for: Games with ordered finishes
    - Features: Handles multiple participants naturally
    - Use when: Finish order matters (e.g., racing games, free-for-all games)

## Implementations in other Languages

- [Python](https://github.com/vivekjoshy/openskill.py)
- [Javascript](https://github.com/philihp/openskill.js)
- [Kotlin](https://github.com/brezinajn/openskill.kt)
- [Elixir](https://github.com/philihp/openskill.ex)
- [Lua](https://github.com/bstummer/openskill.lua)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

Licensed under the Apache License, Version 2.0. See [LICENSE.txt](LICENSE.txt) for details.
