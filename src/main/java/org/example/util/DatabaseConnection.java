package org.example.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final HikariDataSource ds;

    static {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();

        // Read database connection details from .env or system environment
        String dbUrl = dotenv.get("DB_URL");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");

        if (dbUrl == null || dbUser == null || dbPassword == null) {
            logger.error("Database environment variables (DB_URL, DB_USER, DB_PASSWORD) must be set in .env file or system environment.");
            throw new IllegalStateException("Database connection properties are not configured. Please set DB_URL, DB_USER, and DB_PASSWORD.");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void initializeDatabase() {
        logger.info("Initializing database schema...");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create tables if they do not exist
            stmt.execute("CREATE TABLE IF NOT EXISTS User (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255) NOT NULL UNIQUE, passwordHash VARCHAR(255) NOT NULL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Exam (id INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255) NOT NULL, description VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS Question (id INT AUTO_INCREMENT PRIMARY KEY, exam_id INT, text VARCHAR(1024) NOT NULL, type VARCHAR(50) NOT NULL, FOREIGN KEY (exam_id) REFERENCES Exam(id) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS question_options (question_id INT, option_value VARCHAR(255), FOREIGN KEY (question_id) REFERENCES Question(id) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Answers (id INT AUTO_INCREMENT PRIMARY KEY, examId INT, FOREIGN KEY (examId) REFERENCES Exam(id) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS UserAnswer (id INT AUTO_INCREMENT PRIMARY KEY, answers_id INT, questionId INT, userAnswerText VARCHAR(1024), selectedOptionIndex INT, FOREIGN KEY (answers_id) REFERENCES Answers(id) ON DELETE CASCADE)");

            logger.info("Database initialized successfully.");
        } catch (SQLException e) {
            logger.error("Failed to initialize database schema.", e);
            System.exit(1);
        }
    }
}
