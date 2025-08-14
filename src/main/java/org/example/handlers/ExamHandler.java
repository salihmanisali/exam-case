package org.example.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ApiResponse;
import org.example.models.Exam;
import org.example.services.ExamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExamHandler extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ExamHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExamService examService;

    public ExamHandler(ExamService examService) {
        this.examService = examService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            send(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error("Bad Request: Exam ID is required."));
            return;
        }

        try {
            int examId = Integer.parseInt(pathInfo.substring(1));
            logger.info("Received request for exam with ID: {}", examId);

            Exam exam = examService.getExamById(examId);

            if (exam != null) {
                logger.info("Fetched exam with ID: {}.", examId);
                send(resp, HttpServletResponse.SC_OK, ApiResponse.success(exam));
            } else {
                logger.warn("No exam found for ID: {}", examId);
                send(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error("Not Found: Exam with ID " + examId + " not found."));
            }

        } catch (NumberFormatException e) {
            logger.warn("Invalid exam ID format: {}", pathInfo.substring(1));
            send(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error("Bad Request: Invalid exam ID format."));
        } catch (Exception e) {
            logger.error("An unexpected error occurred while fetching exam: " + e.getMessage(), e);
            send(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ApiResponse.error("Internal Server Error."));
        }
    }

    private void send(HttpServletResponse resp, int status, ApiResponse<?> response) throws IOException {
        resp.setStatus(status);
        objectMapper.writeValue(resp.getWriter(), response);
    }
}
