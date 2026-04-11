package com.purchasehistorysystem.util;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.List;

public class AuthUtils {
    public static void clearFieldsStyles(List<TextField> textFieldList, String style) {
        for (TextField textField : textFieldList) {
            textField.getStyleClass().remove(style);
        }
    }

    public static void visibilityControl(PasswordField passwordField, TextField textField, boolean show) {
        if (show) {
            textField.setText(passwordField.getText());
            textField.setVisible(true);
            textField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            textField.requestFocus();
            textField.end();
        }
        else {
            passwordField.setText(textField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            textField.setVisible(false);
            textField.setManaged(false);
            passwordField.requestFocus();
            passwordField.end();
        }
    }
}
