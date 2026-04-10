package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.App;
import com.purchasehistorysystem.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField checkPasswordField;
    @FXML private TextField passwordHintTextField;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML private void onRegisterButton() {
        String username = usernameTextField.getText().trim();
        String password = passwordField.getText();
        String checkPassword = checkPasswordField.getText();
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
            errorLabel.setText(exception.getMessage());
        }

        catch (Exception exception) {
            errorLabel.setText("Сталася помилка на нашій стороні");
        }
    }

    @FXML private void onLoginLink() {
        try {
            App.setRoot("LoginView");
        }
        catch (IOException exception) {
            errorLabel.setText("Помилка при зміні вікна");
        }
    }
}
