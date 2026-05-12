package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.model.Category;
import com.purchasehistorysystem.service.CategoryService;
import com.purchasehistorysystem.service.PurchaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AddPurchaseController {
    @FXML private TextField nameField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextField priceField;
    @FXML private TextField amountField;
    @FXML private Label errorLabel;

    private final PurchaseService purchaseService = new PurchaseService();
    private final CategoryService categoryService = new CategoryService();

    private void loadCategories() {
        try {
            categoryComboBox.getItems().clear();

            List<Category> categories = categoryService.getCategoriesForSelection();

            categoryComboBox.getItems().addAll(categories);
        }

        catch (SQLException exception) {
            errorLabel.setText("Помилка при завантаженні категорій");
        }
    }

    @FXML public void initialize() {
        loadCategories();
    }

    @FXML public void onCancelButton() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    @FXML public void onSaveButton() {
        clearFieldsStyle();

        String name = nameField.getText().trim();
        Category category = categoryComboBox.getValue();
        String priceString = priceField.getText().trim();
        String amountString = amountField.getText().trim();

        double price;
        int amount;

        if (name == null || name.isBlank() || priceString.isBlank() || amountString.isBlank()) {
            errorLabel.setText("Заповніть всі поля та оберіть категорію");
            fillAllFieldsError();
            return;
        }

        try {
             price = Double.parseDouble(priceString);
             amount = Integer.parseInt(amountString);
        }

        catch (NumberFormatException exception) {
            errorLabel.setText("Вартість та кількість мають бути числами");
            invalidPriceAndAmount();
            return;
        }

        try {
            purchaseService.addPurchase(name, category, price, amount);
            onCancelButton();
        }

        catch (SQLException exception) {
            errorLabel.setText("Помилка при збереженні покупки");
        }

        catch (IllegalArgumentException exception) {
            String errorMessage = exception.getMessage();
            errorLabel.setText(errorMessage);

            if (errorMessage.contains("Заповніть всі поля та оберіть категорію")) {
                fillAllFieldsError();
            }

            if (errorMessage.contains("Вартість та кількість мають бути більшими за нуль")) {
                invalidPriceAndAmount();
            }

            if (errorMessage.contains("Вартість не може перевищувати значення 999999.99")) {
                veryHighPriceError();
            }
        }
    }

    @FXML public void onAddCategoryButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                    "/com/purchasehistorysystem/templates/dialog/AddCategoryDialog.fxml")
            );

            VBox dialogContent = fxmlLoader.load();

            Stage dialogStage = new Stage();
            Scene dialogScene = new Scene(dialogContent);

            dialogStage.setTitle("Створити категорію");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(dialogScene);
            dialogStage.setWidth(500);
            dialogStage.showAndWait();

            loadCategories();
        }

        catch (IOException exception) {
            errorLabel.setText("Помилка під час викриття діалогового вікна");
        }
    }

    @FXML public void onDeleteCategoryButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                    "/com/purchasehistorysystem/templates/dialog/DeleteCategoryDialog.fxml")
            );

            VBox dialogContent = fxmlLoader.load();

            Stage dialogStage = new Stage();
            Scene dialogScene = new Scene(dialogContent);

            dialogStage.setTitle("Видалити категорію");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(dialogScene);
            dialogStage.setWidth(500);
            dialogStage.showAndWait();
        }

        catch (IOException exception) {
            errorLabel.setText("Помилка під час викриття діалогового вікна");
        }
    }

    private void fillAllFieldsError() {
        nameField.getStyleClass().add("error-field");
        priceField.getStyleClass().add("error-field");
        amountField.getStyleClass().add("error-field");
        categoryComboBox.getStyleClass().add("combo-box-error");
    }

    private void invalidPriceAndAmount() {
        priceField.getStyleClass().add("error-field");
        amountField.getStyleClass().add("error-field");
    }

    private void veryHighPriceError() {
        priceField.getStyleClass().add("error-field");
    }

    private void clearFieldsStyle() {
        nameField.getStyleClass().remove("error-field");
        priceField.getStyleClass().remove("error-field");
        amountField.getStyleClass().remove("error-field");
        categoryComboBox.getStyleClass().remove("combo-box-error");
    }
}
