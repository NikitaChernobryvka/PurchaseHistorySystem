package com.purchasehistorysystem.service;

import com.purchasehistorysystem.model.User;
import com.purchasehistorysystem.repository.UserRepository;
import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Verifyer;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;

import java.sql.SQLException;

public class UserService {
    private final UserRepository userRepository = new UserRepository();

    public void registerUser(String username, String password, String checkPassword, String email) throws  SQLException, IllegalArgumentException {
        if (username.isBlank() || password.isBlank() || checkPassword.isBlank() || email.isBlank()) {
            throw new IllegalArgumentException("Заповніть усі поля");
        }

        if (username.length() < 4) {
            throw new IllegalArgumentException("Ім'я користувача не має бути меншим за 4 символи");
        }

        String passwordRegex = "^(?=.*[A-Z])(?=.*[^a-zA-Z]).{8,}$";

        if (!password.matches(passwordRegex)) {
            throw new IllegalArgumentException("Пароль має містити мінімум 8 символів, велику літеру та хоча б одну цифру/символ");
        }

        if (!password.equals(checkPassword)) {
            throw new IllegalArgumentException("Паролі не збігаються");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";

        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Неправильний формат пошти");
        }

        if (userRepository.userDuplicateCheck(username, email)) {
            throw new IllegalArgumentException("Ім'я користувача або пошта вже зайняте");
        }

        String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        User user = new User(0, username, passwordHash, email, null);
        userRepository.saveUser(user);
    }

    public User loginUser(String email, String password) throws SQLException, IllegalArgumentException {
        if (email.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Заповніть усі поля");
        }

        User user = userRepository.getUser(email);

        if (user == null) {
            throw new IllegalArgumentException("Невірний логін або пароль");
        }

        Verifyer verifyer = BCrypt.verifyer();
        Result result = verifyer.verify(password.toCharArray(), user.getPasswordHash().toCharArray());

        if (!result.verified) {
            throw new IllegalArgumentException("Невірний логін або пароль");
        }

        return user;
    }

    public void updateToken(int userId, String authToken) throws SQLException {
        userRepository.updateAuthToken(userId, authToken);
    }

    public User findAuthToken(String authToken) throws SQLException {
        return userRepository.findAuthToken(authToken);
    }
}
