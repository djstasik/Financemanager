package service;

import enities.FinancialOperation;
import repositories.FinancialRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class FinancialService<T extends FinancialOperation> {
    protected final FinancialRepository<T> repository;

    protected FinancialService(FinancialRepository<T> repository) {
        this.repository = Objects.requireNonNull(repository, "Репозиторий не может быть null");
    }
    public void addOperation(T operation){
        Objects.requireNonNull(operation, "Операция не может быть null");
        if(operation.isValid()){
            throw new IllegalArgumentException("Некорректные данные операции");
        }
        repository.add(operation);
    }
    public void deleteOperation(String id){
        Objects.requireNonNull(id, "ID не может быть null");
        repository.delete(id);
    }
    public void updateOperation(T operation){
        Objects.requireNonNull(operation, "Операция не может быть null");
        if (!operation.isValid()) {
            throw new IllegalArgumentException("Некорректные данные операции");
        }
        repository.update(operation);
    }
    public Optional<T> getOperationById(String id) {
        return repository.findById(id);
    }
    public List<T> getAllOperations() {
        return repository.findAll();
    }

    public List<T> getOperationsByDateRange(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        return repository.findByDateRange(startDate, endDate);
    }

    public List<T> getOperationsByCategory(String categoryId) {
        Objects.requireNonNull(categoryId, "ID категории не может быть null");
        return repository.findByCategory(categoryId);
    }

    public boolean operationExists(String id) {
        return repository.exists(id);
    }

    public abstract double getTotalBalance();
}
