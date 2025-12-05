package repositories;
import enities.FinancialOperation;
import java.time.LocalDate;
import  java.util.List;
import  java.util.Optional;

public interface FinancialRepository<T extends FinancialOperation> {
    void add (T operation);
    void delete (String id);
    void update(T operation);
    Optional<T> findById(String id);
    List<T> findAll();
    List<T> findByDateRange(LocalDate startDate, LocalDate endDate);
    List<T> findByCategory(String categoryId);
    boolean exists(String id);
}
