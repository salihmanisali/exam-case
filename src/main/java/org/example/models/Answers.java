package org.example.models;

import java.util.List;

public class Answers {
    private int id;
    private int examId;
    private List<UserAnswer> userAnswers;

    public Answers() {
    }

    public Answers(int id, int examId, List<UserAnswer> userAnswers) {
        this.id = id;
        this.examId = examId;
        this.userAnswers = userAnswers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public List<UserAnswer> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(List<UserAnswer> userAnswers) {
        this.userAnswers = userAnswers;
    }
}
