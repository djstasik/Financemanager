package financialmanager.model.entities;

import java.time.LocalDate;
import java.util.Objects;

public class CreditCard {
    private String id;
    private String cardNumber;
    private String ownerName;  // Имя владельца как строка (упрощенно)
    private double creditLimit;
    private double currentBalance;
    private LocalDate expiryDate;

    public CreditCard(String id, String cardNumber, String ownerName,
                      double creditLimit, LocalDate expiryDate) {
        this.id = Objects.requireNonNull(id, "ID не может быть null");
        this.cardNumber = Objects.requireNonNull(cardNumber, "Номер карты не может быть null");
        this.ownerName = Objects.requireNonNull(ownerName, "Имя владельца не может быть null");
        this.creditLimit = creditLimit;
        this.currentBalance = 0;
        this.expiryDate = Objects.requireNonNull(expiryDate, "Срок действия не может быть null");
    }

    // Геттеры
    public String getId() { return id; }
    public String getCardNumber() { return cardNumber; }
    public String getOwnerName() { return ownerName; }
    public double getCreditLimit() { return creditLimit; }
    public double getCurrentBalance() { return currentBalance; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public double getAvailableCredit() { return creditLimit - currentBalance; }

    // Сеттеры
    public void setOwnerName(String ownerName) {
        this.ownerName = Objects.requireNonNull(ownerName, "Имя владельца не может быть null");
    }
    public void setCreditLimit(double creditLimit) { this.creditLimit = creditLimit; }
    public void setCurrentBalance(double currentBalance) { this.currentBalance = currentBalance; }
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = Objects.requireNonNull(expiryDate, "Срок действия не может быть null");
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной");
        }
        currentBalance = Math.max(0, currentBalance - amount); // Уменьшаем задолженность
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной");
        }
        if (amount > getAvailableCredit()) {
            String message = String.format(
                    "Недостаточно кредита!\n" +
                            "Попытка снять: %.2f ₽\n" +
                            "Доступно: %.2f ₽\n" +
                            "Лимит: %.2f ₽\n" +
                            "Текущая задолженность: %.2f ₽",
                    amount, getAvailableCredit(), creditLimit, currentBalance);
            throw new IllegalArgumentException(message);
        }
        currentBalance += amount;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", cardNumber, ownerName);
    }
}