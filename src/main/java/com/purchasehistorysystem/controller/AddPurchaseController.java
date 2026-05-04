package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.model.Category;
import com.purchasehistorysystem.service.CategoryService;
import com.purchasehistorysystem.service.PurchaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
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

    private final PurchaseService purchaseService = new PurchaseService();
    private final CategoryService categoryService = new CategoryService();

    private void loadCategories() {
        try {
            categoryComboBox.getItems().clear();

            List<Category> categories = categoryService.getCustomCategories();

            categoryComboBox.getItems().addAll(categories);
        }

        catch (SQLException exception) {
            System.out.println("Помилка при завантаженні категорій");
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
        String name = nameField.getText().trim();
        Category category = categoryComboBox.getValue();
        String priceString = priceField.getText().trim();
        String amountString = amountField.getText().trim();

        if (name.isEmpty() || category == null || priceString.isEmpty() || amountString.isEmpty()) {
            System.out.println("Заповність всі поля");
            return;
        }

        double price;
        int amount;

        try {
             price = Double.parseDouble(priceString);
             amount = Integer.parseInt(amountString);
        }

        catch (Exception exception) {
            System.out.println("Ціна та кількість мають бути числами");
            return;
        }

        try {
            purchaseService.addPurchase(name, category, price, amount);
            onCancelButton();
        }

        catch (SQLException exception) {
            System.out.println("Помилка при збереженні покупки");
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
            System.out.println("Помилка під час викриття діалогового вікна");
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
            System.out.println("Помилка під час викриття діалогового вікна");
        }
    }
}
