package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.service.CategoryService;
import com.purchasehistorysystem.util.UserSessionUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.SQLException;

public class AddCategoryController {
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryIconComboBox;

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
            System.out.println("Помилка під час копіювання");
        }
    }

    @FXML private void onDownloadIconButton() {
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
        String name = nameField.getText().trim();
        String selectedIcon = categoryIconComboBox.getValue();

        if (name.isEmpty() || selectedIcon == null) {
            return;
        }

        try {
            categoryService.addCustomCategory(name, selectedIcon);
            onCancelButton();
        }

        catch (SQLException exception) {
            System.out.println("Помилка при збереженні нової категорії");
        }
        catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    @FXML public void onDeleteIconButton() {
        int userId = UserSessionUtils.getCurrentUser().getId();
        String selectedIcon = categoryIconComboBox.getValue();

        if (selectedIcon == null || selectedIcon.isEmpty()) {
            System.out.println("Іконку для видалення не обрано");
            return;
        }

        String iconsDir = getIconsDir();

        File deleteFile = new File(iconsDir, selectedIcon);

        if (deleteFile.exists()) {
            boolean deleted = deleteFile.delete();

            if (deleted) {
                try {
                    String fullPath = "/com/purchasehistorysystem/icons/custom/user_" + userId + "/" + selectedIcon;
                    String newIconPath = "/com/purchasehistorysystem/icons/default/DeletedIcon.png";
                    categoryService.updateDeletedIcon(fullPath, newIconPath);

                    loadIconsFromFolder(iconsDir);

                    if (!categoryIconComboBox.getItems().isEmpty()) {
                        categoryIconComboBox.getSelectionModel().selectFirst();
                    }

                    else {
                        categoryIconComboBox.setValue(null);
                    }
                }

                catch (SQLException exception) {
                    System.out.println("Помилка при оновлені шляхів після видалення іконки");
                }
            }

            else {
                System.out.println("Не вдалося видалити файл");
            }
        }
    }
}
