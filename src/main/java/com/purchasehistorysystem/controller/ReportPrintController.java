package com.purchasehistorysystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

public class ReportPrintController {
    @FXML private Label reportDateLabel;
    @FXML private Label reportUserLabel;
    @FXML private ImageView expensesPieChart;
    @FXML private ImageView expensesBarChart;
    @FXML private FlowPane categoryCountList;

    public void setData(WritableImage pieData, WritableImage barData, List<String> categories, String username,
                        String month, int year) {

        expensesPieChart.setImage(pieData);
        expensesBarChart.setImage(barData);

        reportDateLabel.setText("Звіт за: " + month + " " + year);
        reportUserLabel.setText("Користувач: " + username);

        for (String category : categories) {
            Label categoryLabel = new Label(category);
            categoryCountList.getChildren().add(categoryLabel);
        }
    }
}
