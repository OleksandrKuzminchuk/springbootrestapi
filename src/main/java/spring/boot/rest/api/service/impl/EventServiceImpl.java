package spring.boot.rest.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.boot.rest.api.exception.DatabaseOperationException;
import spring.boot.rest.api.exception.NotFoundException;
import spring.boot.rest.api.model.Event;
import spring.boot.rest.api.model.Status;
import spring.boot.rest.api.repository.EventRepo;
import spring.boot.rest.api.service.EventService;
import spring.boot.rest.api.service.FileService;
import spring.boot.rest.api.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;
import static org.springframework.data.domain.Sort.Order.by;
import static spring.boot.rest.api.util.Constants.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepo eventRepo;
    private final UserService userService;
    private final FileService fileService;

    @Override
    public Event save(Event event) {
        log.info("IN save() event -> '{}'...", event);
        final var addUser = userService.checkIfUserExists(event.getUser().getId());
        final var addFile = fileService.checkIfFileExists(event.getFile().getId());
        try {
            event.setStatus(Status.ACTIVE);
            event.setUser(addUser);
            event.setFile(addFile);
            event.setCreatedAt(LocalDateTime.now());
            event.setUpdatedAt(LocalDateTime.now());
            Event savedEvent = eventRepo.save(event);
            log.info("IN save() event -> '{}' saved SUCCESSFULLY", savedEvent);
            return savedEvent;
        } catch (DataAccessException e) {
            log.error("IN save() event -> '{}' FAILED", event, e);
            throw new DatabaseOperationException(FAILED_TO_SAVE_A_EVENT + event, e);
        }
    }

    @Override
    public Event update(Event event) {
        log.info("IN update() event -> '{}'...", event);
        final var existingEvent = checkIfEventExists(event.getId());
        final var newAddUser = userService.checkIfUserExists(event.getUser().getId());
        final var newAddFile = fileService.checkIfFileExists(event.getFile().getId());
        try {
            event.setUser(newAddUser);
            event.setFile(newAddFile);
            updateFieldsIfDifferent(existingEvent, event);
            existingEvent.setUpdatedAt(LocalDateTime.now());
            final var updatedEvent = eventRepo.save(existingEvent);
            log.info("IN update() event -> '{}' updated SUCCESSFULLY", updatedEvent);
            return updatedEvent;
        } catch (DataAccessException e) {
            log.error("IN update() event -> by id - '{}' -> FAILED", event.getId(), e);
            throw new DatabaseOperationException(format(FAILED_TO_UPDATE_A_EVENT_BY_ID, event.getId()), e);
        }
    }

    @Override
    public Event findById(Long id) {
        try {
            log.info("IN findById(id) event -> by id - '{}'...", id);
            final var foundEvent = eventRepo.findById(id).orElseThrow(() -> new NotFoundException(format(NOT_FOUND_EVENT, id)));
            log.info("IN findById(id) event -> by id - '{}' found SUCCESSFULLY", id);
            return foundEvent;
        } catch (DataAccessException e) {
            log.error("IN findById(id) event -> by id - '{}' -> FAILED", id, e);
            throw new DatabaseOperationException(format(FAILED_TO_FIND_A_EVENT_BY_ID, id), e);
        }
    }

    @Override
    public List<Event> findAll() {
        try {
            log.info("IN findAll() events ->...");
            final var foundAllEvents = eventRepo.findAll(Sort.by(by(TEXT_NAME)));
            log.info("IN findAll() events -> found all events - '{}' SUCCESSFULLY", foundAllEvents.size());
            return foundAllEvents;
        } catch (DataAccessException e) {
            log.error("IN findAll() events -> FAILED", e);
            throw new DatabaseOperationException(FAILED_TO_FIND_ALL_EVENTS, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("IN deleteById(id) event -> delete by id - '{}'...", id);
        final var deleteEvent = checkIfEventExists(id);
        try {
            deleteEvent.setStatus(Status.DELETED);
            eventRepo.save(deleteEvent);
            log.info("IN deleteById(id) event -> delete by id - '{}' -> SUCCESSFULLY", id);
        } catch (DataAccessException e) {
            log.error("IN deleteById(id) event -> delete by id - '{}' -> FAILED", id);
            throw new DatabaseOperationException(format(FAILED_TO_FIND_A_EVENT_BY_ID, id), e);
        }
    }

    @Override
    public void deleteAll() {
        log.info("IN deleteAll() events -> ...");
        try {
            final var events = this.findAll();
            events.stream()
                    .filter(eventDTO -> eventDTO.getStatus().equals(Status.ACTIVE))
                    .forEach(eventDTO -> {
                        eventDTO.setStatus(Status.DELETED);
                        this.deleteById(eventDTO.getId());
                    });
            log.info("IN deleteAll() events -> SUCCESSFULLY");
        } catch (DataAccessException e) {
            log.error("IN deleteAll() events -> FAILED");
            throw new DatabaseOperationException(FAILED_TO_DELETE_ALL_EVENTS, e);
        }
    }

    private Event checkIfEventExists(Long id) {
        try {
            log.info("IN isExistsEvent(id) event -> by id - '{}'...", id);
            final var foundEvent = eventRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException(format(NOT_FOUND_EVENT, id)));
            log.info("IN isExistsEvent(id) event -> by id - '{}' -> found SUCCESSFULLY", id);
            return foundEvent;
        }catch (DataAccessException e){
            log.error("IN isExistsEvent(id) event -> by id - '{}' -> FAILED", id, e);
            throw new DatabaseOperationException(format(DATABASE_OPERATION_ERROR_FAILED_TO_FIND_EVENT, id), e);
        }
    }

    public void updateFieldsIfDifferent(Event existingEvent, Event updatedEvent) {
        if (!Objects.equals(existingEvent.getName(), updatedEvent.getName())) {
            existingEvent.setName(updatedEvent.getName());
        }
        if (!Objects.equals(existingEvent.getUser(), updatedEvent.getUser())) {
            existingEvent.setUser(updatedEvent.getUser());
        }
        if (!Objects.equals(existingEvent.getFile(), updatedEvent.getFile())) {
            existingEvent.setFile(updatedEvent.getFile());
        }
    }
}
