package rso.itemscompare.authenticationservice.services.beans;

import rso.itemscompare.authenticationservice.lib.RegistrationToken;
import rso.itemscompare.authenticationservice.models.converters.RegistrationTokenConverter;
import rso.itemscompare.authenticationservice.models.entities.RegistrationTokenEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.NotFoundException;

@RequestScoped
public class RegistrationTokenBean {
    public static final int NOT_EXIST = -99;
    @Inject
    private EntityManager em;

    public RegistrationToken getToken(String userEmail) {
        RegistrationTokenEntity entity = em.find(RegistrationTokenEntity.class, userEmail);
        if (entity == null) {
            throw new NotFoundException();
        }

        em.refresh(entity);
        return RegistrationTokenConverter.toDto(entity);
    }

    public int saveTokenForUser(String userEmail, String token) {
        Query query;
        try {
            getToken(userEmail);
            query = em.createNativeQuery("update registration_token set token = ?token where user_email = ?userEmail");
        } catch (NotFoundException e) {
            query = em.createNativeQuery("insert into registration_token (user_email, token) values(?userEmail, ?token)");
        }
        query.setParameter("userEmail", userEmail);
        query.setParameter("token", token);

        return query.executeUpdate();
    }

    public int deleteTokenForUser(String userEmail) {
        RegistrationTokenEntity tokenEntity = em.find(RegistrationTokenEntity.class, userEmail);
        if (tokenEntity == null) {
            return NOT_EXIST;
        }
        em.remove(tokenEntity);
        em.flush();
        return 1;
    }
}
