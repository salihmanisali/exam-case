# Exam Management System

This project is an exam management system.

## Running the application

To run the application, you can use the following Maven command:

```bash
mvn exec:java
```

## Running the tests

To run the tests, you can use the following Maven command:

```bash
mvn test
```

## Running with Docker

You can also run the application using Docker.

1.  **Build the Docker image:**

    ```bash
    docker build -t exam-system .
    ```

2.  **Run the Docker container:**

    ```bash
    docker run -p 8080:8080 exam-system
    ```

## Available Endpoints

### Authentication

*   **POST /login**

    Authenticates a user and returns a JWT token. Requires Basic Authentication with username and password.

### Exams

*   **GET /exams/{id}**

    Retrieves a specific exam by its ID. Requires a valid JWT token in the `Authorization` header.

*   **POST /submit**

    Submits answers for an exam and returns the score. Requires a valid JWT token in the `Authorization` header.
