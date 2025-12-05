package controller;

import enities.Income;
import enums.IncomeSource;
import service.IncomeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IncomeController extends FinancialController<Income> {

    public IncomeController(IncomeService incomeService) {
        super(incomeService);
    }

    private IncomeService getIncomeService() {
        return (IncomeService) service;
    }

    public void AddIncomes(Income income) {
        add(income);
    }

    public void DeleteIncomes(String id) {
        delete(id);
    }

    public void EditIncomes(Income income) {
        update(income);
    }

    public List<Income> GetAllIncomes() {
        return getAll();
    }

    public Optional<Income> GetIncomesInformation(String id) {
        return getById(id);
    }

    public double GetTotalBalanceIncome() {
        return getTotalBalance();
    }

    public List<Income> getIncomesBySource(IncomeSource incomeSource) {
        return getIncomeService().getIncomesBySource(incomeSource);
    }

    public Map<String, Double> getStatisticsBySource(LocalDate startDate, LocalDate endDate) {
        return getIncomeService().getStatisticsBySource(startDate, endDate);
    }

    public double getTotalIncomes(LocalDate startDate, LocalDate endDate) {
        return getIncomeService().getTotalIncomes(startDate, endDate);
    }

    @Override
    public double getTotalBalance() {
        return getIncomeService().getTotalBalance();
    }

    public void RunProgramIncomes() {
        System.out.println("Программа управления доходами запущена");
        System.out.println("Всего доходов: " + getAll().size());
        System.out.println("Общий баланс доходов: " + getTotalBalance());
    }
}