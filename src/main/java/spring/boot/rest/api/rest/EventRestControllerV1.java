package spring.boot.rest.api.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.boot.rest.api.dto.EventCreateDTO;
import spring.boot.rest.api.dto.EventDTO;
import spring.boot.rest.api.dto.EventUpdateDTO;
import spring.boot.rest.api.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/events")
@PreAuthorize("hasAuthority('read_write_delete:events')")
public class EventRestControllerV1 {

    private final EventService eventService;

    public EventRestControllerV1(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventDTO> create(@RequestBody EventCreateDTO eventCreateDTO) {
        log.info("IN [POST] create() -> creating event: {}", eventCreateDTO);
        EventDTO createdEventDTO = eventService.save(eventCreateDTO);
        log.info("IN [POST] create() -> created event: {} -> SUCCESSFULLY", createdEventDTO);
        return new ResponseEntity<>(createdEventDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> update(@PathVariable("id") Long id,
                                           @RequestBody EventUpdateDTO eventUpdateDTO) {
        log.info("IN update() - updating event: {}", eventUpdateDTO);
        eventUpdateDTO.setId(id);
        EventDTO updatedEventDTO = eventService.update(eventUpdateDTO);
        log.info("IN update() - event updated: {} -> SUCCESSFULLY", updatedEventDTO);
        return new ResponseEntity<>(updatedEventDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> findById(@PathVariable("id") Long id) {
        log.info("IN getById() - getting event by id '{}'", id);
        EventDTO eventDTO = eventService.findById(id);
        log.info("IN getById() - event with id '{}' found: {} -> SUCCESSFULLY", id, eventDTO);
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> findAll() {
        log.info("IN getAll() - getting all events");
        List<EventDTO> events = eventService.findAll();
        log.info("IN getAll() - found {} events: {} -> SUCCESSFULLY", events.size(), events);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        log.info("IN deleteById() - deleting event with id '{}'", id);
        eventService.deleteById(id);
        log.info("IN deleteById() - event with id '{}' deleted SUCCESSFULLY", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAll() {
        log.info("IN deleteAll() - deleting all events");
        eventService.deleteAll();
        log.info("IN deleteAll() - all events deleted SUCCESSFULLY");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
