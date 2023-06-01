package spring.boot.rest.api.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.boot.rest.api.dto.response.UserResponseDto;
import spring.boot.rest.api.dto.request.UserUpdateRequestDto;
import spring.boot.rest.api.dto.response.FileDownloadDTO;
import spring.boot.rest.api.mapper.FileMapper;
import spring.boot.rest.api.mapper.UserMapper;
import spring.boot.rest.api.service.UserService;

import java.util.List;

import static spring.boot.rest.api.util.Constants.*;

@Slf4j
@RestController
@RequestMapping(URL_API_V1_USERS)
@PreAuthorize("hasAuthority('read_write_delete:users')")
@RequiredArgsConstructor
public class UserRestControllerV1 {

    private final UserService userService;
    private final UserMapper userMapper;
    private final FileMapper fileMapper;

    @PutMapping(URL_ID)
    public ResponseEntity<UserResponseDto> update(@PathVariable(ID) Long id,
                                                  @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        log.info("IN [PUT] update() -> updating user: {}", userUpdateRequestDto);
        userUpdateRequestDto.setId(id);
        final var updatedUserResponseDto = userMapper.map(userService.update(userMapper.map(userUpdateRequestDto)));
        log.info("IN [PUT] update() -> updated user: {} -> SUCCESSFULLY", updatedUserResponseDto);
        return new ResponseEntity<>(updatedUserResponseDto, HttpStatus.OK);
    }

    @GetMapping(URL_ID)
    @PreAuthorize("hasAuthority('read:self')")
    public ResponseEntity<UserResponseDto> findById(@PathVariable(ID) Long id) {
        log.info("IN [GET] findById() -> getting user by id '{}'", id);
        final var userResponseDto = userMapper.map(userService.findById(id));
        log.info("IN [GET] findById() -> found user with id '{}' -> SUCCESSFULLY", id);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        log.info("IN [GET] findAll() -> getting all users");
        final var users = userService.findAll().stream().map(userMapper::map).toList();
        log.info("IN [GET] findAll() -> found {} users -> SUCCESSFULLY", users.size());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping(URL_ID)
    public ResponseEntity<Void> deleteById(@PathVariable(ID) Long id) {
        log.info("IN [DELETE] deleteById() -> deleting user with id '{}'", id);
        userService.deleteById(id);
        log.info("IN [DELETE] deleteById() -> deleted user with id '{}' -> SUCCESSFULLY", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        log.info("IN [DELETE] deleteAll() -> deleting all users");
        userService.deleteAll();
        log.info("IN [DELETE] deleteAll() -> all users deleted SUCCESSFULLY");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(URL_ID_FILES)
    @PreAuthorize("hasAuthority('read:self')")
    public ResponseEntity<List<FileDownloadDTO>> findFiles(@PathVariable("id") Long id){
        log.info("IN [GET] findFiles() -> finding files by user id - {}...", id);
        final var foundEvents = userService.findFiles(id).stream().map(fileMapper::mapToFileDownloadDTO).toList();
        log.info("IN [GET] findFiles() -> finding files by user id - {} -> SUCCESSFULLY", id);
        return new ResponseEntity<>(foundEvents, HttpStatus.OK);
    }
}
