package rso.itemscompare.authenticationservice.models.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "auth_token")
public class AuthTokenEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "token", nullable = false)
    private String token;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
