package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.model.Category;
import com.purchasehistorysystem.repository.CategoryRepository;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class AddCategoryController {
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryIconComboBox;

    private final CategoryRepository categoryRepository = new CategoryRepository();

    private final String ICONS_DIR = "src/main/resources/com/purchasehistorysystem/icons/custom/";
    private final String DATABASE_PREFIX = "/com/purchasehistorysystem/icons/custom/";

    private void loadIconsFromFolder(String folderPath) {
        File folder = new File(folderPath);

        categoryIconComboBox.getItems().clear();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                        categoryIconComboBox.getItems().add(file.getName());
                    }
                }
            }
        }
    }

    private void configureIcons() {
        loadIconsFromFolder(ICONS_DIR);

        if (!categoryIconComboBox.getItems().isEmpty()) {
            categoryIconComboBox.getSelectionModel().selectFirst();
        }
    }

    @FXML public void initialize() {
        configureIcons();
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

            loadIconsFromFolder(ICONS_DIR);
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
            File iconDirection = new File(ICONS_DIR, iconSource.getName());
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
            List<Category> categories = categoryRepository.getAllCategories();

            for (Category category : categories) {
                if (category.getName().equalsIgnoreCase(name)) {
                    System.out.println("Така категорія вже існує");
                    return;
                }
            }

            String pathForDatabase = DATABASE_PREFIX + selectedIcon;

            Category category = new Category(0, name, pathForDatabase);
            categoryRepository.addCategory(category);
            onCancelButton();
        }

        catch (SQLException exception) {
            System.out.println("Помилка при збереженні нової категорії");
        }
    }
}
