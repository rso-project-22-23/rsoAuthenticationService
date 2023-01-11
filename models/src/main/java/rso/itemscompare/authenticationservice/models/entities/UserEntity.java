package rso.itemscompare.authenticationservice.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "rso_user")
@NamedQueries(value = {
        @NamedQuery(name = "UserEntity.getByEmail",
                query = "SELECT ue FROM UserEntity ue WHERE ue.email = :userEmail"
        )
})
public class UserEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "activated", nullable = false)
    private boolean activated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
