package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.App;
import com.purchasehistorysystem.repository.AnalyticsRepository;
import com.purchasehistorysystem.util.TaskExecutor;
import com.purchasehistorysystem.util.UserSessionUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

import javafx.scene.chart.PieChart;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnalyticsController {
    @FXML private ComboBox<String> monthComboBox;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private PieChart expensesPieChart;
    @FXML private BarChart<String, Double> expensesBarChart;
    @FXML private ListView<String> categoryCountList;
    @FXML private HBox chartsContainer;
    @FXML private Label emptyPlaceholder;

    private final AnalyticsRepository analyticsRepository = new AnalyticsRepository();

    private void loadMonth() {
        monthComboBox.getItems().clear();
        List<String> months = new ArrayList<>();
        for (Month month : Month.values()) {
            String monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("uk"));
            monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
            months.add(monthName);
        }
        monthComboBox.getItems().addAll(months);
    }

    private void loadYear() {
        yearComboBox.getItems().clear();
        int currentYear = LocalDate.now().getYear();
        List<Integer> years = new ArrayList<>();

        for (int i = currentYear; i >= currentYear - 5; i--) {
            years.add(i);
        }
        yearComboBox.getItems().addAll(years);
    }

    private void loadPieChart(Map<String, Double> data) {
        expensesPieChart.getData().clear();

        for (String categoryName : data.keySet()) {
            Double amount = data.get(categoryName);
            String amountString = String.format("%.2f", amount);
            String dataLabel = categoryName + " (" + amountString + " грн.)";
            PieChart.Data slice = new PieChart.Data(dataLabel, amount);
            expensesPieChart.getData().add(slice);
            expensesPieChart.setLabelsVisible(false);
        }
    }

    private void loadBarChart(LocalDate from, LocalDate to, int userId) {
        Task<Map<LocalDate, Double>> task = new Task<>() {
            @Override
            protected Map<LocalDate, Double> call() throws SQLException {
                return analyticsRepository.getExpensesByDate(from, to, userId);
            }
        };

        task.setOnSucceeded(event -> {
            expensesBarChart.getData().clear();

            Series<String, Double> series = new Series<>();
            series.setName("Витрати по дням");

            for (LocalDate date : task.getValue().keySet()) {
                Double amount = task.getValue().get(date);
                String dayString = String.valueOf(date.getDayOfMonth());

                XYChart.Data<String, Double> dataPoint = new XYChart.Data<>(dayString, amount);

                series.getData().add(dataPoint);
            }

            expensesBarChart.getData().add(series);
        });

        task.setOnFailed(event -> {
            showError("Помилка при завантаженні стовпчастої діаграми");
        });

        TaskExecutor.getPool().submit(task);
    }

    private void loadCategoryList(LocalDate from, LocalDate to, int userId) {
       Task<Map<String, Integer>> task = new Task<>() {
           @Override
           protected Map<String, Integer> call() throws SQLException {
               return analyticsRepository.getCategoryCount(from, to, userId);
           }
       };

       task.setOnSucceeded(event -> {
           categoryCountList.getItems().clear();

           for (String category : task.getValue().keySet()) {
               int categoryCount = task.getValue().get(category);
               String categoryCountString = category + " - " + categoryCount;
               categoryCountList.getItems().add(categoryCountString);
           }
       });

       task.setOnFailed(event -> {
           showError("Помилка при завантаженні списку категорій");
       });

       TaskExecutor.getPool().submit(task);
    }

    private void loadCharts() {
        int monthIndex = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
        Integer year = yearComboBox.getSelectionModel().getSelectedItem();

        if (year == null || monthIndex < 0) {
            return;
        }

        YearMonth yearMonth = YearMonth.of(year, monthIndex);
        LocalDate from = yearMonth.atDay(1);
        LocalDate to = yearMonth.atEndOfMonth();
        int userId = UserSessionUtils.getCurrentUser().getId();

        Task<Map<String, Double>> task = new Task<>() {
            @Override
            protected Map<String, Double> call() throws SQLException {
                return analyticsRepository.getExpensesByCategory(from, to, userId);
            }
        };

        task.setOnSucceeded(event -> {
            Map<String, Double> data = task.getValue();

            if (task.getValue().isEmpty()) {
                chartsContainer.setVisible(false);
                emptyPlaceholder.setVisible(true);
            }

            else {
                chartsContainer.setVisible(true);
                emptyPlaceholder.setVisible(false);
                loadPieChart(data);
                loadBarChart(from, to, userId);
                loadCategoryList(from, to, userId);
            }
        });

        task.setOnFailed(event -> {
            showError("Помилка при завантаженні даних для аналітики");
        });

        TaskExecutor.getPool().submit(task);
    }

    @FXML public void initialize() {
        loadMonth();
        loadYear();

        monthComboBox.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
        yearComboBox.getSelectionModel().selectFirst();

        loadCharts();
    }

    @FXML private void update() {
        loadCharts();
    }

    @FXML private void toHomeButton() {
        try {
            App.setRoot("MainView");
        }
        catch (Exception exception) {
            showError("Помилка при зміні вікна");
        }
    }

    @FXML private void printAnalyticsButton() {
        if (emptyPlaceholder.isVisible()) {
            showError("Неможливо сформулювати звіт через відсутність даних");
            return;
        }

        try {
            expensesPieChart.getStyleClass().add("chart-title-print");
            expensesBarChart.getStyleClass().add("chart-title-print");

            expensesPieChart.applyCss();
            expensesPieChart.layout();

            expensesBarChart.applyCss();
            expensesBarChart.layout();

            String selectedMonth = monthComboBox.getSelectionModel().getSelectedItem();
            int selectedYear = yearComboBox.getValue();

            WritableImage pieChartSnapshot = expensesPieChart.snapshot(null, null);
            WritableImage barChartSnapshot = expensesBarChart.snapshot(null, null);

            expensesPieChart.getStyleClass().remove("chart-title-print");
            expensesBarChart.getStyleClass().remove("chart-title-print");

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/purchasehistorysystem/templates/view/AnalyticsReportView.fxml"));
            VBox root = fxmlLoader.load();

            ReportPrintController reportPrintController = fxmlLoader.getController();

            String username = UserSessionUtils.getCurrentUser().getUsername();

            reportPrintController.setData(pieChartSnapshot, barChartSnapshot,
                    categoryCountList.getItems(), username, selectedMonth, selectedYear);

            PrinterJob printerJob = PrinterJob.createPrinterJob();

            if (printerJob != null && printerJob.showPrintDialog(expensesPieChart.getScene().getWindow())) {
                printerJob.printPage(root);
                printerJob.endJob();
            }
        }

        catch (IOException exception) {
            showError("Помилка при завантаженні звіту");
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
            alertStage.initOwner(monthComboBox.getScene().getWindow());
            alertStage.setTitle("Помилка");

            alertStage.showAndWait();
        }

        catch (IOException exception) {
            System.out.println("Критична помилка");
        }
    }
}
