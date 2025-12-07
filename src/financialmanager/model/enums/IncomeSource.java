package financialmanager.model.enums;

public enum IncomeSource {
    SALARY("Зарплата"),
    INVESTMENT("Инвестиции"),
    FREELANCE("Фриланс"),
    BUSINESS("Бизнес"),
    GIFTS("Подарки"),
    OTHER("Другое");

    private final String displayName;

    IncomeSource(String displayName) {
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