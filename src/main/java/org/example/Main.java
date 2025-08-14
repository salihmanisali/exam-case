package org.example;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.example.dao.*;
import org.example.handlers.ExamHandler;
import org.example.handlers.LoginHandler;
import org.example.handlers.SubmitHandler;
import org.example.handlers.TokenVerificationFilter;
import org.example.services.*;
import org.example.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumSet;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static Server server;

    public static void startServer() throws Exception {
        logger.info("Starting Online Exam System Backend...");

        // Initialize Database
        DatabaseConnection.initializeDatabase();

        // =================================================================
        //  Composition Root: Create and wire all application components
        // =================================================================

        // 1. Create DAO Instances
        IUserDAO userDAO = new UserDAO();
        IQuestionDAO questionDAO = new QuestionDAO();
        IUserAnswerDAO userAnswerDAO = new UserAnswerDAO();
        IAnswersDAO answersDAO = new AnswersDAO(userAnswerDAO);
        IExamDAO examDAO = new ExamDAO(questionDAO);

        // 2. Create Service Instances and Inject DAOs
        AuthService authService = new AuthServiceImpl(userDAO);
        ExamService examService = new ExamServiceImpl(examDAO);
        SubmitService submitService = new SubmitServiceImpl(examDAO, answersDAO);

        // 3. Create Handler and Filter Instances and Inject Services
        LoginHandler loginHandler = new LoginHandler(authService);
        ExamHandler examHandler = new ExamHandler(examService);
        SubmitHandler submitHandler = new SubmitHandler(submitService);
        TokenVerificationFilter tokenFilter = new TokenVerificationFilter(authService);

        // =================================================================
        //  Configure and Start Jetty Server
        // =================================================================

        server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Register Filter
        context.addFilter(new FilterHolder(tokenFilter), "/*", EnumSet.of(DispatcherType.REQUEST));

        // Register Handlers (Servlets)
        context.addServlet(new ServletHolder(loginHandler), "/login");
        context.addServlet(new ServletHolder(examHandler), "/exams/*");
        context.addServlet(new ServletHolder(submitHandler), "/submit");

        // Add a root handler for basic connectivity check
        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.setContentType("text/html");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println("<h1>Online Exam System Backend is Running!</h1>");
            }
        }), "/");

        server.start();
        logger.info("Jetty server started on port 8080.");
    }

    public static void stopServer() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
            logger.info("Jetty server stopped.");
        }
    }

    public static void main(String[] args) throws Exception {
        startServer();
        server.join();
    }
}
