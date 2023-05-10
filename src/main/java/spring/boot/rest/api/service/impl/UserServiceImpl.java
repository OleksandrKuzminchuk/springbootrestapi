package spring.boot.rest.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.boot.rest.api.dto.UserCreateDTO;
import spring.boot.rest.api.dto.UserDTO;
import spring.boot.rest.api.dto.UserUpdateDTO;
import spring.boot.rest.api.exception.AccessDeniedException;
import spring.boot.rest.api.exception.DatabaseOperationException;
import spring.boot.rest.api.mapper.EventMapper;
import spring.boot.rest.api.mapper.FileMapper;
import spring.boot.rest.api.mapper.UserMapper;
import spring.boot.rest.api.model.Permission;
import spring.boot.rest.api.model.Status;
import spring.boot.rest.api.model.User;
import spring.boot.rest.api.repository.EventRepo;
import spring.boot.rest.api.repository.FileRepo;
import spring.boot.rest.api.repository.UserRepo;
import spring.boot.rest.api.service.BaseService;
import spring.boot.rest.api.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.data.domain.Sort.Order.by;
import static spring.boot.rest.api.util.constant.Constants.*;

@Slf4j
@Service
@Transactional
public class UserServiceImpl extends BaseService implements UserService {

    public UserServiceImpl(EventRepo eventRepo, UserRepo userRepo, FileRepo fileRepo,
                           EventMapper eventMapper, UserMapper userMapper, FileMapper fileMapper) {
        super(eventRepo, userRepo, fileRepo, eventMapper, userMapper, fileMapper);
    }

    @Override
    public UserDTO save(UserCreateDTO userCreateDTO) {
        try {
            log.info("IN save() user -> '{}'...", userCreateDTO);
            User saveUser = getUserMapper().map(userCreateDTO);
            saveUser.setStatus(Status.ACTIVE);
            User savedUser = getUserRepo().save(saveUser);
            UserDTO savedUserDTO = getUserMapper().map(savedUser);
            log.info("IN save() user -> '{}' -> saved SUCCESSFULLY", savedUserDTO);
            return savedUserDTO;
        } catch (DataAccessException e) {
            log.error("IN save() user -> '{}' -> FAILED", userCreateDTO, e);
            throw new DatabaseOperationException(FAILED_TO_SAVE_USER, e);
        }
    }

    @Override
    public UserDTO update(UserUpdateDTO userUpdateDTO) {
        log.info("IN update() user -> '{}'...", userUpdateDTO);
        User updateUser = getUserMapper().map(userUpdateDTO);
        User existingUser = checkIfUserExists(updateUser.getId());
        try {
            updateFieldsIfDifferent(existingUser, updateUser);
            User updatedUser = getUserRepo().save(existingUser);
            UserDTO updatedUserDTO = getUserMapper().map(updatedUser);
            log.info("IN update() user -> '{}' -> updated SUCCESSFULLY", updatedUserDTO);
            return updatedUserDTO;
        } catch (DataAccessException e) {
            log.error("IN update() user -> '{}' -> FAILED", userUpdateDTO);
            throw new DatabaseOperationException(format(FAILED_TO_UPDATE_USER, userUpdateDTO.getId()), e);
        }
    }

    @Override
    public UserDTO findById(Long id) {
        log.info("IN findById() - find user with id '{}'...", id);
        User foundUser = checkIfUserExists(id);
        log.debug("IN findById() - checking user's status for access");
        if (foundUser.getStatus().equals(Status.DELETED) && !currentUserIsModeratorOrAdmin()) {
            log.error("IN findById() - access denied: user with id '{}' is DELETED", id);
            throw new AccessDeniedException(format(ACCESS_DENIED_USER_DELETED, id));
        }
        UserDTO foundUserDTO = getUserMapper().map(foundUser);
        log.info("IN findById() - find user with id '{}' -> found SUCCESSFULLY", id);
        return foundUserDTO;
    }


    @Override
    public List<UserDTO> findAll() {
        try {
            log.info("IN findAll() users ->...");
            List<User> foundAllUsers = getUserRepo().findAll(Sort.by(by("firstName")));
            log.info("IN findAll() users -> found all users - '{}' SUCCESSFULLY", foundAllUsers.size());
            return foundAllUsers.stream().map(getUserMapper()::map).collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("IN findAll() users -> FAILED", e);
            throw new DatabaseOperationException(FAILED_TO_FIND_ALL_USERS, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("IN deleteById(id) user -> delete by id - '{}'...", id);
        User deleteUser = checkIfUserExists(id);
        try {
            deleteUser.setStatus(Status.DELETED);
            getUserRepo().save(deleteUser);
            log.info("IN deleteById(id) user -> delete by id - '{}' -> SUCCESSFULLY", id);
        } catch (DataAccessException e) {
            log.error("IN deleteById(id) user -> delete by id - '{}' -> FAILED", id);
            throw new DatabaseOperationException(format(FAILED_TO_DELETE_USER_BY_ID, id), e);
        }
    }

    @Override
    public void deleteAll() {
        log.info("IN deleteAll() users -> ...");
        try {
            List<UserDTO> foundAllUsers = this.findAll();
            foundAllUsers.stream()
                    .filter(userDTO -> userDTO.getStatus().equals(Status.ACTIVE))
                    .forEach(userDTO -> {
                        userDTO.setStatus(Status.DELETED);
                        this.deleteById(userDTO.getId());
                    });
            log.info("IN deleteAll() users -> SUCCESSFULLY");
        } catch (DataAccessException e) {
            log.error("IN deleteAll() users -> FAILED");
            throw new DatabaseOperationException(FAILED_TO_DELETE_ALL_USERS, e);
        }
    }

    private boolean currentUserIsModeratorOrAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equalsIgnoreCase(Permission.READ_WRITE_DELETE_USERS.getPermission()));
    }
}
