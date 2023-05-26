package spring.boot.rest.api.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import spring.boot.rest.api.dto.response.UserResponseDto;
import spring.boot.rest.api.dto.request.UserUpdateRequestDto;
import spring.boot.rest.api.model.User;

import static spring.boot.rest.api.util.Constants.TEXT_SPRING;

@Component
@Mapper(componentModel = TEXT_SPRING)
public interface UserMapper {

    @Mapping(source = "role", target = "role")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    UserResponseDto map(User user);

    @Mapping(source = "role", target = "role")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    User map(UserResponseDto userResponseDto);

    User map(UserUpdateRequestDto userUpdateRequestDto);
}


