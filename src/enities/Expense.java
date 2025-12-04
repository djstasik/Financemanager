package enities;

import enums.ExpenseType;
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
        super(id, name, Math.abs(amount) * -1, date, description, category);
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

    public double getExpenseAmount() {
        return Math.abs(getAmount());
    }

    public ExpenseType getExpenseType() { return expenseType; }

    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = Objects.requireNonNull(expenseType, "Тип расхода не может быть null");
    }
}