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
        if (username.isBlank() || password.isBlank() || checkPassword.isBlank() || passwordHint.isBlank()) {
            throw new IllegalArgumentException("Заповніть усі поля");
        }

        if (userRepository.usernameDuplicateCheck(username)) {
            throw new IllegalArgumentException("Дане ім'я користувача вже зайняте");
        }

        String passwordRegex = "^(?=.*[A-Z])(?=.*[^a-zA-Z]).{8,}$";

        if (!password.matches(passwordRegex)) {
            throw new IllegalArgumentException("Пароль має містити мінімум 8 символів, велику літеру та хоча б одну цифру/символ");
        }

        if (!password.equals(checkPassword)) {
            throw new IllegalArgumentException("Паролі не збігаються");
        }

        String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        User user = new User(0, username, passwordHash, passwordHint);
        userRepository.saveUser(user);
    }

    public User loginUser(String username, String password) throws SQLException, IllegalArgumentException {
        if (username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Заповніть усі поля");
        }

        User user = userRepository.getUser(username);

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

    public String getPasswordHint(String username) throws SQLException {
        User user = userRepository.getUser(username);

        if (user == null) {
            throw new IllegalArgumentException("Невірний логін або пароль");
        }
        return user.getPasswordHint();
    }
}
