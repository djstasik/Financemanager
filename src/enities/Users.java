package enities;

import java.util.Objects;
public class Users {
    private String id;
    private String username;
    private String email;
    private String passwordHash;

    public Users(String id, String username, String email){
        this.id= Objects.requireNonNull(id,"ID не может быть null");
        this.username= Objects.requireNonNull(username,"Имя не может быть null");
        this.email= Objects.requireNonNull(email,"email не может быть null");
        this.passwordHash="";
    }
    public void setPassword(String password){
        this.passwordHash= password !=null ? password : "";
    }
    public boolean checkPassword(String password){
        return passwordHash.equals(password != null ? password : "");
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }

    public void setUsername(String username) {
        this.username = Objects.requireNonNull(username, "Имя пользователя не может быть null");
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Email не может быть null");
    }
}
