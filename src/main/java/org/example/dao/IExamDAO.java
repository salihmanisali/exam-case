package org.example.dao;

import org.example.models.Exam;

import java.util.List;

public interface IExamDAO {
    void addExam(Exam exam);
    Exam getExamById(int id);
    List<Exam> getAllExams();
    void updateExam(Exam exam);
    void deleteExam(int id);
}
