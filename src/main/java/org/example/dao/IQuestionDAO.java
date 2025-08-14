package org.example.dao;

import org.example.models.Question;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IQuestionDAO {
    void addQuestion(Question question, int examId, Connection conn) throws SQLException;
    List<Question> getQuestionsByExamId(int examId);
    void deleteQuestionsByExamId(int examId, Connection conn) throws SQLException;
}
