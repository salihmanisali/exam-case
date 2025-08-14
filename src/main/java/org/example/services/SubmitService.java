package org.example.services;

import org.example.models.Answers;
import org.example.models.Exam;

public interface SubmitService {
    double processSubmission(Answers userAnswers) throws Exception;
}
