package spring.boot.rest.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import spring.boot.rest.api.dto.EventCreateDTO;
import spring.boot.rest.api.dto.EventDTO;
import spring.boot.rest.api.dto.EventUpdateDTO;
import spring.boot.rest.api.model.Event;
import spring.boot.rest.api.model.File;
import spring.boot.rest.api.model.User;

import static spring.boot.rest.api.util.constant.Constants.*;

@Component
@Mapper(componentModel = TEXT_SPRING, uses = {UserMapper.class, FileMapper.class})
public interface EventMapper {

    @Mapping(source = "user", target = "userId", qualifiedByName = "mapUserToId")
    @Mapping(source = "file", target = "fileId", qualifiedByName = "mapFileToId")
    EventDTO map(Event event);

    @Mapping(source = "userId", target = "user", qualifiedByName = "mapIdToUser")
    @Mapping(source = "fileId", target = "file", qualifiedByName = "mapIdToFile")
    Event map(EventDTO eventDto);

    @Mapping(source = "userId", target = "user", qualifiedByName = "mapIdToUser")
    @Mapping(source = "fileId", target = "file", qualifiedByName = "mapIdToFile")
    Event map(EventCreateDTO eventCreateDTO);

    @Mapping(source = "userId", target = "user", qualifiedByName = "mapIdToUser")
    @Mapping(source = "fileId", target = "file", qualifiedByName = "mapIdToFile")
    Event map(EventUpdateDTO eventUpdateDTO);
    @Named("mapUserToId")
    default Long mapUserToId(User user) {
        return user != null ? user.getId() : null;
    }

    @Named("mapFileToId")
    default Long mapFileToId(File file) {
        return file != null ? file.getId() : null;
    }

    @Named("mapIdToUser")
    default User mapIdToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    @Named("mapIdToFile")
    default File mapIdToFile(Long fileId) {
        if (fileId == null) {
            return null;
        }
        File file = new File();
        file.setId(fileId);
        return file;
    }
}
