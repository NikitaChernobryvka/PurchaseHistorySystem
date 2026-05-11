package com.purchasehistorysystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErrorAlertController {
    @FXML private Label errorMessageLabel;

    public void setErrorMessage(String message) {
        errorMessageLabel.setText(message);
    }

    @FXML public void onCloseButton() {
        Stage stage = (Stage) errorMessageLabel.getScene().getWindow();
        stage.close();
    }
}
