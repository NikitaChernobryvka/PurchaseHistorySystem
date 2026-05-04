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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeleteCategoryController {
    @FXML private ComboBox<Category> deleteCategoryComboBox;
    @FXML private ComboBox<String> newCategoryComboBox;

    private final CategoryService categoryService = new CategoryService();
    private ObservableList<Category> observableCategories;

    private void loadCategories() {
        try {
            List<Category> categories = categoryService.getCustomCategories();

            observableCategories = FXCollections.observableArrayList(categories);

            deleteCategoryComboBox.setItems(observableCategories);

            newCategoryComboBox.getItems().add("Не обрано");
            newCategoryComboBox.getSelectionModel().selectFirst();
        }

        catch (SQLException exception) {
            System.err.println("Помилка при заповненні випадаючого списку");
        }
    }

    private void updateNewCategoryComboBox(Category selectedToDelete) {
        newCategoryComboBox.getItems().clear();
        newCategoryComboBox.getItems().add("Не обрано");

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
        try {
            Category deleteCategory = deleteCategoryComboBox.getValue();

            if (deleteCategory == null) {
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/purchasehistorysystem/components/alert/DeleteCategoryAlert.fxml"));

            Parent root = fxmlLoader.load();

            DeleteCategoryAlertController deleteCategoryAlertController = fxmlLoader.getController();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(deleteCategoryComboBox.getScene().getWindow());

            stage.showAndWait();

            if (deleteCategoryAlertController.isConfirm()) {
                String newCategoryName = newCategoryComboBox.getValue();
                Integer newCategoryId = null;

                if (!"Не обрано".equals(newCategoryName)) {
                    for (Category category : observableCategories) {
                        if (category.getName().equals(newCategoryName)) {
                            newCategoryId = category.getId();
                            break;
                        }
                    }
                }

                categoryService.deleteCategory(deleteCategory.getId(), newCategoryId);
                close();
            }
        }
        catch (IOException exception) {
            System.err.println("Помилка при відкритті вікна");
        }
        catch (SQLException exception) {
            System.err.println("Помилка при видаленні категорії");
        }
    }

    private void close() {
        Stage stage = (Stage) deleteCategoryComboBox.getScene().getWindow();
        stage.close();
    }

    @FXML public void onCancelButton() {
        close();
    }
}
