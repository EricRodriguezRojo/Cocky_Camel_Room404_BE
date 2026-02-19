package Cocky_Camel.Room404;

import jakarta.persistence.*;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "password")
    private String password;

    @Column(name = "google_uid")
    private String googleUid;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "ENUM('User', 'Admin')")
    private Role role = Role.User; 

    @Column(name = "is_premium")
    private boolean isPremium = false; 

    public enum Role {
        User,
        Admin
    }

    public User() {}

    public User(String email, String nickname, String password, String googleUid, Role role, boolean isPremium) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.googleUid = googleUid;
        this.role = (role != null) ? role : Role.User;
        this.isPremium = isPremium;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGoogleUid() {
        return googleUid;
    }

    public void setGoogleUid(String googleUid) {
        this.googleUid = googleUid;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }
}