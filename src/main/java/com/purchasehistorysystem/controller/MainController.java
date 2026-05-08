package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.App;
import com.purchasehistorysystem.model.Category;
import com.purchasehistorysystem.model.Purchase;
import com.purchasehistorysystem.service.CategoryService;
import com.purchasehistorysystem.service.PurchaseService;
import com.purchasehistorysystem.util.UserSessionUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

import java.sql.SQLException;


public class MainController {
    @FXML private ComboBox<Category> categoryFilter;
    @FXML private DatePicker rangeFromDate;
    @FXML private DatePicker rangeToDate;
    @FXML private VBox purchaseHistory;
    @FXML private TextField rangeMinPriceField;
    @FXML private TextField rangeMaxPriceField;

    private List<CheckBox> checkBoxes = new ArrayList<>();

    private  final PurchaseService purchaseService = new PurchaseService();
    private final CategoryService categoryService = new CategoryService();

    private void loadCategories() {
        try {
            List<Category> categoryList = categoryService.getCustomCategories();
            categoryFilter.getItems().clear();
            categoryFilter.getItems().addAll(categoryList);
        }

        catch (SQLException exception) {
            System.out.println("Помилка при отриманні категорій");
        }
    }

    private void showPurchases(List<Purchase> purchaseList) {
        purchaseHistory.getChildren().clear();
        checkBoxes.clear();

        Map<String, List<Purchase>> purchasesPerMonth = purchaseService.groupByMonth(purchaseList);

        for (Map.Entry<String, List<Purchase>> entry : purchasesPerMonth.entrySet()) {
            String month = entry.getKey();
            List<Purchase> purchases = entry.getValue();

            double expensesForMonth = purchaseService.calculateExpensesForMonth(purchases);

            HBox monthHeader = new HBox();
            monthHeader.getStyleClass().add("month-header");

            Label monthLabel = new Label(month);
            monthLabel.setPrefWidth(150);

            Label expensesForMonthLabel = new Label(String.format("Всього витрат: " + " %.2f грн", expensesForMonth));

            HBox.setHgrow(monthLabel, Priority.ALWAYS);

            monthHeader.getChildren().addAll(monthLabel, expensesForMonthLabel);
            purchaseHistory.getChildren().add(monthHeader);

            for (Purchase purchase : purchases) {
                HBox row = new HBox();
                row.getStyleClass().add("purchase-row");

                CheckBox selectPurchase = new CheckBox();
                selectPurchase.setUserData(purchase);
                checkBoxes.add(selectPurchase);

                Label nameLabel = new Label(purchase.getName());
                nameLabel.setPrefWidth(200);

                Label categoryLabel = new Label(purchase.getCategory().getName());
                categoryLabel.setPrefWidth(200);

                HBox categoryContainer = new HBox();
                categoryContainer.getStyleClass().add("category-container");

                String path = purchase.getCategory().getIconPath();

                if (path != null && !path.isEmpty()) {
                    Image image = null;

                    InputStream inputStream = getClass().getResourceAsStream(path);

                    if (inputStream != null) {
                        image = new Image(inputStream);
                    }

                    if (image == null) {
                        File source = new File("src/main/resources");
                        String correctPath = path.startsWith("/") ? path.substring(1) : path;

                        File fileOnDisk = new File(source, correctPath);

                        if (fileOnDisk.exists()) {

                            image = new Image(fileOnDisk.toURI().toString());
                        }
                    }

                    if (image != null) {
                        ImageView imageView = new ImageView(image);

                        imageView.setFitHeight(20);
                        imageView.setFitWidth(20);

                        imageView.setPreserveRatio(true);

                        categoryContainer.getChildren().add(imageView);
                    }
                }

                categoryContainer.getChildren().add(categoryLabel);

                Label priceLabel = new Label(String.format("%.2f грн", purchase.getPrice()));
                priceLabel.setPrefWidth(200);

                Label amountLabel = new Label(String.valueOf(purchase.getAmount() + " шт."));
                amountLabel.setPrefWidth(200);

                Label dateLabel = new Label(purchase.getDate().toString());
                dateLabel.setPrefWidth(200);

                row.getChildren().addAll(selectPurchase, nameLabel, categoryContainer, priceLabel, amountLabel, dateLabel);

                purchaseHistory.getChildren().add(row);
            }
        }
    }

