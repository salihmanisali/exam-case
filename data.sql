-- =================================================================
--  Bu dosya, DatabaseConnection.java'daki şema ile uyumlu CREATE TABLE ifadelerini içerir.
--  Bu script, veritabanını manuel olarak kurmak veya şemayı referans almak için kullanılabilir.
-- =================================================================

-- User Table
CREATE TABLE IF NOT EXISTS User (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    passwordHash VARCHAR(255) NOT NULL
);

-- Exam Table
CREATE TABLE IF NOT EXISTS Exam (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

-- Question Table
CREATE TABLE IF NOT EXISTS Question (
    id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT,
    text VARCHAR(1024) NOT NULL,
    type VARCHAR(50) NOT NULL,
    FOREIGN KEY (exam_id) REFERENCES Exam(id) ON DELETE CASCADE
);

-- Question Options Table (for multiple choice questions)
CREATE TABLE IF NOT EXISTS question_options (
    question_id INT,
    option_value VARCHAR(255),
    FOREIGN KEY (question_id) REFERENCES Question(id) ON DELETE CASCADE
);

-- Answers Table (represents a single submission of an exam)
CREATE TABLE IF NOT EXISTS Answers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    examId INT,
    FOREIGN KEY (examId) REFERENCES Exam(id) ON DELETE CASCADE
);

-- UserAnswer Table (represents a single answer within a submission)
CREATE TABLE IF NOT EXISTS UserAnswer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    answers_id INT,
    questionId INT,
    userAnswerText VARCHAR(1024),
    selectedOptionIndex INT,
    FOREIGN KEY (answers_id) REFERENCES Answers(id) ON DELETE CASCADE
);
