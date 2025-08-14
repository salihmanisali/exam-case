package org.example.dao;

import org.example.models.Question;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO implements IQuestionDAO {

    @Override
    public void addQuestion(Question question, int examId, Connection conn) throws SQLException {
        String questionSql = "INSERT INTO Question (exam_id, text, type) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(questionSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, examId);
            pstmt.setString(2, question.getText());
            pstmt.setString(3, question.getType());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    question.setId(generatedKeys.getInt(1));
                }
            }

            if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                String optionSql = "INSERT INTO question_options (question_id, option_value) VALUES (?, ?)";
                try (PreparedStatement optPstmt = conn.prepareStatement(optionSql)) {
                    for (String option : question.getOptions()) {
                        optPstmt.setInt(1, question.getId());
                        optPstmt.setString(2, option);
                        optPstmt.addBatch();
                    }
                    optPstmt.executeBatch();
                }
            }
        }
    }

    @Override
    public List<Question> getQuestionsByExamId(int examId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM Question WHERE exam_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Question question = new Question();
                    question.setId(rs.getInt("id"));
                    question.setText(rs.getString("text"));
                    question.setType(rs.getString("type"));
                    question.setOptions(getOptionsForQuestion(question.getId(), conn));
                    questions.add(question);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    private List<String> getOptionsForQuestion(int questionId, Connection conn) throws SQLException {
        List<String> options = new ArrayList<>();
        String sql = "SELECT option_value FROM question_options WHERE question_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    options.add(rs.getString("option_value"));
                }
            }
        }
        return options;
    }

    @Override
    public void deleteQuestionsByExamId(int examId, Connection conn) throws SQLException {
        String deleteOptionsSql = "DELETE FROM question_options WHERE question_id IN (SELECT id FROM Question WHERE exam_id = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteOptionsSql)) {
            pstmt.setInt(1, examId);
            pstmt.executeUpdate();
        }

        String deleteQuestionsSql = "DELETE FROM Question WHERE exam_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuestionsSql)) {
            pstmt.setInt(1, examId);
            pstmt.executeUpdate();
        }
    }
}
