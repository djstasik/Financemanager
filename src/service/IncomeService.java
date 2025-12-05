package service;

import enities.Income;
import enums.IncomeSource;
import repositories.IncomeRepository;
import java.util.Objects;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class IncomeService extends FinancialService<Income> {

    public IncomeService(IncomeRepository repository) {
        super(repository);
    }

    private IncomeRepository getIncomeRepository() {
        return (IncomeRepository) repository;
    }

    public List<Income> getIncomesBySource(IncomeSource incomeSource) {
        Objects.requireNonNull(incomeSource, "Источник дохода не может быть null");
        return getIncomeRepository().findByIncomeSource(incomeSource);
    }

    public Map<String, Double> getStatisticsBySource(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        return getIncomeRepository().getAmountsBySource(startDate, endDate);
    }

    public double getTotalIncomes(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        return getIncomeRepository().getTotalIncomes(startDate, endDate);
    }

    @Override
    public double getTotalBalance() {
        return getIncomeRepository().getTotalBalance();
    }

    public double getCurrentMonthlyIncome() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        return getTotalIncomes(startOfMonth, endOfMonth);
    }
}