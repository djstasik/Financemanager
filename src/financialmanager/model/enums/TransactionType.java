package financialmanager.model.enums;

public enum TransactionType {
    DEPOSIT("Пополнение"),
    WITHDRAWAL("Снятие");

    private final String displayName;

    TransactionType(String displayName) {
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