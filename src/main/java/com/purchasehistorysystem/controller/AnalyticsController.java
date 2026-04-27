package com.purchasehistorysystem.controller;

import com.purchasehistorysystem.App;
import com.purchasehistorysystem.repository.AnalyticsRepository;
import com.purchasehistorysystem.util.UserSessionUtils;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

import javafx.scene.chart.PieChart;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

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

    private void loadPieChart(LocalDate from, LocalDate to, int userId) throws SQLException {
        Map<String, Double> data = analyticsRepository.getExpensesByCategory(from, to, userId);
        expensesPieChart.getData().clear();

        for (String categoryName : data.keySet()) {
            Double amount = data.get(categoryName);
            String amountString = String.format("%.2f", amount);
            String dataLabel = categoryName + "(" + amountString + " грн)";

            PieChart.Data slice = new PieChart.Data(dataLabel, amount);

            expensesPieChart.getData().add(slice);
        }
    }

    private void loadBarChart(LocalDate from, LocalDate to, int userId) throws SQLException {
        Map<LocalDate, Double> data = analyticsRepository.getExpensesByDate(from, to, userId);

        expensesBarChart.getData().clear();

        Series<String, Double> series = new Series<>();
        series.setName("Витрати по дням");

        for (LocalDate date : data.keySet()) {
            Double amount = data.get(date);
            int dayOfMonth = date.getDayOfMonth();
            String dayString = String.valueOf(dayOfMonth);

            XYChart.Data<String, Double> dataPoint = new XYChart.Data<>(dayString, amount);

            series.getData().add(dataPoint);
        }

        expensesBarChart.getData().add(series);
    }

    private void loadCategoryList(LocalDate from, LocalDate to, int userId) throws SQLException {
        Map<String, Integer> data = analyticsRepository.getCategoryCount(from, to, userId);
        categoryCountList.getItems().clear();

        for (String category : data.keySet()) {
            int categoryCount = data.get(category);
            String categoryCountString = category + " - " + categoryCount;
            categoryCountList.getItems().add(categoryCountString);
        }
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

        try {
            loadPieChart(from, to, userId);
            loadBarChart(from, to, userId);
            loadCategoryList(from, to, userId);
        }

        catch (SQLException exception) {
            System.out.println("Помилка при завантаженні даних для аналітики");
        }
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
            System.err.println("Помилка при зміні вікна");
        }
    }
}
