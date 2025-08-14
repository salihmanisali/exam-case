package org.example.services;

import org.example.dao.IExamDAO;
import org.example.models.Exam;

import java.util.List;

public class ExamServiceImpl implements ExamService {

    private final IExamDAO examDAO;

    public ExamServiceImpl(IExamDAO examDAO) {
        this.examDAO = examDAO;
    }

    @Override
    public Exam getExamById(int id) {
        return examDAO.getExamById(id);
    }

    @Override
    public List<Exam> getAllExams() {
        return examDAO.getAllExams();
    }

    @Override
    public void addExam(Exam exam) {
        examDAO.addExam(exam);
    }

    @Override
    public void updateExam(Exam exam) {
        examDAO.updateExam(exam);
    }

    @Override
    public void deleteExam(int id) {
        examDAO.deleteExam(id);
    }
}
