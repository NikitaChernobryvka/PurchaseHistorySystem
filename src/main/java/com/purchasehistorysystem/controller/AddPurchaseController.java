package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.model.Category;
import com.purchasehistorysystem.model.Purchase;
import com.purchasehistorysystem.repository.CategoryRepository;
import com.purchasehistorysystem.repository.PurchaseRepository;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AddPurchaseController {
    @FXML private TextField nameField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextField priceField;
    @FXML private TextField amountField;

    private final PurchaseRepository purchaseRepository = new PurchaseRepository();
    private final CategoryRepository categoryRepository = new CategoryRepository();

    private void loadCategories() {
        try {
            List<Category> categoryList = categoryRepository.getAllCategories();

            categoryComboBox.getItems().addAll(categoryList);
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
        LocalDate date = LocalDate.now();

        if (name.isEmpty() || category == null || priceString.isEmpty() || amountString.isEmpty()) {
            System.out.println("Заповність всі поля");
            return;
        }

        try {
            double price = Double.parseDouble(priceString);
            int amount = Integer.parseInt(amountString);

            Purchase purchase = new Purchase(0, name, category, price, amount, date);
            purchaseRepository.addPurchase(purchase);

            onCancelButton();
        }

        catch (SQLException exception) {
            System.out.println("Помилка при збереженні нової покупки");
        }
    }
}
