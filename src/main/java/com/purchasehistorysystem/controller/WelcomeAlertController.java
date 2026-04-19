package com.purchasehistorysystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WelcomeAlertController {
    @FXML private Label welcomeLabel;

    public void setWelcomeLabel(String username) {
        welcomeLabel.setText("Вітаємо, " + username);
    }

    @FXML private void onBeginButton() {
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.close();
    }
}
