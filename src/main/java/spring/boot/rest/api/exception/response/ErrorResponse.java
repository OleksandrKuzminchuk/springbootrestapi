package spring.boot.rest.api.exception.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime dateTime;
    private int status;
    private String error;
    private String message;
}
