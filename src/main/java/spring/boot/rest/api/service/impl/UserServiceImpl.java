package spring.boot.rest.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.boot.rest.api.exception.AccessDeniedException;
import spring.boot.rest.api.exception.DatabaseOperationException;
import spring.boot.rest.api.exception.NotFoundException;
import spring.boot.rest.api.model.*;
import spring.boot.rest.api.repository.UserRepo;
import spring.boot.rest.api.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.data.domain.Sort.Order.by;
import static spring.boot.rest.api.util.Constants.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo repo;

    @Override
    public User save(User user) {
        try {
            log.info("IN save() user -> '{}'...", user);
            user.setStatus(Status.ACTIVE);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setPassword(user.getPassword());
            final var savedUser = repo.save(user);
            log.info("IN save() user -> '{}' -> saved SUCCESSFULLY", savedUser);
            return savedUser;
        } catch (DataAccessException e) {
            log.error("IN save() user -> '{}' -> FAILED", user, e);
            throw new DatabaseOperationException(FAILED_TO_SAVE_USER, e);
        }
    }

    @Override
    public User update(User user) {
        log.info("IN update() user -> '{}'...", user);
        final var existingUser = checkIfUserExists(user.getId());
        try {
            updateFieldsIfDifferent(existingUser, user);
            existingUser.setUpdatedAt(LocalDateTime.now());
            User updatedUser = repo.save(existingUser);
            log.info("IN update() user -> '{}' -> updated SUCCESSFULLY", updatedUser);
            return updatedUser;
        } catch (DataAccessException e) {
            log.error("IN update() user -> '{}' -> FAILED", user);
            throw new DatabaseOperationException(format(FAILED_TO_UPDATE_USER, user.getId()), e);
        }
    }

    @Override
    public User findById(Long id) {
        log.info("IN findById() - find user with id '{}'...", id);
        final var user = checkIfUserExists(id);
        log.debug("IN findById() - checking user's status for access");
        var result = isUserDeletedAndCurrentUserNotAdminOrModerator(id, user);
        log.info("IN findById() - find user with id '{}' -> found SUCCESSFULLY", id);
        return result;
    }


    @Override
    public List<User> findAll() {
        try {
            log.info("IN findAll() users ->...");
            final var foundAllUsers = repo.findAll(Sort.by(by(FIELD_NAME_FIRST_NAME)));
            log.info("IN findAll() users -> found all users - '{}' SUCCESSFULLY", foundAllUsers.size());
            return foundAllUsers;
        } catch (DataAccessException e) {
            log.error("IN findAll() users -> FAILED", e);
            throw new DatabaseOperationException(FAILED_TO_FIND_ALL_USERS, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("IN deleteById(id) user -> delete by id - '{}'...", id);
        final var deleteUser = checkIfUserExists(id);
        try {
            deleteUser.setStatus(Status.DELETED);
            repo.save(deleteUser);
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
            final var foundAllUsers = this.findAll();
            foundAllUsers.stream()
                    .filter(user -> user.getStatus().equals(Status.ACTIVE))
                    .forEach(user -> deleteById(user.getId()));
            log.info("IN deleteAll() users -> SUCCESSFULLY");
        } catch (DataAccessException e) {
            log.error("IN deleteAll() users -> FAILED");
            throw new DatabaseOperationException(FAILED_TO_DELETE_ALL_USERS, e);
        }
    }

    @Override
    public List<File> findFiles(Long id) {
        log.info("IN findFiles(id) files -> find files by user id - '{}'...", id);
        final var foundUser = checkIfUserExists(id);
        try {
            log.debug("IN findFiles() - checking user's status for access");
            isUserDeletedAndCurrentUserNotAdminOrModerator(id, foundUser);
            List<File> foundFiles = foundUser.getEvents().stream()
                    .filter(event -> Objects.equals(event.getStatus(), Status.ACTIVE))
                    .map(Event::getFile)
                    .map(file -> File.builder().location(file.getLocation()).build())
                    .collect(Collectors.toList());
            log.info("IN findFiles(id) files -> find files by user id - '{}' -> SUCCESSFULLY", id);
            return foundFiles;
        } catch (DataAccessException e) {
            log.error("IN findFiles(id) files -> find files by user id - '{}' -> FAILED", id);
            throw new DatabaseOperationException(format(FAILED_TO_FILES_BY_USER_ID, id), e);
        }
    }

    @Override
    public User checkIfUserExists(Long id) {
        try {
            log.info("IN isExistsUser(id) user -> by id - '{}'...", id);
            final var foundUser = repo.findById(id)
                    .orElseThrow(() -> new NotFoundException(format(NOT_FOUND_USER, id)));
            log.info("IN isExistsUser(id) user -> by id - '{}' -> found SUCCESSFULLY", id);
            return foundUser;
        }catch (DataAccessException e){
            log.error("IN isExistsUser(id) user -> by id - '{}' -> FAILED", id, e);
            throw new DatabaseOperationException(format(DATABASE_OPERATION_ERROR_FAILED_TO_FIND_USER, id), e);
        }
    }

    @Override
    public boolean currentUserIsModeratorOrAdmin() {
        return getUserDetails().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equalsIgnoreCase(Permission.READ_WRITE_DELETE_USERS.getName()));
    }

    private UserDetails getUserDetails() {
        final var auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetails) auth.getPrincipal();
    }

    private User isUserDeletedAndCurrentUserNotAdminOrModerator(Long id, User user) {

        if(currentUserIsModeratorOrAdmin()){
            return user;
        }
        else if (user.getStatus().equals(Status.DELETED)) {
            log.error("IN findById() - access denied: user with id '{}' is DELETED", id);
            throw new AccessDeniedException(format(ACCESS_DENIED_USER_DELETED, id));
        }
        else if(!getUserDetails().getUsername().equals(user.getEmail())){
            log.error("IN findById() - access denied: user with id '{}' tried to access another user", id);
            throw new AccessDeniedException(ACCESS_DENIED);
        }
        else {
            return User.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .build();
        }
    }

    private void updateFieldsIfDifferent(User existingUser, User updatedUser) {
        if (!Objects.equals(existingUser.getFirstName(), updatedUser.getFirstName())){
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        if (!Objects.equals(existingUser.getLastName(), updatedUser.getLastName())){
            existingUser.setLastName(updatedUser.getLastName());
        }
        if (!Objects.equals(existingUser.getEmail(), updatedUser.getEmail())){
            existingUser.setEmail(updatedUser.getEmail());
        }
    }
}
