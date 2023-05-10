package spring.boot.rest.api.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUpdateContentRequest {
    private String existingS3Secret;
    private MultipartFile file;
}
