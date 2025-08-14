package org.example.dao;

import org.example.models.Answers;

import java.util.List;

public interface IAnswersDAO {
    void addAnswers(Answers answers);
    Answers getAnswersById(int id);
    List<Answers> getAllAnswers();
    void updateAnswers(Answers answers);
    void deleteAnswers(int id);
}
