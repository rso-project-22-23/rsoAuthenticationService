package rso.itemscompare.authenticationservice.lib;

public class RegistrationToken {
    private String userEmail;
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
