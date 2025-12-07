package financialmanager.service;

import financialmanager.model.entities.Expense;
import financialmanager.model.enums.ExpenseType;
import financialmanager.model.repositories.ExpenseRepository;
import java.util.Objects;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ExpenseService extends FinancialService<Expense> {

    public ExpenseService(ExpenseRepository repository) {
        super(repository);
    }

    private ExpenseRepository getExpenseRepository() {
        return (ExpenseRepository) repository;
    }

    public List<Expense> getExpensesByType(ExpenseType expenseType) {
        Objects.requireNonNull(expenseType, "Тип расхода не может быть null");
        return getExpenseRepository().findByExpenseType(expenseType);
    }

    public Map<String, Double> getStatisticsByCategory(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        return getExpenseRepository().getAmountsByCategory(startDate, endDate);
    }

    public double getTotalExpenses(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        return getExpenseRepository().getTotalExpenses(startDate, endDate);
    }

    @Override
    public double getTotalBalance() {
        return getExpenseRepository().getTotalBalance();
    }

    public double getCurrentMonthlyExpense() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        return getTotalExpenses(startOfMonth, endOfMonth);
    }
}