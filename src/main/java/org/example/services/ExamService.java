package org.example.services;

import org.example.models.Exam;

import java.util.List;

public interface ExamService {
    Exam getExamById(int id);
    List<Exam> getAllExams();
    void addExam(Exam exam);
    void updateExam(Exam exam);
    void deleteExam(int id);
}
