package com.purchasehistorysystem.service;

import com.purchasehistorysystem.model.User;
import com.purchasehistorysystem.repository.UserRepository;
import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Verifyer;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;

import java.sql.SQLException;

public class UserService {
    private final UserRepository userRepository = new UserRepository();

    public void registerUser(String username, String password, String checkPassword, String passwordHint) throws  SQLException, IllegalArgumentException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Ім'я користувача не може бути порожнім");
        }

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Пароль має бути не меншим за 8 символів");
        }

        if (!password.equals(checkPassword)) {
            throw new IllegalArgumentException("Паролі не збігаються");
        }

        if (passwordHint == null || passwordHint.isBlank()) {
            throw new IllegalArgumentException("Підказка до пароля не може бути порожньою");
        }

        String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        User user = new User(0, username, passwordHash, passwordHint);
        userRepository.saveUser(user);
    }

    public User loginUser(String username, String password) throws SQLException, IllegalArgumentException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Введіть ім'я користувача та пароль");
        }

        User user = userRepository.getUser(username);

        if (user == null) {
            throw new IllegalArgumentException("Користувача не знайдено");
        }

        Verifyer verifyer = BCrypt.verifyer();
        Result result = verifyer.verify(password.toCharArray(), user.getPasswordHash().toCharArray());

        if (!result.verified) {
            throw new IllegalArgumentException("Невірний пароль");
        }

        return user;
    }
}
