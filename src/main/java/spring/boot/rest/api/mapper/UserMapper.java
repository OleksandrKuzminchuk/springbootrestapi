package spring.boot.rest.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import spring.boot.rest.api.dto.UserCreateDTO;
import spring.boot.rest.api.dto.UserDTO;
import spring.boot.rest.api.dto.UserUpdateDTO;
import spring.boot.rest.api.model.User;

import static spring.boot.rest.api.util.constant.Constants.TEXT_SPRING;
import static spring.boot.rest.api.util.constant.Constants.TEXT_STATUS;

@Component
@Mapper(componentModel = TEXT_SPRING)
public interface UserMapper {

    @Mapping(source = TEXT_STATUS, target = TEXT_STATUS)
    UserDTO map(User user);

    User map(UserDTO userDto);

    User map(UserCreateDTO userCreateDTO);

    User map(UserUpdateDTO userUpdateDTO);
}
