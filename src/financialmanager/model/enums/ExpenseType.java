package financialmanager.model.enums;

public enum ExpenseType {
    FIXED("Постоянный"),
    VARIABLE("Переменный"),
    UNEXPECTED("Неожиданный");

    private final String displayName;

    ExpenseType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}