package spring.boot.rest.api.dto.request;

import lombok.Data;

@Data
public class FileRenameRequest {
    private String existingS3Secret;
    private String newFileName;
}
