package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.App;
import com.purchasehistorysystem.model.User;
import com.purchasehistorysystem.service.UserService;
import com.purchasehistorysystem.util.AuthTokenStorage;
import com.purchasehistorysystem.util.AuthUtils;
import com.purchasehistorysystem.util.UserSessionUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField emailTextField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private TextField passwordTextField;
    @FXML private ToggleButton showPasswordToggle;
    @FXML private CheckBox rememberMeCheckBox;

    private final UserService userService = new UserService();

    @FXML private void onLoginButton() {
        clearFieldsStyles();

        String email = emailTextField.getText().trim();
        String password = showPasswordToggle.isSelected() ? passwordTextField.getText() : passwordField.getText();

        try {
            User user = userService.loginUser(email, password);

            if (user != null) {
                UserSessionUtils.setCurrentUser(user);

                if (rememberMeCheckBox.isSelected()) {
                    String authToken = UserSessionUtils.generateAuthToken();
                    int userId = user.getId();
                    userService.updateToken(userId, authToken);
                    AuthTokenStorage.saveToken(authToken);
                }

                showWelcomeAlert(user.getUsername());

            }
        }
        catch (SQLException exception) {
            errorLabel.setText("Помилка під час авторизації");
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
        emailTextField.getStyleClass().add("error-field");
        passwordField.getStyleClass().add("error-field");
        passwordTextField.getStyleClass().add("error-field");
    }

    private void invalidLoginOrPassword() {
        emailTextField.getStyleClass().add("error-field");
        passwordField.getStyleClass().add("error-field");
        passwordTextField.getStyleClass().add("error-field");
    }

    private void clearFieldsStyles() {
        emailTextField.getStyleClass().remove("error-field");
        passwordField.getStyleClass().remove("error-field");
        passwordTextField.getStyleClass().remove("error-field");
    }

    @FXML private void onPasswordToggleButton() {
        boolean selected = showPasswordToggle.isSelected();

        AuthUtils.visibilityControl(passwordField, passwordTextField, selected);
    }

    private void showWelcomeAlert(String username) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/purchasehistorysystem/components/alert/WelcomeAlert.fxml"));
            Parent root = fxmlLoader.load();

            WelcomeAlertController welcomeAlertController = fxmlLoader.getController();
            welcomeAlertController.setWelcomeLabel(username);

            Stage alertStage = new Stage();
            Scene scene = new Scene(root);
            alertStage.setScene(scene);
            alertStage.initModality(Modality.APPLICATION_MODAL);
            alertStage.initOwner(emailTextField.getScene().getWindow());
            alertStage.setTitle("Успішний вхід");

            alertStage.showAndWait();

            App.setRoot("MainView");
        }
        catch (IOException exception) {
            errorLabel.setText("Помилка при завантаженні головного вікна");
        }
    }
}
