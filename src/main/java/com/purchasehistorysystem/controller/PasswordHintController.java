package com.purchasehistorysystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PasswordHintController {
    @FXML private Label hintLabel;

    public void setHintText(String hint) {
        hintLabel.setText(hint);
    }

    @FXML private void onCloseButton() {
        Stage stage = (Stage) hintLabel.getScene().getWindow();
        stage.close();
    }
}
