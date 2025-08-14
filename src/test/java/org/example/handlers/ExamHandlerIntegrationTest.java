package org.example.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Main;
import org.example.dto.ApiResponse;
import org.example.models.Exam;
import org.example.util.DatabaseConnection;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExamHandlerIntegrationTest {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String authToken;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";


    @BeforeAll
    public void startServerAndAuthenticate() throws Exception {
        // Start the Jetty server
        Main.startServer();

        // Create a test user and get a token
        createTestUser();
        loginAndGetToken();
    }

    @AfterAll
    public void stopServer() throws Exception {
        // Clean up the test user
        cleanupDatabase();
        // Stop the Jetty server
        Main.stopServer();
    }

    @BeforeEach
    public void cleanupTables() {
        // Clean up tables before each test to ensure isolation, but leave the user for auth
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            stmt.execute("TRUNCATE TABLE Question");
            stmt.execute("TRUNCATE TABLE Exam");
            stmt.execute("TRUNCATE TABLE UserAnswer");
            stmt.execute("TRUNCATE TABLE Answers");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    @DisplayName("GET /exams/999 - Should return 404 for non-existent exam")
    public void testGetExam_NotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/exams/999"))
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        ApiResponse<Object> apiResponse = objectMapper.readValue(response.body(), new TypeReference<>() {});
        assertFalse(apiResponse.isSuccess());
        assertTrue(apiResponse.getError().getMessage().contains("not found"));
    }

    @Test
    @DisplayName("GET /exams/invalid - Should return 400 for invalid ID")
    public void testGetExam_InvalidId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/exams/invalid"))
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        ApiResponse<Object> apiResponse = objectMapper.readValue(response.body(), new TypeReference<>() {});
        assertFalse(apiResponse.isSuccess());
        assertTrue(apiResponse.getError().getMessage().contains("Invalid exam ID format"));
    }

    @Test
    @DisplayName("GET /exams/{id} - Should return 200 and exam data for existing exam")
    public void testGetExam_Success() throws Exception {
        // 1. Insert a test exam into the database
        Exam testExam = new Exam(0, "Integration Test Exam", "A description for the test exam.", null);
        int examId = insertTestExam(testExam);

        // 2. Make a request to the endpoint
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/exams/" + examId))
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 3. Assert the response
        assertEquals(200, response.statusCode());
        ApiResponse<Exam> apiResponse = objectMapper.readValue(response.body(), new TypeReference<>() {});
        assertTrue(apiResponse.isSuccess());

        Exam examData = apiResponse.getData();
        assertEquals(examId, examData.getId());
        assertEquals(testExam.getTitle(), examData.getTitle());
        assertEquals(testExam.getDescription(), examData.getDescription());
    }

    private void createTestUser() throws Exception {
        String hashedPassword = BCrypt.hashpw(TEST_PASSWORD, BCrypt.gensalt());

        String sql = "INSERT INTO User (username, passwordHash) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, TEST_USERNAME);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
        }
    }

    private void loginAndGetToken() throws Exception {
        String auth = TEST_USERNAME + ":" + TEST_PASSWORD;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        String authHeader = "Basic " + encodedAuth;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Authorization", authHeader)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to login and get token. Response: " + response.body());
        }

        ApiResponse<Map<String, String>> apiResponse = objectMapper.readValue(response.body(), new TypeReference<>() {});
        this.authToken = apiResponse.getData().get("token");
    }

    private int insertTestExam(Exam exam) throws Exception {
        String sql = "INSERT INTO Exam (title, description) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, exam.getTitle());
            pstmt.setString(2, exam.getDescription());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new IllegalStateException("Creating exam failed, no ID obtained.");
                }
            }
        }
    }

    private void cleanupDatabase() {
        String sql = "DELETE FROM User WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, TEST_USERNAME);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
