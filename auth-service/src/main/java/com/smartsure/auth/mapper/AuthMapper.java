package com.smartsure.auth.mapper;

import com.smartsure.auth.dto.UserSummaryDTO;
import com.smartsure.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserSummaryDTO toUserSummaryDTO(User user);
}
