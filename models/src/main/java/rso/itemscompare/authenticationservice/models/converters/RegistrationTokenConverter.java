package rso.itemscompare.authenticationservice.models.converters;

import rso.itemscompare.authenticationservice.lib.RegistrationToken;
import rso.itemscompare.authenticationservice.models.entities.RegistrationTokenEntity;

public class RegistrationTokenConverter {
    public static RegistrationToken toDto(RegistrationTokenEntity entity) {
        RegistrationToken dto = new RegistrationToken();
        dto.setUserEmail(entity.getUserEmail());
        dto.setToken(entity.getToken());

        return dto;
    }

    public static RegistrationTokenEntity toEntity(RegistrationToken dto) {
        RegistrationTokenEntity entity = new RegistrationTokenEntity();
        entity.setUserEmail(dto.getUserEmail());
        entity.setToken(dto.getToken());

        return entity;
    }
}
