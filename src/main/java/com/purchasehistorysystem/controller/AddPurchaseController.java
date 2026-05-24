package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.model.Category;
import com.purchasehistorysystem.service.CategoryService;
import com.purchasehistorysystem.service.PurchaseService;
import com.purchasehistorysystem.util.TaskExecutor;
import javafx.concurrent.Task;
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
        loadCategories(null);
    }

    private void loadCategories(Integer selectedCategoryId) {
        Task<List<Category>> task = new Task<>() {
            @Override
            protected List<Category> call() throws SQLException {
                return categoryService.getCategoriesForSelection();
            }
        };

        task.setOnSucceeded(event -> {
            categoryComboBox.getItems().clear();
            categoryComboBox.getItems().addAll(task.getValue());

            if (selectedCategoryId != null) {
                boolean found = false;
                for (Category category : categoryComboBox.getItems()) {
                    if (category.getId() == selectedCategoryId) {
                        categoryComboBox.setValue(category);
                        found = true;
                        break;
                    }
                }

                if (!found && !categoryComboBox.getItems().isEmpty()) {
                    Category firstItem = categoryComboBox.getItems().getFirst();
                    categoryComboBox.setValue(firstItem);
                }
            }
        });

        task.setOnFailed(event -> {
            errorLabel.setText("Помилка при завантаженні категорій");
        });

        TaskExecutor.getPool().submit(task);
    }

    @FXML public void initialize() {
        loadCategories();
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(",")) {
                String correctValue = newValue.replace(",", ".");
                priceField.setText(correctValue);
            }
        });
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
        }

        catch (NumberFormatException exception) {
            errorLabel.setText("Вартість має бути числом");
            priceError();
            return;
        }

        try {
            amount = Integer.parseInt(amountString);
        }

        catch (NumberFormatException exception) {
            errorLabel.setText("Кількість має бути цілим числом");
            amountError();
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                purchaseService.addPurchase(name, category, price, amount);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            onCancelButton();
        });

        task.setOnFailed(event -> {
            if (task.getException() instanceof IllegalArgumentException) {
                String errorMessage = task.getException().getMessage();
                errorLabel.setText(errorMessage);

                if (errorMessage.contains("Заповніть всі поля та оберіть категорію")) {
                    fillAllFieldsError();
                }

                if (errorMessage.contains("Вартість та кількість мають бути більшими за нуль")) {
                    invalidPriceAndAmount();
                }

                if (errorMessage.contains("Вартість не може перевищувати значення 999999.99")) {
                    priceError();
                }

                if (errorMessage.contains("Кількість має бути в межах від 0 до 1000")) {
                    amountError();
                }
            }

            else {
                errorLabel.setText("Помилка при збереженні покупки");
            }
        });

        TaskExecutor.getPool().submit(task);
    }

    @FXML public void onAddCategoryButton() {
        try {
            Category selectedCategory = categoryComboBox.getValue();
            Integer selectedCategoryId = selectedCategory != null ? selectedCategory.getId() : null;

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                    "/com/purchasehistorysystem/templates/dialog/AddCategoryDialog.fxml")
            );

            VBox dialogContent = fxmlLoader.load();

            Stage dialogStage = new Stage();
            Scene dialogScene = new Scene(dialogContent);

            dialogStage.setTitle("Створити категорію");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(dialogScene);
            dialogStage.setWidth(350);
            
            dialogStage.showAndWait();

            loadCategories(selectedCategoryId);
        }

        catch (IOException exception) {
            errorLabel.setText("Помилка під час викриття діалогового вікна");
        }
    }

    @FXML public void onDeleteCategoryButton() {
        try {
            Category selectedCategory = categoryComboBox.getValue();
            Integer selectedCategoryId = selectedCategory != null ? selectedCategory.getId() : null;

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                    "/com/purchasehistorysystem/templates/dialog/DeleteCategoryDialog.fxml")
            );

            VBox dialogContent = fxmlLoader.load();

            Stage dialogStage = new Stage();
            Scene dialogScene = new Scene(dialogContent);

            dialogStage.setTitle("Видалити категорію");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(dialogScene);
            dialogStage.setWidth(525);
            dialogStage.showAndWait();

            loadCategories(selectedCategoryId);
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

    private void priceError() {
        priceField.getStyleClass().add("error-field");
    }

    private void amountError() {
        amountField.getStyleClass().add("error-field");
    }

    private void clearFieldsStyle() {
        nameField.getStyleClass().remove("error-field");
        priceField.getStyleClass().remove("error-field");
        amountField.getStyleClass().remove("error-field");
        categoryComboBox.getStyleClass().remove("combo-box-error");
    }
}
