package financialmanager.controller;

import financialmanager.model.entities.Expense;
import financialmanager.model.enums.ExpenseType;
import financialmanager.service.ExpenseService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExpenseController extends FinancialController<Expense> {

    public ExpenseController(ExpenseService expenseService) {
        super(expenseService);
    }

    private ExpenseService getExpenseService() {
        return (ExpenseService) service;
    }

    public void AddPotentialExpenses(Expense expense) {
        add(expense);
    }

    public void DeletePotentialExpenses(String id) {
        delete(id);
    }

    public void EditPotentialExpenses(Expense expense) {
        update(expense);
    }

    public List<Expense> GetPotentialExpenses() {
        return getAll();
    }

    public Optional<Expense> GetPotentialExpensesInformation(String id) {
        return getById(id);
    }

    public double GetTotalBalancePotentialExpense() {
        return getTotalBalance();
    }

    public List<Expense> getExpensesByType(ExpenseType expenseType) {
        return getExpenseService().getExpensesByType(expenseType);
    }

    public Map<String, Double> getStatisticsByCategory(LocalDate startDate, LocalDate endDate) {
        return getExpenseService().getStatisticsByCategory(startDate, endDate);
    }

    public double getTotalExpenses(LocalDate startDate, LocalDate endDate) {
        return getExpenseService().getTotalExpenses(startDate, endDate);
    }

    @Override
    public double getTotalBalance() {
        return getExpenseService().getTotalBalance();
    }

    public void RunProgramPotentialExpenses() {
        System.out.println("Программа управления расходами запущена");
        System.out.println("Всего расходов: " + getAll().size());
        System.out.println("Общий баланс расходов: " + getTotalBalance());
    }
}