package repositories;


import enities.Expense;
import enums.ExpenseType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface ExpenseRepository extends FinancialRepository<Expense> {
    List<Expense> findByExpenseType(ExpenseType expenseType);
    Map<String, Double> getAmountByCategory(LocalDate startDate, LocalDate endDte);
    double getTotalExpenses(LocalDate startDate, LocalDate endDate);
    double getTotalBalance();

    Map<String, Double> getAmountsByCategory(LocalDate startDate, LocalDate endDate);
}
