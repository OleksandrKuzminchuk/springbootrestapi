package spring.boot.rest.api.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.boot.rest.api.dto.UserCreateDTO;
import spring.boot.rest.api.dto.UserDTO;
import spring.boot.rest.api.dto.UserUpdateDTO;
import spring.boot.rest.api.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasAuthority('read_write_delete:users')")
public class UserRestControllerV1 {

    private final UserService userService;

    @Autowired
    public UserRestControllerV1(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody UserCreateDTO userCreateDTO) {
        log.info("IN [POST] create() -> creating user: {}", userCreateDTO);
        UserDTO createdUserDTO = userService.save(userCreateDTO);
        log.info("IN [POST] create() -> created user: {} -> SUCCESSFULLY", createdUserDTO);
        return new ResponseEntity<>(createdUserDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable("id") Long id,
                                          @RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("IN [PUT] update() -> updating user: {}", userUpdateDTO);
        userUpdateDTO.setId(id);
        UserDTO updatedUserDTO = userService.update(userUpdateDTO);
        log.info("IN [PUT] update() -> updated user: {} -> SUCCESSFULLY", updatedUserDTO);
        return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('read:self')")
    public ResponseEntity<UserDTO> findById(@PathVariable("id") Long id) {
        log.info("IN [GET] findById() -> getting user by id '{}'", id);
        UserDTO userDTO = userService.findById(id);
        log.info("IN [GET] findById() -> found user with id '{}' -> SUCCESSFULLY", id);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll() {
        log.info("IN [GET] findAll() -> getting all users");
        List<UserDTO> users = userService.findAll();
        log.info("IN [GET] findAll() -> found {} users -> SUCCESSFULLY", users.size());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        log.info("IN [DELETE] deleteById() -> deleting user with id '{}'", id);
        userService.deleteById(id);
        log.info("IN [DELETE] deleteById() -> deleted user with id '{}' -> SUCCESSFULLY", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        log.info("IN [DELETE] deleteAll() -> deleting all users");
        userService.deleteAll();
        log.info("IN [DELETE] deleteAll() -> all users deleted SUCCESSFULLY");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
