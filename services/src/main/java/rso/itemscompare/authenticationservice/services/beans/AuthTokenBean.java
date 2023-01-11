package rso.itemscompare.authenticationservice.services.beans;

import rso.itemscompare.authenticationservice.lib.AuthToken;
import rso.itemscompare.authenticationservice.models.converters.AuthTokenConverter;
import rso.itemscompare.authenticationservice.models.entities.AuthTokenEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.ws.rs.NotFoundException;

@RequestScoped
public class AuthTokenBean {
    public static final int ALREADY_LOGGED_IN = -99;
    public static final int ALREADY_LOGGED_OUT = -98;
    @Inject
    private EntityManager em;

    public AuthToken getToken(Integer userId) {
        AuthTokenEntity entity = em.find(AuthTokenEntity.class, userId);
        if (entity == null) {
            throw new NotFoundException();
        }

        em.refresh(entity);
        return AuthTokenConverter.toDto(entity);
    }

    public int saveTokenForUser(int userId, String token) {
        try {
            getToken(userId);
            return ALREADY_LOGGED_IN;
        } catch (NotFoundException e) {
            Query query = em.createNativeQuery("insert into auth_token (user_id, token) values(?userId, ?token)");
            query.setParameter("userId", userId);
            query.setParameter("token", token);
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            int queryResult = query.executeUpdate();
            tx.commit();
            return queryResult;
        }
    }

    public int deleteTokenForUser(int userId) {
        AuthTokenEntity tokenEntity = em.find(AuthTokenEntity.class, userId);
        if (tokenEntity == null) {
            return ALREADY_LOGGED_OUT;
        }
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(tokenEntity);
        em.flush();
        tx.commit();
        return 1;
    }
}
