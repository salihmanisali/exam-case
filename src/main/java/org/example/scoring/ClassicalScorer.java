package org.example.scoring;

import org.example.models.Answers;
import org.example.models.UserAnswer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassicalScorer implements Scorer {

    private static final Logger logger = LoggerFactory.getLogger(ClassicalScorer.class);

    @Override
    public double score(Answers answers) {
        logger.info("Scoring classical exam for exam ID: {}", answers.getExamId());
        double totalScore = 0;

        // For classical questions, scoring is typically manual or requires advanced NLP.
        // For this implementation, we'll just log the answers and return 0.
        // A more sophisticated system would involve human graders or AI analysis.
        for (UserAnswer userAnswer : answers.getUserAnswers()) {
            logger.info("Classical question ID: {}, User Answer: \"{}\"", userAnswer.getQuestionId(), userAnswer.getUserAnswerText());
            // In a real scenario, this would be marked for manual review or processed by an NLP engine.
        }

        logger.warn("Classical exam scoring is a placeholder. Manual review or advanced NLP is required for accurate scoring.");
        return totalScore; // Classical questions usually require manual grading, so initial score is 0.
    }
}
