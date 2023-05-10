package spring.boot.rest.api.service;

import spring.boot.rest.api.dto.EventCreateDTO;
import spring.boot.rest.api.dto.EventDTO;
import spring.boot.rest.api.dto.EventUpdateDTO;

public interface EventService extends GenericService<EventDTO, EventCreateDTO, EventUpdateDTO, Long> {
}
