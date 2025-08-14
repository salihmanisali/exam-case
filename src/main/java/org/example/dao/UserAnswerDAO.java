package org.example.dao;

import org.example.models.UserAnswer;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserAnswerDAO implements IUserAnswerDAO {

    @Override
    public void addUserAnswer(UserAnswer userAnswer, int answersId, Connection conn) throws SQLException {
        String sql = "INSERT INTO UserAnswer (answers_id, questionId, userAnswerText, selectedOptionIndex) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, answersId);
            pstmt.setInt(2, userAnswer.getQuestionId());
            pstmt.setString(3, userAnswer.getUserAnswerText());
            pstmt.setInt(4, userAnswer.getSelectedOptionIndex());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    userAnswer.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<UserAnswer> getUserAnswersByAnswersId(int answersId) {
        List<UserAnswer> userAnswers = new ArrayList<>();
        String sql = "SELECT * FROM UserAnswer WHERE answers_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, answersId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    UserAnswer userAnswer = new UserAnswer();
                    userAnswer.setId(rs.getInt("id"));
                    userAnswer.setQuestionId(rs.getInt("questionId"));
                    userAnswer.setUserAnswerText(rs.getString("userAnswerText"));
                    userAnswer.setSelectedOptionIndex(rs.getInt("selectedOptionIndex"));
                    userAnswers.add(userAnswer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userAnswers;
    }

    @Override
    public void deleteUserAnswersByAnswersId(int answersId, Connection conn) throws SQLException {
        String sql = "DELETE FROM UserAnswer WHERE answers_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, answersId);
            pstmt.executeUpdate();
        }
    }
}
