package rso.itemscompare.authenticationservice.models.converters;

import rso.itemscompare.authenticationservice.lib.AuthToken;
import rso.itemscompare.authenticationservice.models.entities.AuthTokenEntity;

public class AuthTokenConverter {
    public static AuthToken toDto(AuthTokenEntity entity) {
        AuthToken dto = new AuthToken();
        dto.setUserId(entity.getUserId());
        dto.setToken(entity.getToken());

        return dto;
    }

    public static AuthTokenEntity toEntity(AuthToken dto) {
        AuthTokenEntity entity = new AuthTokenEntity();
        entity.setUserId(dto.getUserId());
        entity.setToken(dto.getToken());

        return entity;
    }
}
