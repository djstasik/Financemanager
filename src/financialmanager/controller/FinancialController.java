package financialmanager.controller;

import financialmanager.model.entities.FinancialOperation;
import financialmanager.service.FinancialService;
import java.util.Objects;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public abstract class FinancialController<T extends FinancialOperation> {
    protected final FinancialService<T> service;

    public FinancialController(FinancialService<T> service) {
        this.service = Objects.requireNonNull(service, "Сервис не может быть null");
    }

    public void add(T operation) {
        try {
            service.addOperation(operation);
            System.out.println("Операция успешно добавлена: " + operation.getName());
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении операции: " + e.getMessage());
            throw e;
        }
    }

    public void delete(String id) {
        try {
            service.deleteOperation(id);
            System.out.println("Операция с ID '" + id + "' успешно удалена");
        } catch (Exception e) {
            System.err.println("Ошибка при удалении операции: " + e.getMessage());
            throw e;
        }
    }

    public void update(T operation) {
        try {
            service.updateOperation(operation);
            System.out.println("Операция успешно обновлена: " + operation.getName());
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении операции: " + e.getMessage());
            throw e;
        }
    }

    public Optional<T> getById(String id) {
        return service.getOperationById(id);
    }

    public List<T> getAll() {
        return service.getAllOperations();
    }

    public List<T> getByDateRange(LocalDate startDate, LocalDate endDate) {
        return service.getOperationsByDateRange(startDate, endDate);
    }

    public List<T> getByCategory(String categoryId) {
        return service.getOperationsByCategory(categoryId);
    }

    public boolean exists(String id) {
        return service.operationExists(id);
    }

    public abstract double getTotalBalance();
}