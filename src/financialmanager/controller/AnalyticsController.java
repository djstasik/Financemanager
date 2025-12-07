package financialmanager.controller;

import financialmanager.service.AnalyticsService;
import java.util.Objects;
import java.time.LocalDate;
import java.util.Map;

public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = Objects.requireNonNull(analyticsService, "Сервис аналитики не может быть null");
    }

    public double getTotalBalance() {
        return analyticsService.getTotalBalance();
    }

    public Map<String, Object> getFinancialStatistics(LocalDate startDate, LocalDate endDate) {
        return analyticsService.getFinancialStatistics(startDate, endDate);
    }

    public Map<String, Object> getMonthlyReport(int year, int month) {
        return analyticsService.getMonthlyReport(year, month);
    }

    public Map<String, Object> getExpensesAnalytics(LocalDate startDate, LocalDate endDate) {
        return analyticsService.getExpensesAnalytics(startDate, endDate);
    }

    public Map<String, Object> getIncomesAnalytics(LocalDate startDate, LocalDate endDate) {
        return analyticsService.getIncomesAnalytics(startDate, endDate);
    }

    public String getFinancialHealth() {
        return analyticsService.getFinancialHealth();
    }

    public void showGeneralStatistics() {
        System.out.println("=== ФИНАНСОВАЯ СТАТИСТИКА ===");
        System.out.println("Общий баланс: " + getTotalBalance());
        System.out.println("Финансовое здоровье: " + getFinancialHealth());

        LocalDate now = LocalDate.now();
        Map<String, Object> currentMonth = getMonthlyReport(now.getYear(), now.getMonthValue());

        System.out.println("Текущий месяц:");
        currentMonth.forEach((key, value) ->
                System.out.println("  " + key + ": " + value)
        );
    }
}