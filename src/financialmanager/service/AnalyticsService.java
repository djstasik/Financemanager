package financialmanager.service;

import financialmanager.model.repositories.ExpenseRepository;
import financialmanager.model.repositories.IncomeRepository;
import java.util.Objects;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsService {
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    public AnalyticsService(ExpenseRepository expenseRepository, IncomeRepository incomeRepository) {
        this.expenseRepository = Objects.requireNonNull(expenseRepository, "Репозиторий расходов не может быть null");
        this.incomeRepository = Objects.requireNonNull(incomeRepository, "Репозиторий доходов не может быть null");
    }

    public double getTotalBalance() {
        double totalIncomes = incomeRepository.getTotalBalance();
        double totalExpenses = expenseRepository.getTotalBalance();
        return totalIncomes + totalExpenses;
    }

    public Map<String, Object> getFinancialStatistics(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        Map<String, Object> statistics = new HashMap<>();

        double incomes = incomeRepository.getTotalIncomes(startDate, endDate);
        double expenses = expenseRepository.getTotalExpenses(startDate, endDate);
        double balance = incomes - expenses;

        statistics.put("Общие доходы", incomes);
        statistics.put("Общие расходы", expenses);
        statistics.put("Баланс", balance);
        statistics.put("Процент расходов", incomes > 0 ? (expenses / incomes) * 100 : 0);
        statistics.put("Статус", balance >= 0 ? "Положительный" : "Отрицательный");

        return statistics;
    }

    public Map<String, Object> getMonthlyReport(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return getFinancialStatistics(startOfMonth, endOfMonth);
    }

    public Map<String, Object> getExpensesAnalytics(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        Map<String, Object> analytics = new HashMap<>();

        Map<String, Double> expensesByCategory = expenseRepository.getAmountsByCategory(startDate, endDate);
        double totalExpenses = expenseRepository.getTotalExpenses(startDate, endDate);

        analytics.put("Расходы по категориям", expensesByCategory);
        analytics.put("Общие расходы", totalExpenses);
        analytics.put("Самая затратная категория", findMaxCategory(expensesByCategory));

        return analytics;
    }

    public Map<String, Object> getIncomesAnalytics(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        Map<String, Object> analytics = new HashMap<>();

        Map<String, Double> incomesBySource = incomeRepository.getAmountsBySource(startDate, endDate);
        double totalIncomes = incomeRepository.getTotalIncomes(startDate, endDate);

        analytics.put("Доходы по источникам", incomesBySource);
        analytics.put("Общие доходы", totalIncomes);
        analytics.put("Основной источник дохода", findMaxSource(incomesBySource));

        return analytics;
    }

    private String findMaxCategory(Map<String, Double> expensesByCategory) {
        return expensesByCategory.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Нет данных");
    }

    private String findMaxSource(Map<String, Double> incomesBySource) {
        return incomesBySource.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Нет данных");
    }

    public String getFinancialHealth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        Map<String, Object> currentMonth = getFinancialStatistics(startOfMonth, endOfMonth);
        double incomes = (double) currentMonth.get("Общие доходы");
        double expenses = (double) currentMonth.get("Общие расходы");
        double expensePercentage = (double) currentMonth.get("Процент расходов");

        if (expensePercentage > 90) {
            return "Критическое - тратите почти все доходы";
        } else if (expensePercentage > 70) {
            return "Рискованное - высокий уровень расходов";
        } else if (expensePercentage > 50) {
            return "Стабильное - умеренные расходы";
        } else {
            return "Отличное - низкие расходы, хорошие накопления";
        }
    }
}