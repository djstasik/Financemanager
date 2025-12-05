package enities;

import java.time.LocalDate;
import java.util.Objects;

public abstract class FinancialOperation {
    private String id;
    private String name;
    private double amount;
    private LocalDate date;
    private String description;
    private Category category;

    public FinancialOperation(String id, String name, double amount, LocalDate date, Category category){
        this.id= Objects.requireNonNull(id, "ID не может быть null");
        this.name= Objects.requireNonNull(name, " Название не может быть null");
        this.amount=amount;
        this.date= Objects.requireNonNull(date,"Дата не может быть null");
        this.category=Objects.requireNonNull(category,"Категория не может быть null");
        this.description="";
    }
    public abstract String getOperationType();
    public abstract boolean isValid();
    public String getId(){return id;}
    public String getName() { return name; }
    public double getAmount() { return amount; }
    public LocalDate getDate() {return date;}
    public String getDescription() {return description;}
    public Category getCategory() {return category;}

    public void setName(String name){
        this.name=Objects.requireNonNull(name,"Назваение не может быть null");
    }
    public void setId(String id){
        this.id=Objects.requireNonNull(id,"Назваение не может быть null");
    }
    public void setAmount(double amount){
        this.amount=Objects.requireNonNull(this.amount=amount);
    }
    public void setDate(LocalDate date){
        this.date=Objects.requireNonNull(date,"Назваение не может быть null");
    }
    public void setDescription(String description){
        this.description= description != null ? description : "";
    }
    public void setCategory(Category category){
        this.category = Objects.requireNonNull(category, "Категория не может быть null");
    }
    public boolean isInDateRange(LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}