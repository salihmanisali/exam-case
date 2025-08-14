package org.example.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ApiResponse;
import org.example.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoginHandler extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthService authService;

    public LoginHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            logger.warn("Unauthorized access attempt: Missing or invalid Authorization header.");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getWriter(), ApiResponse.error("Unauthorized: Basic authentication required."));
            return;
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] parts = credentials.split(":", 2);

            if (parts.length != 2) {
                logger.warn("Unauthorized access attempt: Malformed Basic Auth credentials.");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), ApiResponse.error("Bad Request: Malformed credentials."));
                return;
            }

            String username = parts[0];
            String password = parts[1];

            Optional<String> tokenOptional = authService.login(username, password);

            if (tokenOptional.isPresent()) {
                String token = tokenOptional.get();
                logger.info("User '{}' logged in successfully. Token: {}", username, token);

                resp.setStatus(HttpServletResponse.SC_OK);
                Map<String, String> data = new HashMap<>();
                data.put("token", token);
                objectMapper.writeValue(resp.getWriter(), ApiResponse.success(data, "Login successful."));
            } else {
                logger.warn("Unauthorized access attempt: Invalid username or password for user '{}'.", username);
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                objectMapper.writeValue(resp.getWriter(), ApiResponse.error("Unauthorized: Invalid username or password."));
            }

        } catch (IllegalArgumentException e) {
            logger.error("Error decoding Basic Auth header: " + e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), ApiResponse.error("Bad Request: Invalid Base64 encoding."));
        } catch (Exception e) {
            logger.error("An unexpected error occurred during login: " + e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), ApiResponse.error("Internal Server Error."));
        }
    }
}
