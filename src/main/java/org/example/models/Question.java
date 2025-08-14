package org.example.models;

import java.util.List;

public class Question {
    private int id;
    private int examId;
    private String text;
    private String type; // e.g., "multiple_choice", "classical"
    private List<String> options; // For multiple choice questions

    public Question() {
    }

    public Question(int id, int examId, String text, String type, List<String> options) {
        this.id = id;
        this.examId = examId;
        this.text = text;
        this.type = type;
        this.options = options;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
