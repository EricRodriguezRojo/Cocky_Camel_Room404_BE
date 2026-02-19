package Cocky_Camel.hospital;

import jakarta.persistence.*;

@Entity
@Table(name = "ENFERMEROS")
public class Nurse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idENFERMEROS")
    private int id;

    @Column(name = "NOMBRE", nullable = false)
    private String name;

    @Column(name = "USUARIO", nullable = false)
    private String user;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    public Nurse() {}

    public Nurse(String name, String user, String password) {
        this.name = name;
        this.user = user;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
