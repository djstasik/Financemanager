package repositories;

import enities.FinancialOperation;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class InMemoryFinancialRepository<T extends FinancialOperation>
        implements FinancialRepository<T> {

    protected final Map<String, T> storage = new ConcurrentHashMap<>();
    @Override
    public void add(T operation) {
        Objects.requireNonNull(operation, "Операция не может быть null");
        if(exists(operation.getId())){
            throw new IllegalArgumentException("Операция с ID '" + operation.getId() + "' уже существует");
        }
        storage.put(operation.getId(), operation);
    }
    @Override
    public void delete(String id){
        Objects.requireNonNull(id, "ID не может быть null");
        if(!storage.containsKey(id)){
            throw new IllegalArgumentException("Операция с ID '" + id + "' не найдена");
        }
        storage.remove(id);
    }
    @Override
    public void update(T operation) {
        Objects.requireNonNull(operation, "Операция не может быть null");
        if (!exists(operation.getId())) {
            throw new IllegalArgumentException("Операция с ID '" + operation.getId() + "' не найдена");
        }
        storage.put(operation.getId(), operation);
    }

    @Override
    public Optional<T> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }
    @Override
    public List<T> findByDateRange(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        return storage.values().stream()
                .filter(operation -> operation.isInDateRange(startDate, endDate))
                .sorted(Comparator.comparing(FinancialOperation::getDate).reversed())
                .toList();
    }

    @Override
    public List<T> findByCategory(String categoryId) {
        Objects.requireNonNull(categoryId, "ID категории не может быть null");

        return storage.values().stream()
                .filter(operation -> operation.getCategory().getId().equals(categoryId))
                .sorted(Comparator.comparing(FinancialOperation::getDate).reversed())
                .toList();
    }

    @Override
    public boolean exists(String id) {
        return storage.containsKey(id);
    }

    protected void clear() {
        storage.clear();
    }

    public int count() {
        return storage.size();
    }

    public abstract Map<String, Double> getAmountsByCategory(LocalDate startDate, LocalDate endDate);
}