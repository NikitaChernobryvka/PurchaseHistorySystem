package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.App;
import com.purchasehistorysystem.model.User;
import com.purchasehistorysystem.service.UserService;
import com.purchasehistorysystem.util.AuthTokenStorage;
import com.purchasehistorysystem.util.AuthUtils;
import com.purchasehistorysystem.util.TaskExecutor;
import com.purchasehistorysystem.util.UserSessionUtils;
import javafx.concurrent.Task;
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

        Task<User> task = new Task<>() {
            @Override
            protected User call() throws SQLException {
                return userService.loginUser(email, password);
            }
        };

        task.setOnSucceeded(event -> {
            User user = task.getValue();

            if (user != null) {
                UserSessionUtils.setCurrentUser(user);

                if (rememberMeCheckBox.isSelected()) {
                    String authToken = UserSessionUtils.generateAuthToken();
                    int userId = user.getId();

                    Task<Void> authTokenTask = new Task<>() {
                        @Override
                        protected Void call() throws SQLException {
                            userService.updateToken(userId, authToken);
                            return null;
                        }
                    };

                    authTokenTask.setOnSucceeded(authTokenEvent -> {
                        try {
                            AuthTokenStorage.saveToken(authToken);

                        }

                        catch (Exception exception) {
                            errorLabel.setText("Не вдалося зберегти токен авторизації");
                        }

                        showWelcomeAlert(user.getUsername());
                    });

                    authTokenTask.setOnFailed(authTokenEvent -> {
                        showWelcomeAlert(user.getUsername());
                    });

                    TaskExecutor.getPool().submit(authTokenTask);
                }

                else {
                    showWelcomeAlert(user.getUsername());
                }
            }
        });

        task.setOnFailed(event -> {
            if (task.getException() instanceof IllegalArgumentException) {
                String errorMessage = task.getException().getMessage();
                errorLabel.setText(errorMessage);

                if (errorMessage.contains("Заповніть усі поля")) {
                    fillAllFieldsError();
                }

                if (errorMessage.contains("Невірний логін або пароль")) {
                    invalidLoginOrPassword();
                }
            }

            else {
                errorLabel.setText("Помилка під час авторизації");
            }
        });

        TaskExecutor.getPool().submit(task);
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

            Stage welcomeAlertStage = new Stage();
            Scene scene = new Scene(root);
            welcomeAlertStage.setResizable(false);
            welcomeAlertStage.setScene(scene);
            welcomeAlertStage.initModality(Modality.APPLICATION_MODAL);
            welcomeAlertStage.initOwner(emailTextField.getScene().getWindow());
            welcomeAlertStage.setTitle("Успішний вхід");

            welcomeAlertStage.showAndWait();

            App.setRoot("MainView");
        }
        catch (IOException exception) {
            errorLabel.setText("Помилка при завантаженні головного вікна");
        }
    }
}
