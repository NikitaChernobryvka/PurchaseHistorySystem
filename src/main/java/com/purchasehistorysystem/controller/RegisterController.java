package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.App;
import com.purchasehistorysystem.service.UserService;
import com.purchasehistorysystem.util.AuthUtils;
import com.purchasehistorysystem.util.TaskExecutor;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegisterController {
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField checkPasswordField;
    @FXML private TextField emailTextField;
    @FXML private Label errorLabel;
    @FXML private TextField passwordTextField;
    @FXML private TextField checkPasswordTextField;
    @FXML private ToggleButton showPasswordToggle;
    @FXML private ToggleButton showCheckPasswordToggle;


    private final UserService userService = new UserService();

    @FXML private void onRegisterButton() {
        clearFieldsStyles();

        String username = usernameTextField.getText().trim();
        String password = showPasswordToggle.isSelected() ? passwordTextField.getText() : passwordField.getText();
        String checkPassword = showCheckPasswordToggle.isSelected() ? checkPasswordTextField.getText() : checkPasswordField.getText();
        String email = emailTextField.getText();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                userService.registerUser(username, password, checkPassword, email);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            try {
                App.setRoot("LoginView");
            }

            catch (IOException exception) {
                errorLabel.setText("Помилка при зміні вікна");
            }
        });

        task.setOnFailed(event -> {
            if (task.getException() instanceof IllegalArgumentException) {
                String errorMessage = task.getException().getMessage();
                errorLabel.setText(errorMessage);

                if (errorMessage.contains("Заповніть усі поля")) {
                    fillAllFieldsError();
                }

                if (errorMessage.contains("Ім'я користувача не має бути меншим за 4 символи")) {
                    usernameError();
                }

                if (errorMessage.contains("Ім'я користувача не має бути більшим за 30 символів")) {
                    usernameError();
                }

                if (errorMessage.contains("Неправильний формат пошти")) {
                    emailError();
                }

                if (errorMessage.contains("Ім'я користувача або пошта вже зайняте")) {
                    usernameOrEmailTakenError();
                }

                if (errorMessage.contains("Пароль має містити мінімум 8 символів, велику літеру та хоча б одну цифру/символ")) {
                    incorrectPasswordError();
                }

                if (errorMessage.contains("Паролі не збігаються")) {
                    passwordMismatchError();
                }
            }

            else {
                errorLabel.setText("Помилка при реєстрації");
            }
        });

        TaskExecutor.getPool().submit(task);
    }

    @FXML private void onLoginLink() {
        clearFieldsStyles();

        try {
            App.setRoot("LoginView");
        }
        catch (IOException exception) {
            errorLabel.setText("Помилка при зміні вікна");
        }
    }

    private void fillAllFieldsError() {
        if (usernameTextField.getText().trim().isBlank()) {
            usernameTextField.getStyleClass().add("error-field");
        }

        if (emailTextField.getText().trim().isBlank()) {
            emailTextField.getStyleClass().add("error-field");
        }

        String password = showPasswordToggle.isSelected() ? passwordTextField.getText() : passwordField.getText();
        if (password.isBlank()) {
            passwordField.getStyleClass().add("error-field");
            passwordTextField.getStyleClass().add("error-field");
        }

        String checkPassword = showCheckPasswordToggle.isSelected() ? checkPasswordTextField.getText() : checkPasswordField.getText();
        if (checkPassword.isBlank()) {
            checkPasswordField.getStyleClass().add("error-field");
            checkPasswordTextField.getStyleClass().add("error-field");
        }
    }

    private void usernameError() {
        usernameTextField.getStyleClass().add("error-field");
    }

    private void emailError() {
        emailTextField.getStyleClass().add("error-field");
    }

    private void usernameOrEmailTakenError() {
        usernameTextField.getStyleClass().add("error-field");
        emailTextField.getStyleClass().add("error-field");
    }

    private void incorrectPasswordError() {
        passwordField.getStyleClass().add("error-field");
        passwordTextField.getStyleClass().add("error-field");

    }

    private void passwordMismatchError() {
        passwordField.getStyleClass().add("error-field");
        checkPasswordField.getStyleClass().add("error-field");

        passwordTextField.getStyleClass().add("error-field");
        checkPasswordTextField.getStyleClass().add("error-field");
    }

    private void clearFieldsStyles() {
        List<TextField> textFieldList = new ArrayList<>();

        textFieldList.add(usernameTextField);

        textFieldList.add(passwordField);
        textFieldList.add(passwordTextField);

        textFieldList.add(checkPasswordField);
        textFieldList.add(checkPasswordTextField);

        textFieldList.add(emailTextField);

        AuthUtils.clearFieldsStyles(textFieldList, "error-field");
    }

    @FXML private void onPasswordToggleButton() {
        boolean selected = showPasswordToggle.isSelected();

        AuthUtils.visibilityControl(passwordField, passwordTextField, selected);

    }

    @FXML private void onCheckPasswordToggleButton() {
        boolean selected = showCheckPasswordToggle.isSelected();

        AuthUtils.visibilityControl(checkPasswordField, checkPasswordTextField, selected);
    }
}
