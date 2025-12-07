package financialmanager.model.repositories;

import financialmanager.model.entities.Income;
import financialmanager.model.enums.IncomeSource;
import java.util.Objects;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryIncomeRepository extends InMemoryFinancialRepository<Income> implements IncomeRepository {

    @Override
    public List<Income> findByIncomeSource(IncomeSource incomeSource) {
        Objects.requireNonNull(incomeSource, "Источник дохода не может быть null");

        return storage.values().stream()
                .filter(income -> income.getIncomeSource() == incomeSource)
                .sorted(Comparator.comparing(Income::getDate).reversed())
                .toList();
    }

    @Override
    public Map<String, Double> getAmountsBySource(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        return storage.values().stream()
                .filter(income -> income.isInDateRange(startDate, endDate))
                .collect(Collectors.groupingBy(
                        income -> income.getIncomeSource().getDisplayName(),
                        Collectors.summingDouble(Income::getAmount)
                ));
    }

    @Override
    public double getTotalIncomes(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        return storage.values().stream()
                .filter(income -> income.isInDateRange(startDate, endDate))
                .mapToDouble(Income::getAmount)
                .sum();
    }

    @Override
    public double getTotalBalance() {
        return storage.values().stream()
                .mapToDouble(Income::getAmount)
                .sum();
    }
}