package org.example.scoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScorerFactory {

    private static final Logger logger = LoggerFactory.getLogger(ScorerFactory.class);

    public static Scorer getScorer(String examType) {
        if (examType == null || examType.trim().isEmpty()) {
            logger.error("Exam type cannot be null or empty.");
            throw new IllegalArgumentException("Exam type cannot be null or empty.");
        }

        switch (examType.toLowerCase()) {
            case "multiple_choice":
                logger.info("Creating MultipleChoiceScorer.");
                return new MultipleChoiceScorer();
            case "classical":
                logger.info("Creating ClassicalScorer.");
                return new ClassicalScorer();
            // Add more cases for other exam types as needed
            default:
                logger.error("Unknown exam type: {}", examType);
                throw new IllegalArgumentException("Unknown exam type: " + examType);
        }
    }
}
