package rso.itemscompare.authenticationservice.models.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "password_token")
public class PasswordTokenEntity {
    @Id
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "token", nullable = false)
    private String token;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