    private void loadPurchases() {
        try {
            List<Purchase> purchaseList = purchaseService.getAllPurchases();
            showPurchases(purchaseList);
        }

        catch (SQLException exception) {
            System.out.println("Помилка при отриманні списку покупок");
        }
    }

    @FXML public void initialize() {
        int userId = UserSessionUtils.getCurrentUser().getId();
        categoryService.getDefaultCategories(userId);

        loadCategories();
        loadPurchases();

        rangeMinPriceField.textProperty().addListener((observable, oldValue, newValue) -> usePriceFilter());
        rangeMaxPriceField.textProperty().addListener((observable, oldValue, newValue) -> usePriceFilter());
    }

    @FXML public void onCategoryFilter() {
        Category selectedCategory = categoryFilter.getValue();

        if (selectedCategory == null || selectedCategory.getId() == 0) {
            loadPurchases();
            return;
        }

        try {
            List<Purchase> purchaseList = purchaseService.filterByCategory(selectedCategory.getId());
            showPurchases(purchaseList);
        }

        catch (SQLException exception) {
            System.out.println("Помилка при сортуванні за категорією");
        }
    }

    @FXML public void onDateFilter() {
        LocalDate from = rangeFromDate.getValue();
        LocalDate to = rangeToDate.getValue();

        if (from == null || to == null) {
            loadPurchases();
            return;
        }

        try {
            List<Purchase> purchaseList = purchaseService.filterByDateRange(from, to);
            showPurchases(purchaseList);
        }

        catch (SQLException exception) {
            System.out.println("Помилка при сортуванні за датою");
        }
    }

    @FXML public void onAddButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                    "/com/purchasehistorysystem/templates/dialog/AddPurchaseDialog.fxml")
            );

            VBox dialogContent = fxmlLoader.load();

            Stage dialogStage = new Stage();
            Scene dialogScene = new Scene(dialogContent);

            dialogStage.setTitle("Додати покупку");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(dialogScene);
            dialogStage.setWidth(500);
            dialogStage.showAndWait();

            loadPurchases();
        }

        catch (IOException exception) {
            System.out.println("Помилка під час відкриття діалогового вікна");
        }
    }

    @FXML public void onDeleteButton() {
        boolean deleted = false;

        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                Purchase purchase = (Purchase) checkBox.getUserData();

                try {
                    purchaseService.deletePurchase(purchase.getId());
                    deleted = true;
                }

                catch (SQLException exception) {
                    System.out.println("Помилка при видаленні покупки");
                }
            }
        }

        if (deleted) {
            loadPurchases();
        }
    }

    @FXML private void onLogoutButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/purchasehistorysystem/components/alert/LogoutAlert.fxml"));
            Parent root = fxmlLoader.load();

            LogoutAlertController logoutAlertController = fxmlLoader.getController();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(categoryFilter.getScene().getWindow());

            stage.showAndWait();

            if (logoutAlertController.isConfirm()) {
                UserSessionUtils.cleanSession();
                App.setRoot("LoginView");
            }
        }

        catch (IOException exception) {
            System.out.println("Помилка під час виходу із системи");
        }

    }

    @FXML private void onLinkToAnalytics() {
        try {
            App.setRoot("AnalyticsView");
        }

        catch (IOException exception) {
            System.err.println("Помилка при зміні вікна");
        }
    }

    private void usePriceFilter() {
        try {
            String minPriceText = rangeMinPriceField.getText().trim();
            String maxPriceText = rangeMaxPriceField.getText().trim();

            if (minPriceText.isBlank() && maxPriceText.isBlank()) {
                loadPurchases();
                return;
            }

            double minPrice = minPriceText.isBlank() ? 0 : Double.parseDouble(minPriceText);
            double maxPrice = maxPriceText.isBlank() ? Double.MAX_VALUE : Double.parseDouble(maxPriceText);

            List<Purchase> purchaseList = purchaseService.filterByPriceRange(minPrice, maxPrice);
            showPurchases(purchaseList);

        }

        catch (SQLException exception) {
            System.err.println("Помилка під час фільтрації за ціною");
        }

        catch (NumberFormatException exception) {
            System.err.println("Помилка. Введені значення мають бути числами");
        }
    }
}
