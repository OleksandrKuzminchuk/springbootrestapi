package spring.boot.rest.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import spring.boot.rest.api.exception.DatabaseOperationException;
import spring.boot.rest.api.exception.NotFoundException;
import spring.boot.rest.api.mapper.EventMapper;
import spring.boot.rest.api.mapper.FileMapper;
import spring.boot.rest.api.mapper.UserMapper;
import spring.boot.rest.api.model.Event;
import spring.boot.rest.api.model.File;
import spring.boot.rest.api.model.User;
import spring.boot.rest.api.repository.EventRepo;
import spring.boot.rest.api.repository.FileRepo;
import spring.boot.rest.api.repository.UserRepo;

import java.util.UUID;

import static java.lang.String.format;
import static spring.boot.rest.api.util.constant.Constants.*;

@Slf4j
public abstract class BaseService {
    private final EventRepo eventRepo;
    private final UserRepo userRepo;
    private final FileRepo fileRepo;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final FileMapper fileMapper;

    protected BaseService(EventRepo eventRepo, UserRepo userRepo, FileRepo fileRepo, EventMapper eventMapper, UserMapper userMapper, FileMapper fileMapper) {
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
        this.fileRepo = fileRepo;
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
        this.fileMapper = fileMapper;
    }

    protected EventRepo getEventRepo() {
        return eventRepo;
    }

    protected UserRepo getUserRepo() {
        return userRepo;
    }

    protected FileRepo getFileRepo() {
        return fileRepo;
    }

    protected EventMapper getEventMapper() {
        return eventMapper;
    }

    protected UserMapper getUserMapper() {
        return userMapper;
    }

    protected FileMapper getFileMapper() {
        return fileMapper;
    }

    protected Event checkIfEventExists(Long id) {
        try {
            log.info("IN isExistsEvent(id) event -> by id - '{}'...", id);
            Event foundEvent = eventRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException(format(NOT_FOUND_EVENT, id)));
            log.info("IN isExistsEvent(id) event -> by id - '{}' -> found SUCCESSFULLY", id);
            return foundEvent;
        }catch (DataAccessException e){
            log.error("IN isExistsEvent(id) event -> by id - '{}' -> FAILED", id, e);
            throw new DatabaseOperationException(format(DATABASE_OPERATION_ERROR_FAILED_TO_FIND_EVENT, id), e);
        }
    }

    protected User checkIfUserExists(Long id) {
        try {
            log.info("IN isExistsUser(id) user -> by id - '{}'...", id);
            User foundUser = userRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException(format(NOT_FOUND_USER, id)));
            log.info("IN isExistsUser(id) user -> by id - '{}' -> found SUCCESSFULLY", id);
            return foundUser;
        }catch (DataAccessException e){
            log.error("IN isExistsUser(id) user -> by id - '{}' -> FAILED", id, e);
            throw new DatabaseOperationException(format(DATABASE_OPERATION_ERROR_FAILED_TO_FIND_USER, id), e);
        }
    }

    protected File checkIfFileExists(Long id) {
        try {
            log.info("IN isExistsFile(id) file -> by id - '{}'...", id);
            File foundFile = fileRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException(format(NOT_FOUND_FILE, id)));
            log.info("IN isExistsFile(id) file -> by id - '{}' -> found SUCCESSFULLY", id);
            return foundFile;
        }catch (DataAccessException e){
            log.error("IN isExistsFile(id) file -> by id - '{}' -> FAILED", id, e);
            throw new DatabaseOperationException(format(DATABASE_OPERATION_ERROR_FAILED_TO_FIND_FILE, id), e);
        }
    }

    protected <T extends Updatable<T>> void updateFieldsIfDifferent(T target, T source){
        target.updateFrom(source);
    }
    protected String getUUIDRandomKey(){
        return UUID.randomUUID().toString();
    }
}
