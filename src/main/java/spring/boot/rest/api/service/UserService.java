package spring.boot.rest.api.service;

import spring.boot.rest.api.dto.UserCreateDTO;
import spring.boot.rest.api.dto.UserDTO;
import spring.boot.rest.api.dto.UserUpdateDTO;

public interface UserService extends GenericService<UserDTO, UserCreateDTO, UserUpdateDTO, Long> {
}
