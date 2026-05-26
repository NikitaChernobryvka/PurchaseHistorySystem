package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.service.CategoryService;
import com.purchasehistorysystem.util.TaskExecutor;
import com.purchasehistorysystem.util.UserSessionUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.sql.SQLException;

public class AddCategoryController {
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryIconComboBox;
    @FXML private Label errorLabel;

    private final CategoryService categoryService = new CategoryService();

    private String getIconsDir() {
        int userId = UserSessionUtils.getCurrentUser().getId();
        return "src/main/resources/com/purchasehistorysystem/icons/custom/user_" + userId + "/";
    }

    private void loadIconsFromFolder(String folderPath) {
        File folder = new File(folderPath);
        categoryIconComboBox.getItems().clear();

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    categoryIconComboBox.getItems().add(file.getName());
                }
            }
        }
    }

    @FXML public void initialize() {
        String iconsDir = getIconsDir();
        loadIconsFromFolder(iconsDir);
        if (!categoryIconComboBox.getItems().isEmpty()) {
           categoryIconComboBox.getSelectionModel().selectFirst();
        }
    }

    private void copyFile(File source, File direction) {
        File dir = direction.getParentFile();

        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }

        try (
                InputStream inputStream = new FileInputStream(source);
                OutputStream outputStream = new FileOutputStream(direction);
                ) {
            inputStream.transferTo(outputStream);

            String iconsDir = getIconsDir();
            loadIconsFromFolder(iconsDir);
            categoryIconComboBox.setValue(source.getName());
        }

        catch (IOException exception) {
            errorLabel.setText("Помилка під час копіювання файлу");
        }
    }

    @FXML private void onDownloadIconButton() {
        clearFieldsStyle();
        errorLabel.setText("");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Оберіть іконку (PNG, JPG або JPEG)");

        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG файли", "*.png");
        FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("JPG/JPEG файли", "*.jpg", "*.jpeg");
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("Всі файли", "*.png", "*.jpg", "*.jpeg");

        fileChooser.getExtensionFilters().add(pngFilter);
        fileChooser.getExtensionFilters().add(jpgFilter);
        fileChooser.getExtensionFilters().add(allFilter);

        File iconSource = fileChooser.showOpenDialog(nameField.getScene().getWindow());

        if (iconSource != null) {
            String iconsDir = getIconsDir();
            File iconDirection = new File(iconsDir, iconSource.getName());
            copyFile(iconSource, iconDirection);
        }
    }

    @FXML public void onCancelButton() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    @FXML public void onSaveButton() {
        clearFieldsStyle();

        String name = nameField.getText().trim();
        String selectedIcon = categoryIconComboBox.getValue();

        if (name.isEmpty() || selectedIcon == null) {
            errorLabel.setText("Введіть назву категорії та оберіть іконку");
            fillAllFieldsError();
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                categoryService.addCustomCategory(name, selectedIcon);
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
                fillNameFieldError();
            }

            else {
                errorLabel.setText("Помилка при збереженні нової категорії");
            }
        });

        TaskExecutor.getPool().submit(task);
    }

    @FXML public void onDeleteIconButton() {
        clearFieldsStyle();



        int userId = UserSessionUtils.getCurrentUser().getId();
        String selectedIcon = categoryIconComboBox.getValue();

        if (selectedIcon == null || selectedIcon.isEmpty()) {
            errorLabel.setText("Іконку для видалення не обрано");
            noIconSelected();
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/purchasehistorysystem/components/alert/DeleteIconAlert.fxml"));

            Parent root = fxmlLoader.load();

            DeleteIconAlertController deleteIconAlertController = fxmlLoader.getController();

            Stage deleteIconStage = new Stage();
            Scene scene = new Scene(root);
            deleteIconStage.setScene(scene);
            deleteIconStage.initModality(Modality.WINDOW_MODAL);
            deleteIconStage.initOwner(nameField.getScene().getWindow());
            deleteIconStage.setTitle("Видалення іконки");

            deleteIconStage.showAndWait();

            if (!deleteIconAlertController.isConfirm()) {
                errorLabel.setText("");
                return;
            }
        }

        catch (IOException exception) {
            errorLabel.setText("Помилка при відкритті вікна");
            return;
        }

        String iconsDir = getIconsDir();

        File deleteFile = new File(iconsDir, selectedIcon);

        if (deleteFile.exists()) {
            boolean deleted = deleteFile.delete();

            if (deleted) {
                String fullPath = "/com/purchasehistorysystem/icons/custom/user_" + userId + "/" + selectedIcon;
                String newIconPath = "/com/purchasehistorysystem/icons/default/DeletedIcon.png";

                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws SQLException {
                        categoryService.updateDeletedIcon(fullPath, newIconPath);
                        return null;
                    }
                };

                task.setOnSucceeded(event -> {
                    loadIconsFromFolder(iconsDir);

                    if (!categoryIconComboBox.getItems().isEmpty()) {
                        categoryIconComboBox.getSelectionModel().selectFirst();
                    }

                    else {
                        categoryIconComboBox.setValue(null);
                    }
                });

                task.setOnFailed(event -> {
                    errorLabel.setText("Помилка при оновленні шляхів після видалення іконки");
                });

                TaskExecutor.getPool().submit(task);
            }

            else {
                errorLabel.setText("Не вдалося видалити файл");
            }
        }
    }

    private void fillAllFieldsError() {
        nameField.getStyleClass().add("error-field");
        categoryIconComboBox.getStyleClass().add("combo-box-error");
    }

    private void fillNameFieldError() {
        nameField.getStyleClass().add("error-field");
    }

    private void clearFieldsStyle() {
        nameField.getStyleClass().remove("error-field");
        categoryIconComboBox.getStyleClass().remove("combo-box-error");
    }

    private void noIconSelected() {
        categoryIconComboBox.getStyleClass().add("combo-box-error");
    }
}
