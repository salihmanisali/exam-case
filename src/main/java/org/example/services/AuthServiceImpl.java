package org.example.services;

import org.example.dao.IUserDAO;
import org.example.models.User;
import org.example.util.PasswordUtil;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthServiceImpl implements AuthService {

    private final IUserDAO userDAO;
    private final Map<String, String> activeTokens; // Token -> Username

    public AuthServiceImpl(IUserDAO userDAO) {
        this.userDAO = userDAO;
        this.activeTokens = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<String> login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user != null && PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            String token = UUID.randomUUID().toString();
            activeTokens.put(token, username);
            return Optional.of(token);
        }
        return Optional.empty();
    }

    @Override
    public boolean verifyToken(String token) {
        return activeTokens.containsKey(token);
    }
}
