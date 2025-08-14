package org.example.dao;

import org.example.models.UserAnswer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IUserAnswerDAO {
    void addUserAnswer(UserAnswer userAnswer, int answersId, Connection conn) throws SQLException;
    List<UserAnswer> getUserAnswersByAnswersId(int answersId);
    void deleteUserAnswersByAnswersId(int answersId, Connection conn) throws SQLException;
}
