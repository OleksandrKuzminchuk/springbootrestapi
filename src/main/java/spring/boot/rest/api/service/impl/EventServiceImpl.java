package spring.boot.rest.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.boot.rest.api.dto.EventCreateDTO;
import spring.boot.rest.api.dto.EventDTO;
import spring.boot.rest.api.dto.EventUpdateDTO;
import spring.boot.rest.api.exception.DatabaseOperationException;
import spring.boot.rest.api.exception.NotFoundException;
import spring.boot.rest.api.mapper.EventMapper;
import spring.boot.rest.api.mapper.FileMapper;
import spring.boot.rest.api.mapper.UserMapper;
import spring.boot.rest.api.model.Event;
import spring.boot.rest.api.model.File;
import spring.boot.rest.api.model.Status;
import spring.boot.rest.api.model.User;
import spring.boot.rest.api.repository.EventRepo;
import spring.boot.rest.api.repository.FileRepo;
import spring.boot.rest.api.repository.UserRepo;
import spring.boot.rest.api.service.BaseService;
import spring.boot.rest.api.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.data.domain.Sort.Order.by;
import static spring.boot.rest.api.util.constant.Constants.*;

@Slf4j
@Service
@Transactional
public class EventServiceImpl extends BaseService implements EventService {

    public EventServiceImpl(EventRepo eventRepo, UserRepo userRepo, FileRepo fileRepo,
                            EventMapper eventMapper, UserMapper userMapper, FileMapper fileMapper) {
        super(eventRepo, userRepo, fileRepo, eventMapper, userMapper, fileMapper);
    }

    @Override
    public EventDTO save(EventCreateDTO eventCreateDTO) {
        log.info("IN save() event -> '{}'...", eventCreateDTO);
        User addUser = checkIfUserExists(eventCreateDTO.getUserId());
        File addFile = checkIfFileExists(eventCreateDTO.getFileId());
        try {
            Event saveEvent = getEventMapper().map(eventCreateDTO);
            saveEvent.setStatus(Status.ACTIVE);
            saveEvent.setUser(addUser);
            saveEvent.setFile(addFile);
            saveEvent.setCreatedAt(LocalDateTime.now());
            saveEvent.setUpdatedAt(LocalDateTime.now());
            Event savedEvent = getEventRepo().save(saveEvent);
            EventDTO savedEventDTO = getEventMapper().map(savedEvent);
            log.info("IN save() event -> '{}' saved SUCCESSFULLY", savedEventDTO);
            return savedEventDTO;
        } catch (DataAccessException e) {
            log.error("IN save() event -> '{}' FAILED", eventCreateDTO, e);
            throw new DatabaseOperationException(FAILED_TO_SAVE_A_EVENT + eventCreateDTO, e);
        }
    }

    @Override
    public EventDTO update(EventUpdateDTO eventUpdateDTO) {
        log.info("IN update() event -> '{}'...", eventUpdateDTO);
        Event existingEvent = checkIfEventExists(eventUpdateDTO.getId());
        User newAddUser = checkIfUserExists(eventUpdateDTO.getUserId());
        File newAddFile = checkIfFileExists(eventUpdateDTO.getFileId());
        try {
            Event updateEvent = getEventMapper().map(eventUpdateDTO);
            updateEvent.setUser(newAddUser);
            updateEvent.setFile(newAddFile);
            updateFieldsIfDifferent(existingEvent, updateEvent);
            existingEvent.setUpdatedAt(LocalDateTime.now());
            Event updatedEvent = getEventRepo().save(existingEvent);
            EventDTO updatedEventDTO = getEventMapper().map(updatedEvent);
            log.info("IN update() event -> '{}' updated SUCCESSFULLY", updatedEvent);
            return updatedEventDTO;
        } catch (DataAccessException e) {
            log.error("IN update() event -> by id - '{}' -> FAILED", eventUpdateDTO.getId(), e);
            throw new DatabaseOperationException(format(FAILED_TO_UPDATE_A_EVENT_BY_ID, eventUpdateDTO.getId()), e);
        }
    }

    @Override
    public EventDTO findById(Long id) {
        try {
            log.info("IN findById(id) event -> by id - '{}'...", id);
            Event foundEvent = getEventRepo().findById(id).orElseThrow(() -> new NotFoundException(format(NOT_FOUND_EVENT, id)));
            EventDTO foundEventDTO = getEventMapper().map(foundEvent);
            log.info("IN findById(id) event -> by id - '{}' found SUCCESSFULLY", id);
            return foundEventDTO;
        } catch (DataAccessException e) {
            log.error("IN findById(id) event -> by id - '{}' -> FAILED", id, e);
            throw new DatabaseOperationException(format(FAILED_TO_FIND_A_EVENT_BY_ID, id), e);
        }
    }

    @Override
    public List<EventDTO> findAll() {
        try {
            log.info("IN findAll() events ->...");
            List<Event> foundAllEvents = getEventRepo().findAll(Sort.by(by(TEXT_NAME)));
            log.info("IN findAll() events -> found all events - '{}' SUCCESSFULLY", foundAllEvents.size());
            return foundAllEvents.stream().map(getEventMapper()::map).collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("IN findAll() events -> FAILED", e);
            throw new DatabaseOperationException(FAILED_TO_FIND_ALL_EVENTS, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("IN deleteById(id) event -> delete by id - '{}'...", id);
        Event deleteEvent = checkIfEventExists(id);
        try {
            deleteEvent.setStatus(Status.DELETED);
            getEventRepo().save(deleteEvent);
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
            List<EventDTO> eventsDTO = this.findAll();
            eventsDTO.stream()
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
}
