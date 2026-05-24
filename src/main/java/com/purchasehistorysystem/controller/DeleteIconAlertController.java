package com.purchasehistorysystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DeleteIconAlertController {
    @FXML private Button cancelButton;

    private boolean confirm = false;

    public boolean isConfirm() {
        return confirm;
    }

    @FXML private void onCancelButton() {
        confirm = false;
        close();
    }

    @FXML private void onDeleteButton() {
        confirm = true;
        close();
    }

    private void close() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
