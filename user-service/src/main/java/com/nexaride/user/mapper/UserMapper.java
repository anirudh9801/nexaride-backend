package com.nexaride.user.mapper;

import com.nexaride.user.dto.RegisterRequest;
import com.nexaride.user.dto.UpdateProfileRequest;
import com.nexaride.user.dto.UserResponse;
import com.nexaride.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true) // Mapping ignored because id wll be gen by Db
    @Mapping(target = "userCode", ignore = true) // It will be handled by Redis in future
    @Mapping(target = "createdAt",ignore = true)//will be handled by Service
    @Mapping(target = "updatedAt", ignore = true)//Handled by service
    User toEntity(RegisterRequest request);

    UserResponse toResponse(User user);


    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userCode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateUserFromDto(UpdateProfileRequest request, @MappingTarget User user);
}
