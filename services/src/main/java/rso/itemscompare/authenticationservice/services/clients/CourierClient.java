package rso.itemscompare.authenticationservice.services.clients;

import models.SendEnhancedRequestBody;
import models.SendEnhancedResponseBody;
import models.SendRequestMessage;
import services.Courier;
import services.SendService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.HashMap;

@ApplicationScoped
public class CourierClient {
    @PostConstruct
    private void init() {
        // Initialize Courier. Get Authentication token from System environment variable
        Courier.init(System.getenv("COURIER_TOKEN"));
    }

    public void sendRegistrationMail(String mail, String token) throws IOException {
        SendEnhancedRequestBody request = new SendEnhancedRequestBody();
        SendRequestMessage message = new SendRequestMessage();

        HashMap<String, String> to = new HashMap<>();
        to.put("email", mail);
        message.setTo(to);
        message.setTemplate("FMTGDCG12YMNKXNTRG15MYMBR3K0");

        HashMap<String, Object> data = new HashMap<>();
        String url = String.format("http://20.31.253.184/authentication-service/v1/confirm-registration?user=%s&token=%s", mail, token);
        data.put("confirm_url", url);
        message.setData(data);

        request.setMessage(message);

        SendEnhancedResponseBody response = new SendService().sendEnhancedMessage(request);
        System.out.println("Registration send email response: " + response);
        if (response == null) {
            throw new IOException("Failed to send email");
        }
    }

    public void sendResetPasswordMail(String mail, String token) throws IOException {
        SendEnhancedRequestBody request = new SendEnhancedRequestBody();
        SendRequestMessage message = new SendRequestMessage();

        HashMap<String, String> to = new HashMap<>();
        to.put("email", mail);
        message.setTo(to);
        message.setTemplate("THS4KF3FRCMZ7MNHPERKMC8C6B1E");

        HashMap<String, Object> data = new HashMap<>();
        // TODO change this URL to live URL when deployed
        String url = String.format("http://localhost:8080/app/reset-password?user=%s&token=%s", mail, token);
        data.put("reset_url", url);
        message.setData(data);

        request.setMessage(message);

        SendEnhancedResponseBody response = new SendService().sendEnhancedMessage(request);
        System.out.println("Reset password send email response: " + response);
        if (response == null) {
            throw new IOException("Failed to send email");
        }
    }
}
