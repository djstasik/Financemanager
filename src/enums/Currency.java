package enums;

public enum Currency {
    BYN("РУБЛЬ", "Р"),
    USD("Доллар","$"),
    EUR("Евро","€");

    private final String name;
    private final String symbol;

    Currency(String name, String symbol){
        this.name =name;
        this.symbol= symbol;
    }
    public String getName(){return name;}
    public String getSymbol(){return symbol;}
    @Override
    public String toString(){
        return getName() + " " + getSymbol();
    }
}
