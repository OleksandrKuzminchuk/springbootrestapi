package spring.boot.rest.api.service;

import org.springframework.web.multipart.MultipartFile;
import spring.boot.rest.api.model.File;

public interface FileService extends GenericService<File, Long> {
    File upload(MultipartFile newFile);
    byte[] download(String location);
    File updateName(Long id, String newFileName);
    File updateFileContent(Long id, MultipartFile newFile);
    File checkIfFileExists(Long id);
}
