package com.samuel_mc.pickados_api.dto.mappers;

import com.samuel_mc.pickados_api.dto.RegisterTipsterRequestDTO;
import com.samuel_mc.pickados_api.dto.RegisterUserRequestDTO;
import com.samuel_mc.pickados_api.entity.TipsterProfileEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCIA= Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "profilePhotoKey", ignore = true)
    @Mapping(target = "preferredCompetitions", ignore = true)
    @Mapping(target = "preferredTeams", ignore = true)
    UserEntity registerUserRequestDTOToUserEntity(RegisterUserRequestDTO registerUserRequestDTO);

    RegisterUserRequestDTO registerTipsterRequestDTOtoRegisterUserRequestDTO(RegisterTipsterRequestDTO registerTipsterRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "validated", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "user", ignore = true)
    TipsterProfileEntity registerTipsterRequestDTOtoTipsterProfileEntity(RegisterTipsterRequestDTO registerTipsterRequestDTO);

    @Mapping(target = "role", source = "role.name")
    com.samuel_mc.pickados_api.dto.UserResponseDTO userEntityToUserResponseDTO(UserEntity userEntity);
}
