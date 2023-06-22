package antifraud.api.authentication;

import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Table(name = "users")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;
    @Column(name = "role")
    private String role;
    @Column(name = "status")
    private Boolean accountNonLocked;
    @Column(name = "password")
    private String password;

    public User() {
    }

    public User(String name, String username, String password) {
        this.name = name;
        this.username = username.toLowerCase();
        this.password = password;
        this.accountNonLocked = false;
    }

    public boolean checkData(){
        return  this.name != null  &&
                this.username != null &&
                this.password != null;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
