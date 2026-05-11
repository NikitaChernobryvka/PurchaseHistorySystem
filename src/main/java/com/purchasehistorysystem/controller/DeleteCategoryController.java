package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.model.Category;
import com.purchasehistorysystem.service.CategoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeleteCategoryController {
    @FXML private ComboBox<Category> deleteCategoryComboBox;
    @FXML private ComboBox<String> newCategoryComboBox;
    @FXML private Label errorLabel;

    private final CategoryService categoryService = new CategoryService();
    private ObservableList<Category> observableCategories;

    private void loadCategories() {
        try {
            List<Category> categories = categoryService.getCustomCategories();

            observableCategories = FXCollections.observableArrayList(categories);

            deleteCategoryComboBox.setItems(observableCategories);

            newCategoryComboBox.getItems().add("Оберіть категорію для заміни");
            newCategoryComboBox.getSelectionModel().selectFirst();
        }

        catch (SQLException exception) {
            errorLabel.setText("Помилка при заповненні випадаючого списку");
        }
    }

    private void updateNewCategoryComboBox(Category selectedToDelete) {
        newCategoryComboBox.getItems().clear();
        newCategoryComboBox.getItems().add("Оберіть категорію для заміни");

        List<String> categoryNames = new ArrayList<>();

        for (Category category : observableCategories) {
            if (category.getId() != selectedToDelete.getId()) {
                categoryNames.add(category.getName());
            }
        }

        newCategoryComboBox.getItems().addAll(categoryNames);
        newCategoryComboBox.getSelectionModel().selectFirst();
    }

    @FXML public void initialize() {
        loadCategories();

        deleteCategoryComboBox.valueProperty().addListener((obsValue, oldValue, newValue) -> {
            if (newValue != null) {
                updateNewCategoryComboBox(newValue);
            }
        });
    }

    @FXML public void onConfirmDeleteButton() {
        clearStyles();

        try {
            Category deleteCategory = deleteCategoryComboBox.getValue();
            String newCategoryName = newCategoryComboBox.getValue();

            if (deleteCategory == null) {
                errorLabel.setText("Оберіть категорію для видалення");
                chooseDeleteError();
                return;
            }

            if (newCategoryName == null || newCategoryName.isEmpty() || newCategoryName.equals("Оберіть категорію для заміни")) {
                errorLabel.setText("Оберіть іконку для заміни");
                chooseChangeError();
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/purchasehistorysystem/components/alert/DeleteCategoryAlert.fxml"));

            Parent root = fxmlLoader.load();

            DeleteCategoryAlertController deleteCategoryAlertController = fxmlLoader.getController();

            Stage deleteCategoryStage = new Stage();
            Scene scene = new Scene(root);
            deleteCategoryStage.setScene(scene);
            deleteCategoryStage.initModality(Modality.WINDOW_MODAL);
            deleteCategoryStage.initOwner(deleteCategoryComboBox.getScene().getWindow());
            deleteCategoryStage.setTitle("Видалення категорії");

            deleteCategoryStage.showAndWait();

            if (deleteCategoryAlertController.isConfirm()) {
                Integer newCategoryId = null;

                for (Category category : observableCategories) {
                    if (category.getName().equals(newCategoryName)) {
                        newCategoryId = category.getId();
                        break;
                    }
                }


                categoryService.deleteCategory(deleteCategory.getId(), newCategoryId);
                close();
            }
        }
        catch (IOException exception) {
            errorLabel.setText("Помилка при відкритті вікна");
        }
        catch (SQLException exception) {
            errorLabel.setText("Помилка при видаленні категорії");
        }
    }

    private void close() {
        Stage stage = (Stage) deleteCategoryComboBox.getScene().getWindow();
        stage.close();
    }

    @FXML public void onCancelButton() {
        close();
    }

    private void chooseDeleteError() {
        deleteCategoryComboBox.getStyleClass().add("combo-box-error");
    }

    private void chooseChangeError() {
        newCategoryComboBox.getStyleClass().add("combo-box-error");
    }

    private void clearStyles() {
        deleteCategoryComboBox.getStyleClass().remove("combo-box-error");
        newCategoryComboBox.getStyleClass().remove("combo-box-error");
    }
}
