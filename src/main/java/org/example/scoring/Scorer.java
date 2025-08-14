package org.example.scoring;

import org.example.models.Answers;

public interface Scorer {
    /**
     * Scores the given answers based on the specific scoring strategy.
     * @param answers The user's submitted answers.
     * @return The calculated score.
     */
    double score(Answers answers);
}
