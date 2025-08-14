package org.example.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TokenVerificationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TokenVerificationFilter.class);
    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TokenVerificationFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Exclude the login path from filtering
        if ("/login".equals(httpRequest.getServletPath())) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorizedResponse(httpResponse, "Unauthorized: Bearer token is required.");
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        if (authService.verifyToken(token)) {
            logger.info("Token verified for path: {}", httpRequest.getServletPath());
            chain.doFilter(request, response);
        } else {
            sendUnauthorizedResponse(httpResponse, "Unauthorized: Invalid or expired token.");
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        logger.warn("Token verification failed: {}", message);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic can be placed here
    }

    @Override
    public void destroy() {
        // Cleanup logic can be placed here
    }
}
