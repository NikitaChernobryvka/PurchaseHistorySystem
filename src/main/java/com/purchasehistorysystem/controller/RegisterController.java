package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.App;
import com.purchasehistorysystem.service.UserService;
import com.purchasehistorysystem.util.AuthUtils;
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
    @FXML private TextField passwordHintTextField;
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
        String passwordHint = passwordHintTextField.getText();

        try {
            userService.registerUser(username, password, checkPassword, passwordHint);
            App.setRoot("LoginView");
        }

        catch (SQLException exception) {
            errorLabel.setText("Помилка при реєстрації");
        }

        catch (IOException exception) {
            errorLabel.setText("Помилка при зміні вікна");
        }

        catch (IllegalArgumentException exception) {
            String errorMessage = exception.getMessage();
            errorLabel.setText(errorMessage);

            if (errorMessage.contains("Заповніть усі поля")) {
                fillAllFieldsError();
            }

            if (errorMessage.contains("Дане ім'я користувача вже зайняте")) {
                usernameTakenError();
            }

            if (errorMessage.contains("Пароль має містити мінімум 8 символів, велику літеру та хоча б одну цифру/символ")) {
                incorrectPasswordError();
            }

            if (errorMessage.contains("Паролі не збігаються")) {
                passwordMismatchError();
            }
        }

        catch (Exception exception) {
            errorLabel.setText("Сталася помилка на нашій стороні");
        }
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

        if (passwordHintTextField.getText().trim().isBlank()) {
            passwordHintTextField.getStyleClass().add("error-field");
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

    private void usernameTakenError() {
        usernameTextField.getStyleClass().add("error-field");
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

        textFieldList.add(passwordHintTextField);

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
