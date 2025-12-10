package financialmanager.model.enums;

public enum PaymentMethod {
    CASH("Наличные"),
    DEBIT_CARD("Дебетовая карта"),
    CREDIT_CARD("Кредитная карта"),
    TRANSFER("Перевод"),
    OTHER("Другое");

    private final String displayName;

    PaymentMethod(String displayName) {
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