package enities;

import enums.IncomeSource;
import java.time.LocalDate;
import java.util.Objects;

public class Income extends FinancialOperation {
    private IncomeSource incomeSource;

    public Income(String id, String name, double amount, LocalDate date,
                  Category category, IncomeSource incomeSource){
        super( id, name, Math.abs(amount), date, category);
        this.incomeSource = Objects.requireNonNull(incomeSource,"Источник дохода не может быть null");
    }
    public String getOperationType(){
        return "ДОХОД";
    }
    public boolean isValid(){
        return  getAmount() >0 &&
                !getName().trim().isEmpty() &&
                getCategory() != null;
    }
    public IncomeSource getIncomeSource(){return incomeSource;}
    public void setIncomeSource(IncomeSource incomeSource){
        this.incomeSource = Objects.requireNonNull(incomeSource,"Источник дохода не может быть null");
    }
}
