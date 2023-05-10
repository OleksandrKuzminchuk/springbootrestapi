package spring.boot.rest.api.service;

import org.springframework.web.multipart.MultipartFile;
import spring.boot.rest.api.dto.FileDTO;
import spring.boot.rest.api.dto.FileUpdateDTO;

public interface FileService extends GenericService<FileDTO, FileDTO, FileUpdateDTO, Long> {
    FileDTO findByS3Secret(String s3Secret);
    FileDTO upload(MultipartFile newFile);
    byte[] download(String s3Secret);
    FileDTO renameFile(String existingS3Secret, String newFileName);
    FileDTO updateFileContent(String existingS3Secret, MultipartFile newFile);
}
