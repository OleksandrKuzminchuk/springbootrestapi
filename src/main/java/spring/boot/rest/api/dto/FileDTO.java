package spring.boot.rest.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.boot.rest.api.model.Status;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    private Long id;
    private String name;
    private String s3Secret;
    private String s3Bucket;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Status status;
}
