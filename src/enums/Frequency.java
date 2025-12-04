package enums;

public enum Frequency {
    DAILY("Ежедневно"),
    WEEKLY("Еженедельно"),
    MONTHLY("Ежемесячно"),
    YEARLY("Ежегодно");

    private final String displayName;

    Frequency(String displayName){
        this.displayName=displayName;
    }
    public String getDisplayName(){return displayName;}
    @Override
    public String toString(){
        return getDisplayName();
    }
}
