package repositories;

import enities.Expense;
import enums.ExpenseType;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryExpenseRepository extends InMemoryFinancialRepository<Expense>
        implements ExpenseRepository {

    @Override
    public List<Expense> findByExpenseType(ExpenseType expenseType) {
        Objects.requireNonNull(expenseType, "Тип расхода не может быть null");

        return storage.values().stream()
                .filter(expense -> expense.getExpenseType() == expenseType)
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .toList();
    }

    @Override
    public Map<String, Double> getAmountByCategory(LocalDate startDate, LocalDate endDte) {
        return Map.of();
    }

    @Override
    public Map<String, Double> getAmountsByCategory(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        return storage.values().stream()
                .filter(expense -> expense.isInDateRange(startDate, endDate))
                .collect(Collectors.groupingBy(
                        expense -> expense.getCategory().getName(),
                        Collectors.summingDouble(Expense::getExpenseAmount)
                ));
    }

    @Override
    public double getTotalExpenses(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        return storage.values().stream()
                .filter(expense -> expense.isInDateRange(startDate, endDate))
                .mapToDouble(Expense::getExpenseAmount)
                .sum();
    }

    @Override
    public double getTotalBalance() {
        return storage.values().stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}