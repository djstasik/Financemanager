package financialmanager.model.repositories;

import financialmanager.model.entities.Expense;
import financialmanager.model.enums.ExpenseType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseRepository extends FinancialRepository<Expense> {
    List<Expense> findByExpenseType(ExpenseType expenseType);
    Map<String, Double> getAmountsByCategory(LocalDate startDate, LocalDate endDate);
    double getTotalExpenses(LocalDate startDate, LocalDate endDate);
    double getTotalBalance();
}