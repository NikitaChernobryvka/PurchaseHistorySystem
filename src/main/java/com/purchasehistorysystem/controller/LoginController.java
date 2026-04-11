package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.App;
import com.purchasehistorysystem.model.User;
import com.purchasehistorysystem.service.UserService;
import com.purchasehistorysystem.util.AuthUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginController {
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private TextField passwordTextField;
    @FXML private ToggleButton showPasswordToggle;

    private final UserService userService = new UserService();

    @FXML private void onLoginButton() {
        clearFieldsStyles();

        String username = usernameTextField.getText().trim();
        String password = showPasswordToggle.isSelected() ? passwordTextField.getText() : passwordField.getText();

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
            String errorMessage = exception.getMessage();
            errorLabel.setText(errorMessage);

            if (errorMessage.contains("Заповніть усі поля")) {
                fillAllFieldsError();
            }

            if (errorMessage.contains("Невірний логін або пароль")) {
                invalidLoginOrPassword();
            }
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

    private void fillAllFieldsError() {
        usernameTextField.getStyleClass().add("error-field");
        passwordField.getStyleClass().add("error-field");
        passwordTextField.getStyleClass().add("error-field");
    }

    private void invalidLoginOrPassword() {
        usernameTextField.getStyleClass().add("error-field");
        passwordField.getStyleClass().add("error-field");
        passwordTextField.getStyleClass().add("error-field");
    }

    private void clearFieldsStyles() {
        usernameTextField.getStyleClass().remove("error-field");
        passwordField.getStyleClass().remove("error-field");
        passwordTextField.getStyleClass().remove("error-field");
    }

    @FXML private void onPasswordToggleButton() {
        boolean selected = showPasswordToggle.isSelected();

        AuthUtils.visibilityControl(passwordField, passwordTextField, selected);
    }

    @FXML private void onLinkPasswordHint() {
        errorLabel.setText("");
        usernameTextField.getStyleClass().remove("error-field");
        passwordField.getStyleClass().remove("error-field");
        passwordTextField.getStyleClass().remove("error-field");

        String username = usernameTextField.getText().trim();

        if (username.isEmpty()) {

            errorLabel.setText("Введіть ім'якористувача, щоб знайти вашу підказку");
            usernameTextField.getStyleClass().add("error-field");
            return;
        }

        try {
            String hint = userService.getPasswordHint(username);

            if (hint == null) {
                errorLabel.setText("Невірний логін або пароль");
                return;
            }
            showPasswordHintAlert(hint);
        }

        catch (SQLException exception) {
            errorLabel.setText("Помилка отриманні підказки");
        }
        catch (IllegalArgumentException exception) {
            errorLabel.setText(exception.getMessage());
            usernameTextField.getStyleClass().add("error-field");
            passwordField.getStyleClass().add("error-field");
        }
    }

    private void showPasswordHintAlert(String hint) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/purchasehistorysystem/components/alert/PasswordHintAlert.fxml"));
            Parent root = fxmlLoader.load();

            PasswordHintController passwordHintController = fxmlLoader.getController();
            passwordHintController.setHintText(hint);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(usernameTextField.getScene().getWindow());
            stage.setTitle("Підказка");
            stage.show();
        }

        catch (IOException exception) {
            errorLabel.setText("Помилка під час відображення підказки");
        }
    }
}
