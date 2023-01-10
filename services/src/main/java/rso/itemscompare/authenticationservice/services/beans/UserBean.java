package rso.itemscompare.authenticationservice.services.beans;

import rso.itemscompare.authenticationservice.lib.User;
import rso.itemscompare.authenticationservice.models.converters.UserConverter;
import rso.itemscompare.authenticationservice.models.entities.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.List;

@RequestScoped
public class UserBean {
    @Inject
    private EntityManager em;

    public User getUserById(Integer userId) {
        UserEntity entity = em.find(UserEntity.class, userId);
        if (entity == null) {
            throw new NotFoundException();
        }

        return UserConverter.toDto(entity);
    }

    public User getUserByEmail(String userEmail) {
        TypedQuery<UserEntity> query = em.createNamedQuery("UserEntity.getByEmail", UserEntity.class)
                .setParameter("userEmail", userEmail);
        List<UserEntity> resultList = query.getResultList();
        if (resultList.size() == 1) {
            return UserConverter.toDto(resultList.get(0));
        }

        throw new NotFoundException();
    }

    public int addNewUser(String email, String password) {
        Query query = em.createNativeQuery("insert into rso_user (email, password) values(?email, ?password)");
        query.setParameter("email", email);
        query.setParameter("password", password);

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        int queryResult = query.executeUpdate();
        tx.commit();
        return queryResult;
    }
}
