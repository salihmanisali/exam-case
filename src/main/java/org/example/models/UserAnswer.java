package org.example.models;

public class UserAnswer {
    private int id;
    private int questionId;
    private String userAnswerText;
    private int selectedOptionIndex; // For multiple choice, if applicable

    public UserAnswer() {
    }

    public UserAnswer(int id, int questionId, String userAnswerText, int selectedOptionIndex) {

        this.id = id;
        this.questionId = questionId;
        this.userAnswerText = userAnswerText;
        this.selectedOptionIndex = selectedOptionIndex;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getUserAnswerText() {
        return userAnswerText;
    }

    public void setUserAnswerText(String userAnswerText) {
        this.userAnswerText = userAnswerText;
    }

    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    public void setSelectedOptionIndex(int selectedOptionIndex) {
        this.selectedOptionIndex = selectedOptionIndex;
    }
}
