package org.example.dao;

import org.example.models.Answers;
import org.example.models.UserAnswer;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswersDAO implements IAnswersDAO {

    private final IUserAnswerDAO userAnswerDAO;

    public AnswersDAO(IUserAnswerDAO userAnswerDAO) {
        this.userAnswerDAO = userAnswerDAO;
    }

    @Override
    public void addAnswers(Answers answers) {
        String sql = "INSERT INTO Answers (examId) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            pstmt.setInt(1, answers.getExamId());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    answers.setId(generatedKeys.getInt(1));
                }
            }

            for (UserAnswer userAnswer : answers.getUserAnswers()) {
                userAnswerDAO.addUserAnswer(userAnswer, answers.getId(), conn);
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public Answers getAnswersById(int id) {
        String sql = "SELECT * FROM Answers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Answers answers = new Answers();
                    answers.setId(rs.getInt("id"));
                    answers.setExamId(rs.getInt("examId"));
                    answers.setUserAnswers(userAnswerDAO.getUserAnswersByAnswersId(id));
                    return answers;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Answers> getAllAnswers() {
        List<Answers> answersList = new ArrayList<>();
        String sql = "SELECT * FROM Answers";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Answers answers = new Answers();
                answers.setId(rs.getInt("id"));
                answers.setExamId(rs.getInt("examId"));
                answers.setUserAnswers(userAnswerDAO.getUserAnswersByAnswersId(answers.getId()));
                answersList.add(answers);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answersList;
    }

    @Override
    public void updateAnswers(Answers answers) {
        String sql = "UPDATE Answers SET examId = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            pstmt.setInt(1, answers.getExamId());
            pstmt.setInt(2, answers.getId());
            pstmt.executeUpdate();

            userAnswerDAO.deleteUserAnswersByAnswersId(answers.getId(), conn);
            for (UserAnswer userAnswer : answers.getUserAnswers()) {
                userAnswerDAO.addUserAnswer(userAnswer, answers.getId(), conn);
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void deleteAnswers(int id) {
        String sql = "DELETE FROM Answers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            userAnswerDAO.deleteUserAnswersByAnswersId(id, conn);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
