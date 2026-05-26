package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.App;
import com.purchasehistorysystem.model.Category;
import com.purchasehistorysystem.model.Purchase;
import com.purchasehistorysystem.service.CategoryService;
import com.purchasehistorysystem.service.PurchaseService;
import com.purchasehistorysystem.service.UserService;
import com.purchasehistorysystem.util.AuthTokenStorage;
import com.purchasehistorysystem.util.TaskExecutor;
import com.purchasehistorysystem.util.UserSessionUtils;
import javafx.concurrent.Task;
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
    @FXML private ScrollPane purchaseScrollPane;
    @FXML private Label emptyPlaceholder;
    @FXML private Button clearFromDateButton;
    @FXML private Button clearToDateButton;

    private final List<CheckBox> checkBoxes = new ArrayList<>();

    private  final PurchaseService purchaseService = new PurchaseService();
    private final CategoryService categoryService = new CategoryService();
    private final UserService userService = new UserService();

    private boolean filterUpdating = false;

    private void loadCategories() {
        loadCategories(null);
    }

    private void loadCategories(Integer selectedCategoryId) {
        Task<List<Category>> task = new Task<>() {
            @Override
            protected List<Category> call() throws SQLException {
                return categoryService.getCustomCategories();
            }
        };

        task.setOnSucceeded(event -> {
            filterUpdating = true;

            categoryFilter.getItems().clear();
            categoryFilter.getItems().addAll(task.getValue());

            if (selectedCategoryId != null) {
                for (Category category : categoryFilter.getItems()) {
                    if (category.getId() == selectedCategoryId) {
                        categoryFilter.setValue(category);
                        break;
                    }
                }
            }

            filterUpdating = false;
            applyFilters();
        });

        task.setOnFailed(event -> {
            showError("Помилка при отриманні категорій");
        });

        TaskExecutor.getPool().submit(task);
    }

    private void showPurchases(List<Purchase> purchaseList) {
        purchaseHistory.getChildren().clear();
        checkBoxes.clear();

        boolean empty = purchaseList == null || purchaseList.isEmpty();
        emptyPlaceholder.setVisible(empty);
        purchaseScrollPane.setVisible(!empty);

        if (empty) {
            return;
        }

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

                        imageView.setFitHeight(25);
                        imageView.setFitWidth(25);

                        imageView.setPreserveRatio(true);

                        categoryContainer.getChildren().add(imageView);
                    }
                }

                categoryContainer.getChildren().add(categoryLabel);

                Label priceLabel = new Label(String.format("%.2f грн", purchase.getPrice()));
                priceLabel.setPrefWidth(200);

                Label amountLabel = new Label(String.valueOf(purchase.getAmount() + " шт."));
                amountLabel.setPrefWidth(200);

                Label totalPriceLabel = new Label("Всього: " + String.valueOf(purchase.getTotalPrice()) +  " грн.");
                totalPriceLabel.setPrefWidth(200);

                Label dateLabel = new Label(purchase.getDate().toString());
                dateLabel.setPrefWidth(200);

                row.getChildren().addAll(selectPurchase, nameLabel, categoryContainer, priceLabel, amountLabel, totalPriceLabel, dateLabel);

                purchaseHistory.getChildren().add(row);
            }
        }
    }

    @FXML public void initialize() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                int userId = UserSessionUtils.getCurrentUser().getId();
                categoryService.getDefaultCategories(userId);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            loadCategories();
        });

        task.setOnFailed(event -> {
            showError("Помилка при отриманні вбудованих категорій");
        });

        TaskExecutor.getPool().submit(task);

        rangeMinPriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(",")) {
                String correctedValue = newValue.replace(",", ".");
                rangeMinPriceField.setText(correctedValue);
                return;
            }

            usePriceFilter();
        });
        rangeMaxPriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(",")) {
                String correctedValue = newValue.replace(",", ".");
                rangeMaxPriceField.setText(correctedValue);
                return;
            }

            usePriceFilter();
        });

        rangeFromDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            clearFromDateButton.setVisible(newValue != null);
        });

        rangeToDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            clearToDateButton.setVisible(newValue != null);
        });
    }

    @FXML public void onCategoryFilter() {
        if (!filterUpdating) {
            applyFilters();
        }
    }

    @FXML public void onDateFilter() {
        applyFilters();
    }

    private void usePriceFilter() {
        if (!filterUpdating) {
            applyFilters();
        }
    }

    private void applyFilters() {
        try {
            Category selectedCategory = categoryFilter.getValue();
            Integer selectedCategoryId = selectedCategory != null && selectedCategory.getId() != 0 ? selectedCategory.getId() : null;

            LocalDate from = rangeFromDate.getValue();
            LocalDate to = rangeToDate.getValue();

            String minPriceText = rangeMinPriceField.getText().trim();
            String maxPriceText = rangeMaxPriceField.getText().trim();

            double minPrice = minPriceText.isBlank() ? 0 : Double.parseDouble(minPriceText);
            double maxPrice = maxPriceText.isBlank() ? Double.MAX_VALUE : Double.parseDouble(maxPriceText);

            Task<List<Purchase>> task = new Task<>() {
                @Override
                protected List<Purchase> call() throws SQLException {
                    return purchaseService.filter(selectedCategoryId, from, to, minPrice, maxPrice);
                }
            };

            task.setOnSucceeded(event -> {
                showPurchases(task.getValue());
            });

            task.setOnFailed(event -> {
                showError("Помилка при фільтрації");
            });

            TaskExecutor.getPool().submit(task);
        }

        catch (NumberFormatException exception) {
            if (categoryFilter.getScene() != null) {
                filterUpdating = true;
                rangeMaxPriceField.clear();
                rangeMinPriceField.clear();
                filterUpdating = false;
                showError("Введені значення мають бути невід'ємними числами");
            }
        }
    }

    @FXML private void clearFromDate() {
        rangeFromDate.setValue(null);
        clearFromDateButton.setVisible(false);
        onDateFilter();
    }

    @FXML private void clearToDate() {
        rangeToDate.setValue(null);
        clearToDateButton.setVisible(false);
        onDateFilter();
    }

    @FXML public void onAddButton() {
        try {
            Category selectedCategory = categoryFilter.getValue();
            Integer selectedCategoryId = selectedCategory != null ? selectedCategory.getId() : null;

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                    "/com/purchasehistorysystem/templates/dialog/AddPurchaseDialog.fxml")
            );

            VBox dialogContent = fxmlLoader.load();

            Stage dialogStage = new Stage();
            Scene dialogScene = new Scene(dialogContent);

            dialogStage.setTitle("Додати покупку");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setScene(dialogScene);
            dialogStage.setWidth(475);
            dialogStage.showAndWait();

            purchaseHistory.getChildren().clear();
            purchaseScrollPane.setVisible(false);
            emptyPlaceholder.setVisible(false);

            loadCategories(selectedCategoryId);
        }

        catch (IOException exception) {
            showError("Помилка під час відкриття діалогового вікна");
        }
    }

    @FXML public void onDeleteButton() {
        List<Purchase> purchasesToDelete = new ArrayList<>();

        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                purchasesToDelete.add((Purchase) checkBox.getUserData());
            }
        }

        if (purchasesToDelete.isEmpty()) {
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                for (Purchase purchase : purchasesToDelete) {
                    purchaseService.deletePurchase(purchase.getId());
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            applyFilters();
        });

        task.setOnFailed(event -> {
            showError("Помилка при видаленні покупки");
        });

        TaskExecutor.getPool().submit(task);
    }

    @FXML private void onLogoutButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/purchasehistorysystem/components/alert/LogoutAlert.fxml"));
            Parent root = fxmlLoader.load();

            LogoutAlertController logoutAlertController = fxmlLoader.getController();

            Stage logoutStage = new Stage();
            Scene scene = new Scene(root);
            logoutStage.setScene(scene);
            logoutStage.initModality(Modality.WINDOW_MODAL);
            logoutStage.initOwner(categoryFilter.getScene().getWindow());
            logoutStage.setTitle("Вихід із системи");
            logoutStage.setWidth(275);

            logoutStage.showAndWait();

            if (logoutAlertController.isConfirm()) {
                int userId = UserSessionUtils.getCurrentUser().getId();

                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws SQLException {
                        userService.updateToken(userId, null);
                        return null;
                    }
                };

                task.setOnSucceeded(event -> {
                    try {
                        AuthTokenStorage.clearToken();
                    }

                    catch (Exception exception) {
                        showError("Помилка при видаленні токену");
                    }

                    UserSessionUtils.cleanSession();

                    try {
                        App.setRoot("LoginView");
                    }

                    catch (IOException exception) {
                        showError("Помилка при зміні вікна");
                    }
                });

                task.setOnFailed(event -> {
                    UserSessionUtils.cleanSession();

                    try {
                        App.setRoot("LoginView");
                    }

                    catch (IOException exception) {
                        showError("Помилка при зміні вікна");
                    }
                });

                TaskExecutor.getPool().submit(task);
            }
        }

        catch (IOException exception) {
            showError("Помилка під час виходу із системи");
        }

    }

    @FXML private void onLinkToAnalytics() {
        try {
            App.setRoot("AnalyticsView");
        }

        catch (IOException exception) {
            showError("Помилка при зміні вікна");
        }
    }

    private void showError(String message) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/purchasehistorysystem/components/alert/ErrorAlert.fxml"));
            Parent root = fxmlLoader.load();

            ErrorAlertController errorAlertController = fxmlLoader.getController();
            errorAlertController.setErrorMessage(message);

            Stage alertStage = new Stage();
            Scene scene = new Scene(root);

            alertStage.setScene(scene);
            alertStage.initModality(Modality.APPLICATION_MODAL);
            alertStage.initOwner(categoryFilter.getScene().getWindow());
            alertStage.setTitle("Помилка");

            alertStage.showAndWait();
        }

        catch (IOException exception) {
            System.out.println("Критична помилка");
        }
    }

    @FXML public void clearAllFilters() {
        filterUpdating = true;

        if (!categoryFilter.getItems().isEmpty()) {
            categoryFilter.getSelectionModel().selectFirst();
        }

        rangeFromDate.setValue(null);
        rangeToDate.setValue(null);

        clearFromDateButton.setVisible(false);
        clearToDateButton.setVisible(false);

        rangeMinPriceField.clear();
        rangeMaxPriceField.clear();

        filterUpdating = false;

        applyFilters();
    }
}
