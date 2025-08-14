package org.example.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ApiResponse;
import org.example.models.Answers;
import org.example.services.SubmitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SubmitHandler extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(SubmitHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SubmitService submitService;

    public SubmitHandler(SubmitService submitService) {
        this.submitService = submitService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            Answers userAnswers = objectMapper.readValue(req.getInputStream(), Answers.class);
            logger.info("Received submission for exam ID: {}", userAnswers.getExamId());

            double score = submitService.processSubmission(userAnswers);

            Map<String, Object> data = new HashMap<>();
            data.put("examId", userAnswers.getExamId());
            data.put("score", score);

            send(resp, HttpServletResponse.SC_OK, ApiResponse.success(data, "Exam submitted successfully."));

        } catch (Exception e) {
            logger.error("Error processing exam submission: " + e.getMessage(), e);
            send(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ApiResponse.error("Internal Server Error: " + e.getMessage()));
        }
    }

    private void send(HttpServletResponse resp, int status, ApiResponse<?> response) throws IOException {
        resp.setStatus(status);
        objectMapper.writeValue(resp.getWriter(), response);
    }
}
