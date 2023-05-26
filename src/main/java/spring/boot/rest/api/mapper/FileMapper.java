package spring.boot.rest.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import spring.boot.rest.api.dto.response.FileResponseDto;
import spring.boot.rest.api.dto.response.FileDownloadDTO;
import spring.boot.rest.api.model.File;

import static spring.boot.rest.api.util.Constants.TEXT_SPRING;

@Component
@Mapper(componentModel = TEXT_SPRING)
public interface FileMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "location", target = "location")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "status", target = "status")
    FileResponseDto map(File file);

    File map(FileResponseDto fileResponseDto);
    @Mapping(source = "location", target = "location")
    FileDownloadDTO mapToFileDownloadDTO(File file);
}
