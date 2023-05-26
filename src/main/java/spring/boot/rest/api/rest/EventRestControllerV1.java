package spring.boot.rest.api.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.boot.rest.api.dto.response.EventResponseDto;
import spring.boot.rest.api.dto.request.EventCreateRequestDto;
import spring.boot.rest.api.dto.request.EventUpdateRequestDto;
import spring.boot.rest.api.mapper.EventMapper;
import spring.boot.rest.api.service.EventService;

import java.util.List;
import java.util.stream.Collectors;

import static spring.boot.rest.api.util.Constants.*;

@Slf4j
@RestController
@RequestMapping(URL_API_V1_EVENTS)
@PreAuthorize("hasAuthority('read_write_delete:events')")
@RequiredArgsConstructor
public class EventRestControllerV1 {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping
    public ResponseEntity<EventResponseDto> create(@RequestBody EventCreateRequestDto eventCreateRequestDto) {
        log.info("IN [POST] create() -> creating event: {}", eventCreateRequestDto);
        final var createdEventResponseDto = eventMapper.map(eventService.save(eventMapper.map(eventCreateRequestDto)));
        log.info("IN [POST] create() -> created event: {} -> SUCCESSFULLY", createdEventResponseDto);
        return new ResponseEntity<>(createdEventResponseDto, HttpStatus.CREATED);
    }

    @PutMapping(URL_ID)
    public ResponseEntity<EventResponseDto> update(@PathVariable(ID) Long id,
                                                   @RequestBody EventUpdateRequestDto eventUpdateRequestDto) {
        log.info("IN update() - updating event: {}", eventUpdateRequestDto);
        eventUpdateRequestDto.setId(id);
        final var updatedEventResponseDto = eventMapper.map(eventService.update(eventMapper.map(eventUpdateRequestDto)));
        log.info("IN update() - event updated: {} -> SUCCESSFULLY", updatedEventResponseDto);
        return new ResponseEntity<>(updatedEventResponseDto, HttpStatus.OK);
    }

    @GetMapping(URL_ID)
    public ResponseEntity<EventResponseDto> findById(@PathVariable(ID) Long id) {
        log.info("IN getById() - getting event by id '{}'", id);
        final var eventResponseDto = eventMapper.map(eventService.findById(id));
        log.info("IN getById() - event with id '{}' found: {} -> SUCCESSFULLY", id, eventResponseDto);
        return new ResponseEntity<>(eventResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDto>> findAll() {
        log.info("IN getAll() - getting all events");
        final var events = eventService.findAll().stream().map(eventMapper::map).toList();
        log.info("IN getAll() - found {} events: {} -> SUCCESSFULLY", events.size(), events);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @DeleteMapping(URL_ID)
    public ResponseEntity<Void> deleteById(@PathVariable(ID) Long id) {
        log.info("IN deleteById() - deleting event with id '{}'", id);
        eventService.deleteById(id);
        log.info("IN deleteById() - event with id '{}' deleted SUCCESSFULLY", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        log.info("IN deleteAll() - deleting all events");
        eventService.deleteAll();
        log.info("IN deleteAll() - all events deleted SUCCESSFULLY");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
