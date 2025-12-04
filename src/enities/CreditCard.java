package enities;
import java.time.LocalDate;
import java.util.Objects;

public class CreditCard {
    private String id;
    private String cardNumber;
    private String holderName;
    private double creditLimit;
    private double currentBalance;
    private LocalDate expiryDate;

    public CreditCard(String id, String cardNumber, String holderName, double creditLimit, LocalDate expiryDate){
        this.id= Objects.requireNonNull(id,"ID не может быть null");
        this.cardNumber= Objects.requireNonNull(cardNumber,"Номер карты не может быть null");
        this.holderName= Objects.requireNonNull(holderName,"Имя держателя не может быть null");
        this.creditLimit=creditLimit;
        this.currentBalance=0;
        this.expiryDate= Objects.requireNonNull(expiryDate,"Срок действия не может быть null");
    }
    public double getAvailableCredit(){
        return creditLimit-currentBalance;
    }
    public void deposit(double amount){
        if(amount<0){
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной");
        }
        currentBalance= Math.max(0, currentBalance- amount);
    }
    public void withdraw(double amount){
        if (amount<0){
            throw new IllegalArgumentException("Сумма снятия должна быть положительной");
        }
        if (amount>getAvailableCredit()){
            throw new IllegalArgumentException("Недостаточно доступного кредита");
        }
        currentBalance +=amount;
    }
    public String getId() { return id; }
    public String getCardNumber() { return cardNumber; }
    public String getHolderName() { return holderName; }
    public double getCreditLimit() { return creditLimit; }
    public double getCurrentBalance() { return currentBalance; }
    public LocalDate getExpiryDate() { return expiryDate; }

    public void setHolderName(String holderName) {
        this.holderName = Objects.requireNonNull(holderName, "Имя держателя не может быть null");
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = Objects.requireNonNull(expiryDate, "Срок действия не может быть null");
    }
}