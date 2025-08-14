package org.example.dao;

import org.example.models.User;

import java.util.List;

public interface IUserDAO {
    void addUser(User user);
    User getUserById(int id);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(int id);
}
