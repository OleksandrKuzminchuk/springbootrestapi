package spring.boot.rest.api.service;

import spring.boot.rest.api.dto.EventDTO;
import spring.boot.rest.api.dto.UserCreateDTO;
import spring.boot.rest.api.dto.UserDTO;
import spring.boot.rest.api.dto.UserUpdateDTO;

import java.util.List;

public interface UserService extends GenericService<UserDTO, UserCreateDTO, UserUpdateDTO, Long> {
    List<EventDTO> findEvents(Long id);
}
