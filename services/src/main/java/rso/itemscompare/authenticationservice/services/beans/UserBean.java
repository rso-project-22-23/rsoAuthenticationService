package rso.itemscompare.authenticationservice.services.beans;

import models.SendEnhancedRequestBody;
import models.SendEnhancedResponseBody;
import models.SendRequestMessage;
import rso.itemscompare.authenticationservice.lib.User;
import rso.itemscompare.authenticationservice.models.converters.UserConverter;
import rso.itemscompare.authenticationservice.models.entities.UserEntity;
import rso.itemscompare.authenticationservice.services.clients.CourierClient;
import services.Courier;
import services.SendService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.*;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RequestScoped
public class UserBean {
    @Inject
    private EntityManager em;

    @Inject
    private CourierClient cc;

    @Inject
    private RegistrationTokenBean registrationTokenBean;

    public User getUserById(Integer userId) {
        UserEntity entity = em.find(UserEntity.class, userId);
        if (entity == null) {
            throw new NotFoundException();
        }

        em.refresh(entity);
        return UserConverter.toDto(entity);
    }

    public User getUserByEmail(String userEmail) {
        TypedQuery<UserEntity> query = em.createNamedQuery("UserEntity.getByEmail", UserEntity.class)
                .setParameter("userEmail", userEmail);
        List<UserEntity> resultList = query.getResultList();
        if (resultList.size() == 1) {
            UserEntity entity = resultList.get(0);
            em.refresh(entity);
            return UserConverter.toDto(entity);
        }

        throw new NotFoundException();
    }

    public int addNewUser(String email, String password, String registrationToken) throws IOException {
        Query query = em.createNativeQuery("insert into rso_user (email, password) values(?email, ?password)");
        query.setParameter("email", email);
        query.setParameter("password", password);

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        int queryResult = query.executeUpdate();
        int saveTokenResult = 0;
        if (queryResult == 1) {
            saveTokenResult = registrationTokenBean.saveTokenForUser(email, registrationToken);
            if (saveTokenResult == 1) {
                cc.sendRegistrationMail(email, registrationToken);
            }
        }
        tx.commit();

        return saveTokenResult;
    }

    public int activateUser(int userId, String userEmail) {
        Query query = em.createNativeQuery("update rso_user set activated = true where id = ?userId");
        query.setParameter("userId", userId);

        EntityTransaction tx = em.getTransaction();
        int deleteTokenResult = 0;
        tx.begin();
        int queryResult = query.executeUpdate();
        if (queryResult == 1) {
            deleteTokenResult = registrationTokenBean.deleteTokenForUser(userEmail);
        }
        tx.commit();
        return deleteTokenResult;
    }
}
