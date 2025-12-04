package enities;

import java.time.LocalDate;
import java.util.Objects;

public class Budget {
    private String id;
    private Category category;
    private double limit;
    private LocalDate startDate;
    private LocalDate endDate;

    public Budget(String id, Category category, double limit,
                  LocalDate startDate, LocalDate endDate) {
        this.id = Objects.requireNonNull(id, "ID не может быть null");
        this.category = Objects.requireNonNull(category, "Категория не может быть null");
        this.limit = limit;
        this.startDate = Objects.requireNonNull(startDate, "Дата начала не может быть null");
        this.endDate = Objects.requireNonNull(endDate, "Дата окончания не может быть null");
    }
    public boolean isExceeded(double currentSpending){
        return currentSpending > limit;
    }
    public double getRemaining(double currentSpending) {
        return Math.max(0, limit - currentSpending);
    }
    public boolean isActive(LocalDate date){
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    public String getId() { return id; }
    public Category getCategory() { return category; }
    public double getLimit() { return limit; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }

    public void setCategory(Category category) {
        this.category = Objects.requireNonNull(category, "Категория не может быть null");
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = Objects.requireNonNull(startDate, "Дата начала не может быть null");
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = Objects.requireNonNull(endDate, "Дата окончания не может быть null");
    }
}
