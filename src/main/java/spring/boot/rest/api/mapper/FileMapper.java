package spring.boot.rest.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import spring.boot.rest.api.dto.FileCreateDTO;
import spring.boot.rest.api.dto.FileDTO;
import spring.boot.rest.api.dto.FileUpdateDTO;
import spring.boot.rest.api.model.File;

import static spring.boot.rest.api.util.constant.Constants.TEXT_SPRING;
import static spring.boot.rest.api.util.constant.Constants.TEXT_STATUS;

@Component
@Mapper(componentModel = TEXT_SPRING)
public interface FileMapper {

    @Mapping(source = TEXT_STATUS, target = TEXT_STATUS)
    FileDTO map(File file);

    File map(FileDTO fileDto);
    File map(FileCreateDTO fileCreateDTO);
    File map(FileUpdateDTO fileUpdateDTO);
}
