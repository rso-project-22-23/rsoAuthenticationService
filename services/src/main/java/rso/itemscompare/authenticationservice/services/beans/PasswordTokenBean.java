package rso.itemscompare.authenticationservice.services.beans;

import rso.itemscompare.authenticationservice.lib.PasswordToken;
import rso.itemscompare.authenticationservice.models.converters.PasswordTokenConverter;
import rso.itemscompare.authenticationservice.models.entities.PasswordTokenEntity;
import rso.itemscompare.authenticationservice.services.clients.CourierClient;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.ws.rs.NotFoundException;
import java.io.IOException;

@RequestScoped
public class PasswordTokenBean {
    public static final int NOT_EXIST = -99;
    @Inject
    private EntityManager em;

    @Inject
    private CourierClient cc;

    public PasswordToken getToken(String userEmail) {
        PasswordTokenEntity entity = em.find(PasswordTokenEntity.class, userEmail);
        if (entity == null) {
            throw new NotFoundException();
        }

        em.refresh(entity);
        return PasswordTokenConverter.toDto(entity);
    }

    public int saveTokenForUser(String userEmail, String token) throws IOException {
        Query query;
        try {
            getToken(userEmail);
            query = em.createNativeQuery("update password_token set token = ?token where user_email = ?userEmail");
        } catch (NotFoundException e) {
            query = em.createNativeQuery("insert into password_token (user_email, token) values(?userEmail, ?token)");
        }
        query.setParameter("userId", userEmail);
        query.setParameter("token", token);
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        int queryResult = query.executeUpdate();
        if (queryResult == 1) {
            cc.sendRegistrationMail(userEmail, token);
        }
        tx.commit();
        return queryResult;
    }

    public int deleteTokenForUser(int userId) {
        PasswordTokenEntity tokenEntity = em.find(PasswordTokenEntity.class, userId);
        if (tokenEntity == null) {
            return NOT_EXIST;
        }
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(tokenEntity);
        em.flush();
        tx.commit();
        return 1;
    }
}
