package com.purchasehistorysystem.service;

import com.purchasehistorysystem.model.Category;
import com.purchasehistorysystem.model.Purchase;
import com.purchasehistorysystem.repository.PurchaseRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class PurchaseService {
    private final PurchaseRepository purchaseRepository = new PurchaseRepository();

    public List<Purchase> getAllPurchases() throws SQLException {
        return purchaseRepository.getAllPurchases();
    }

    public void addPurchase(String name, Category category, double price, int amount) throws SQLException {
        if (name.isBlank() || category == null) {
            throw new IllegalArgumentException("Заповність всі поля");
        }

        if (price <= 0) {
            throw new IllegalArgumentException("Ціна має бути більшою за нуль");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Кількість має бути більшою за нуль");
        }

        Purchase purchase = new Purchase(0, name, category, price, amount, LocalDate.now());
        purchaseRepository.addPurchase(purchase);
    }

    public void deletePurchase(int id) throws SQLException {
        purchaseRepository.deletePurchase(id);
    }

    public List<Purchase> filterByCategory(int categoryID) throws SQLException {
        return purchaseRepository.filterByCategory(categoryID);
    }

    public List<Purchase> filterByDateRange(LocalDate from, LocalDate to) throws SQLException {
        return purchaseRepository.filterByDateRange(from, to);
    }

    public Map<String, List<Purchase>> groupByMonth(List<Purchase> purchaseList) {
        Map<String, List<Purchase>> purchasesPerMonth = new LinkedHashMap<>();

        for (Purchase purchase : purchaseList) {
            String monthName = purchase.getDate().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("uk"));
            monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);

            String monthYear = monthName + " " + purchase.getDate().getYear();

            if (!purchasesPerMonth.containsKey(monthYear)) {
                purchasesPerMonth.put(monthYear, new ArrayList<>());
            }

            purchasesPerMonth.get(monthYear).add(purchase);
        }
        return purchasesPerMonth;
    }

    public double calculateExpensesForMonth(List<Purchase> purchaseList) {
        double expensesForMonth = 0;

        for (Purchase purchase : purchaseList) {
            expensesForMonth += purchase.getPrice() * purchase.getAmount();
        }

        return expensesForMonth;
    }
}
