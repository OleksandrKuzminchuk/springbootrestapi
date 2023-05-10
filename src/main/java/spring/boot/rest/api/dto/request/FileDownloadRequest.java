package spring.boot.rest.api.dto.request;

import lombok.Data;

@Data
public class FileDownloadRequest {
    private String s3Secret;
}
