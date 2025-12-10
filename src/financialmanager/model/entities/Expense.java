package financialmanager.model.entities;

import financialmanager.model.enums.ExpenseType;

import java.time.LocalDate;
import java.util.Objects;

public class Expense extends FinancialOperation {
    private ExpenseType expenseType;

    public Expense(String id, String name, double amount, LocalDate date,
                   Category category, ExpenseType expenseType) {
        super(id, name, Math.abs(amount) * -1, date, category);
        this.expenseType = Objects.requireNonNull(expenseType, "Тип расхода не может быть null");
    }

    public Expense(String id, String name, double amount, LocalDate date,
                   String description, Category category, ExpenseType expenseType) {
        super(id, name, Math.abs(amount) * -1, date, description, category, null);
        this.expenseType = Objects.requireNonNull(expenseType, "Тип расхода не может быть null");
    }

    public Expense(String id, String name, double amount, LocalDate date,
                   String description, Category category, ExpenseType expenseType, String creditCardId) {
        super(id, name, Math.abs(amount) * -1, date, description, category, creditCardId);
        this.expenseType = Objects.requireNonNull(expenseType, "Тип расхода не может быть null");
    }

    @Override
    public String getOperationType() {
        return "РАСХОД";
    }

    @Override
    public boolean isValid() {
        return getAmount() < 0 &&
                !getName().trim().isEmpty() &&
                getCategory() != null;
    }

    public ExpenseType getExpenseType() { return expenseType; }
    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = Objects.requireNonNull(expenseType, "Тип расхода не может быть null");
    }

    public double getExpenseAmount() {
        return Math.abs(getAmount());
    }

    @Override
    public String toString() {
        return String.format("Expense{id='%s', name='%s', amount=%.2f, type=%s%s}",
                getId(), getName(), getExpenseAmount(), expenseType,
                hasCreditCard() ? ", card=" + getCreditCardId() : "");
    }
}