package repositories;
import enities.Income;
import enums.IncomeSource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface IncomeRepository extends FinancialRepository<Income> {
    List<Income> findByIncomeSource(IncomeSource incomeSource);
    Map<String, Double> getAmountsBySource(LocalDate startDate, LocalDate endDate);
    double getTotalIncomes(LocalDate startDate, LocalDate endDate);
    double getTotalBalance();
}
