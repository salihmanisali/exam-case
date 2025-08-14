package org.example.services;

import java.util.Optional;

public interface AuthService {
    /**
     * Authenticates a user based on username and password.
     * @param username The user's username.
     * @param password The user's plain text password.
     * @return An Optional containing a session token if authentication is successful, otherwise an empty Optional.
     */
    Optional<String> login(String username, String password);

    /**
     * Verifies if a session token is valid.
     * @param token The session token to verify.
     * @return true if the token is valid, false otherwise.
     */
    boolean verifyToken(String token);
}
