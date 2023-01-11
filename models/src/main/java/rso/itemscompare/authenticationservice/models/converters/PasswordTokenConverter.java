package rso.itemscompare.authenticationservice.models.converters;

import rso.itemscompare.authenticationservice.lib.PasswordToken;
import rso.itemscompare.authenticationservice.models.entities.PasswordTokenEntity;

public class PasswordTokenConverter {
    public static PasswordToken toDto(PasswordTokenEntity entity) {
        PasswordToken dto = new PasswordToken();
        dto.setUserEmail(entity.getUserEmail());
        dto.setToken(entity.getToken());

        return dto;
    }

    public static PasswordTokenEntity toEntity(PasswordToken dto) {
        PasswordTokenEntity entity = new PasswordTokenEntity();
        entity.setUserEmail(dto.getUserEmail());
        entity.setToken(dto.getToken());

        return entity;
    }
}
