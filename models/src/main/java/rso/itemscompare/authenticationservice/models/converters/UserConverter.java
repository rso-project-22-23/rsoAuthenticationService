package rso.itemscompare.authenticationservice.models.converters;

import rso.itemscompare.authenticationservice.lib.User;
import rso.itemscompare.authenticationservice.models.entities.UserEntity;

public class UserConverter {
    public static User toDto(UserEntity entity) {
        User dto = new User();
        dto.setUserId(entity.getId());
        dto.setUserEmail(entity.getEmail());
        dto.setUserPassword(entity.getPassword());
        dto.setActivated(entity.getActivated());

        return dto;
    }

    public static UserEntity toEntity(User dto) {
        UserEntity entity = new UserEntity();
        entity.setId(dto.getUserId());
        entity.setEmail(dto.getUserEmail());
        entity.setPassword(dto.getUserPassword());
        entity.setActivated(dto.getActivated());

        return entity;
    }
}
