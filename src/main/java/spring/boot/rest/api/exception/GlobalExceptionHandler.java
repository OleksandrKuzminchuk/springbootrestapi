package spring.boot.rest.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import spring.boot.rest.api.exception.response.ErrorResponse;

import java.time.LocalDateTime;

import static spring.boot.rest.api.util.constant.Constants.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<?> handleDatabaseOperationException(DatabaseOperationException e){
        ErrorResponse response = getResponse(HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_OPERATION_ERROR, e.getMessage());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e){
        ErrorResponse response = getResponse(HttpStatus.NOT_FOUND, NOT_FOUND_EXCEPTION, e.getMessage());
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e){
        ErrorResponse response = getResponse(HttpStatus.NOT_FOUND, USERNAME_NOT_FOUND_EXCEPTION, e.getMessage());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e){
        ErrorResponse response = getResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED_EXCEPTION, e.getMessage());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<?> handleFileException(FileException e){
        ErrorResponse response = getResponse(HttpStatus.BAD_REQUEST, FILE_EXCEPTION, e.getMessage());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception e) {
        ErrorResponse response = getResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_EXCEPTION, e.getMessage());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(Error.class)
    public ResponseEntity<?> handleError(Error e) {
        ErrorResponse response = getResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_EXCEPTION, e.getMessage());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        ErrorResponse response = getResponse(HttpStatus.INTERNAL_SERVER_ERROR, RUNTIME_EXCEPTION, e.getMessage());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    public ResponseEntity<?> handleInvalidJwtAuthenticationException(InvalidJwtAuthenticationException e) {
        ErrorResponse response = getResponse(HttpStatus.UNAUTHORIZED, INVALID_JWT_AUTH_EXCEPTION, e.getMessage());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    private ErrorResponse getResponse(HttpStatus status, String error, String message) {
        return new ErrorResponse(LocalDateTime.now(), status.value(), error, message);
    }
}
