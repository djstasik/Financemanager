package enities;

import java.util.Objects;

public class Category{
    private String id;
    private String name;
    private String description;
    private String colorCode;

    public Category(String id, String name){
        this.id = Objects.requireNonNull(id, "ID не может быть null");
        this.name = Objects.requireNonNull(name, "ID не может быть null");
        this.description= "";
        this.colorCode= "#000000";
    }

    public Category(String id, String name, String description, String colorCode){
        this(id, name);
        this.description = description != null ? description : "";
        this.colorCode= colorCode != null ? colorCode : "#000000";

    }
    public String getId(){return id;}
    public String getName(){return name;}
    public String getDescription() { return description; }
    public String getColorCode() { return colorCode; }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Название не может быть null");
    }
    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }
    public void setColorCode(String colorCode) {
        this.colorCode = colorCode != null ? colorCode : "#000000";
    }

}