package org.example.dao;

import org.example.models.Exam;
import org.example.models.Question;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO implements IExamDAO {

    private final IQuestionDAO questionDAO;

    public ExamDAO(IQuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }

    @Override
    public void addExam(Exam exam) {
        String sql = "INSERT INTO Exam (title, description) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            pstmt.setString(1, exam.getTitle());
            pstmt.setString(2, exam.getDescription());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    exam.setId(generatedKeys.getInt(1));
                }
            }

            for (Question question : exam.getQuestions()) {
                questionDAO.addQuestion(question, exam.getId(), conn);
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Exam getExamById(int id) {
        String sql = "SELECT * FROM Exam WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Exam exam = new Exam();
                    exam.setId(rs.getInt("id"));
                    exam.setTitle(rs.getString("title"));
                    exam.setDescription(rs.getString("description"));
                    exam.setQuestions(questionDAO.getQuestionsByExamId(id));
                    return exam;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Exam> getAllExams() {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT * FROM Exam";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Exam exam = new Exam();
                exam.setId(rs.getInt("id"));
                exam.setTitle(rs.getString("title"));
                exam.setDescription(rs.getString("description"));
                exam.setQuestions(questionDAO.getQuestionsByExamId(exam.getId()));
                exams.add(exam);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }

    @Override
    public void updateExam(Exam exam) {
        String sql = "UPDATE Exam SET title = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            pstmt.setString(1, exam.getTitle());
            pstmt.setString(2, exam.getDescription());
            pstmt.setInt(3, exam.getId());
            pstmt.executeUpdate();

            questionDAO.deleteQuestionsByExamId(exam.getId(), conn);
            for (Question question : exam.getQuestions()) {
                questionDAO.addQuestion(question, exam.getId(), conn);
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteExam(int id) {
        String sql = "DELETE FROM Exam WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            questionDAO.deleteQuestionsByExamId(id, conn);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
