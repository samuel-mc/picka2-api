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

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    UserEntity registerUserRequestDTOToUserEntity(RegisterUserRequestDTO registerUserRequestDTO);

    RegisterUserRequestDTO registerTipsterRequestDTOtoRegisterUserRequestDTO(RegisterTipsterRequestDTO registerTipsterRequestDTO);

    @Mapping(target = "user", ignore = true)
    TipsterProfileEntity registerTipsterRequestDTOtoTipsterProfileEntity(RegisterTipsterRequestDTO registerTipsterRequestDTO);

    com.samuel_mc.pickados_api.dto.UserResponseDTO userEntityToUserResponseDTO(UserEntity userEntity);
}
