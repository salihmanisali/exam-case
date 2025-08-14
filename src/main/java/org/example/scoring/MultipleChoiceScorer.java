package org.example.scoring;

import org.example.models.Answers;
import org.example.models.UserAnswer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MultipleChoiceScorer implements Scorer {

    private static final Logger logger = LoggerFactory.getLogger(MultipleChoiceScorer.class);

    // This map would typically be populated from a database with correct answers for each question.
    // For demonstration, let's hardcode some correct answers.
    private static final Map<Integer, Integer> CORRECT_ANSWERS = new HashMap<>();
    static {
        CORRECT_ANSWERS.put(1, 1); // Question 1 (2+2), correct option is index 1 (which is '4')
        CORRECT_ANSWERS.put(2, 2); // Question 2 (Capital of France), correct option is index 2 (which is 'Paris')
    }

    @Override
    public double score(Answers answers) {
        logger.info("Scoring multiple-choice exam for exam ID: {}", answers.getExamId());
        double totalScore = 0;
        for (UserAnswer userAnswer : answers.getUserAnswers()) {
            int questionId = userAnswer.getQuestionId();
            int selectedOptionIndex = userAnswer.getSelectedOptionIndex();

            if (CORRECT_ANSWERS.containsKey(questionId)) {
                if (CORRECT_ANSWERS.get(questionId) == selectedOptionIndex) {
                    totalScore += 1.0; // Award 1 point for each correct answer
                    logger.debug("Question {} is correct. Current score: {}", questionId, totalScore);
                } else {
                    logger.debug("Question {} is incorrect. Selected: {}, Correct: {}", questionId, selectedOptionIndex, CORRECT_ANSWERS.get(questionId));
                }
            } else {
                logger.warn("No correct answer defined for multiple-choice question ID: {}", questionId);
            }
        }
        logger.info("Multiple-choice exam scored. Total score: {}", totalScore);
        return totalScore;
    }
}
