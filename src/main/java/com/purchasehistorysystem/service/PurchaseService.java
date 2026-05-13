package com.purchasehistorysystem.service;

import com.purchasehistorysystem.model.Category;
import com.purchasehistorysystem.model.Purchase;
import com.purchasehistorysystem.repository.PurchaseRepository;
import com.purchasehistorysystem.util.UserSessionUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class PurchaseService {
    private final PurchaseRepository purchaseRepository = new PurchaseRepository();

    private int getCurrentUserId() {
        return UserSessionUtils.getCurrentUser().getId();
    }

    public List<Purchase> getAllPurchases() throws SQLException {
        int userId = getCurrentUserId();
        return purchaseRepository.getAllPurchases(userId);
    }

    public void addPurchase(String name, Category category, double price, int amount) throws SQLException, IllegalArgumentException {
        if (name.isBlank() || category == null) {
            throw new IllegalArgumentException("Заповніть всі поля та оберіть категорію");
        }

        if (price <= 0 || amount <= 0) {
            throw new IllegalArgumentException("Вартість та кількість мають бути більшими за нуль");
        }

        if (price > 999999.99) {
            throw new IllegalArgumentException("Вартість не може перевищувати значення 999999.99");
        }

        int userId = getCurrentUserId();

        Purchase purchase = new Purchase(0, name, category, price, amount, LocalDate.now(), userId);
        purchaseRepository.addPurchase(purchase, userId);
    }

    public void deletePurchase(int id) throws SQLException {
        int userId = getCurrentUserId();
        purchaseRepository.deletePurchase(id, userId);
    }

    public List<Purchase> filter(Integer categoryId, LocalDate from, LocalDate to, double minPrice, double maxPrice) throws SQLException {
        int userId = UserSessionUtils.getCurrentUser().getId();

        if (from != null && to != null && from.isAfter(to)) {
            LocalDate temp = from;
            from = to;
            to = temp;
        }

        if (maxPrice < minPrice) {
            double temp = maxPrice;
            maxPrice = minPrice;
            minPrice = temp;
        }

        return purchaseRepository.filter(categoryId, from, to, minPrice, maxPrice, userId);
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
