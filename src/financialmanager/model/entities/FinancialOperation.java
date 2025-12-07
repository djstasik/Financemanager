package financialmanager.model.entities;

import java.time.LocalDate;
import java.util.Objects;

public abstract class FinancialOperation {
    private String id;
    private String name;
    private double amount;
    private LocalDate date;
    private String description;
    private Category category;

    public FinancialOperation(String id, String name, double amount, LocalDate date, Category category) {
        this.id = Objects.requireNonNull(id, "ID не может быть null");
        this.name = Objects.requireNonNull(name, "Название не может быть null");
        this.amount = amount;
        this.date = Objects.requireNonNull(date, "Дата не может быть null");
        this.category = Objects.requireNonNull(category, "Категория не может быть null");
        this.description = "";
    }

    public FinancialOperation(String id, String name, double amount, LocalDate date,
                              String description, Category category) {
        this(id, name, amount, date, category);
        this.description = description != null ? description : "";
    }

    public abstract String getOperationType();
    public abstract boolean isValid();

    public double getAbsoluteAmount() {
        return Math.abs(amount);
    }

    public boolean isInDateRange(LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Название не может быть null");
    }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) {
        this.date = Objects.requireNonNull(date, "Дата не может быть null");
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public Category getCategory() { return category; }
    public void setCategory(Category category) {
        this.category = Objects.requireNonNull(category, "Категория не может быть null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinancialOperation that = (FinancialOperation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s{id='%s', name='%s', amount=%.2f, date=%s}",
                getClass().getSimpleName(), id, name, amount, date);
    }
}