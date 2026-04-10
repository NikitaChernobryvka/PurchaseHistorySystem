package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.App;
import com.purchasehistorysystem.model.User;
import com.purchasehistorysystem.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML private void onLoginButton() {
        String username = usernameTextField.getText().trim();
        String password = passwordField.getText();

        try {
            User user = userService.loginUser(username, password);
            App.setRoot("MainView");
        }
        catch (SQLException exception) {
            errorLabel.setText("Помилка під час авторизації");
        }

        catch (IOException exception) {
            errorLabel.setText("Помилка при зміні вікна");
        }

        catch (IllegalArgumentException exception) {
            errorLabel.setText(exception.getMessage());
        }

        catch (Exception exception) {
            errorLabel.setText("Помилка сталася на нашій стороні");
        }
    }

    @FXML private void onRegisterLink() {
        try {
            App.setRoot("RegisterView");
        }
        catch (IOException exception) {
            errorLabel.setText("Помилка при зміні вікна");
        }
    }
}
