package org.example.services;

import org.example.dao.IAnswersDAO;
import org.example.dao.IExamDAO;
import org.example.models.Answers;
import org.example.models.Exam;
import org.example.models.Question;
import org.example.scoring.Scorer;
import org.example.scoring.ScorerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SubmitServiceImpl implements SubmitService {

    private static final Logger logger = LoggerFactory.getLogger(SubmitServiceImpl.class);
    private final IExamDAO examDAO;
    private final IAnswersDAO answersDAO;

    public SubmitServiceImpl(IExamDAO examDAO, IAnswersDAO answersDAO) {
        this.examDAO = examDAO;
        this.answersDAO = answersDAO;
    }

    @Override
    public double processSubmission(Answers userAnswers) throws Exception {
        logger.info("Processing submission for exam ID: {}", userAnswers.getExamId());

        // 1. Fetch the Exam from the database to validate and get questions
        Exam exam = examDAO.getExamById(userAnswers.getExamId());
        if (exam == null) {
            throw new Exception("Exam with ID " + userAnswers.getExamId() + " not found.");
        }

        // 2. Save the user's answers to the database
        answersDAO.addAnswers(userAnswers);
        logger.info("User answers for exam ID {} saved successfully.", userAnswers.getExamId());

        // 3. Determine exam type for scoring
        String examType = determineExamType(exam);

        // 4. Get the appropriate scorer and calculate the score
        Scorer scorer = ScorerFactory.getScorer(examType);
        double score = scorer.score(userAnswers);
        logger.info("Score calculated for exam ID {}: {}", userAnswers.getExamId(), score);

        // TODO: Update the submission in the database with the calculated score.

        return score;
    }

    private String determineExamType(Exam exam) throws Exception {
        List<Question> questions = exam.getQuestions();
        if (questions == null || questions.isEmpty()) {
            throw new Exception("Exam has no questions. Cannot determine exam type for scoring.");
        }

        String firstQuestionType = questions.get(0).getType();
        boolean allSameType = questions.stream().allMatch(q -> q.getType().equals(firstQuestionType));

        if (allSameType) {
            return firstQuestionType;
        } else {
            logger.warn("Exam ID {} has mixed question types. Defaulting to 'multiple_choice' scorer.", exam.getId());
            return "multiple_choice"; // Fallback for mixed types
        }
    }
}
