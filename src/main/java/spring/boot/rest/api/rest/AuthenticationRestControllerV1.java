package spring.boot.rest.api.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.boot.rest.api.dto.request.AuthenticationRequestDto;
import spring.boot.rest.api.dto.request.RegisterRequestDto;
import spring.boot.rest.api.dto.response.AuthenticationResponseDto;
import spring.boot.rest.api.service.AuthenticationService;

import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static spring.boot.rest.api.util.Constants.*;

@Slf4j
@RestController
@RequestMapping(URL_API_V1_AUTH)
@RequiredArgsConstructor
public class AuthenticationRestControllerV1 {

    private final AuthenticationService authService;

    @PostMapping(URL_REGISTER)
    @PreAuthorize("hasAuthority('manage:users') or hasAuthority('manage:roles')")
    public ResponseEntity<AuthenticationResponseDto> register(@RequestBody RegisterRequestDto request) {
        log.info("IN register() -> registering new user...");
        return ResponseEntity.status(CREATED).body(authService.register(request));
    }

    @PostMapping(URL_AUTHENTICATE)
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody AuthenticationRequestDto request) {
        log.info("IN authenticate() -> authenticating user...");
        return ResponseEntity.status(OK).body(authService.authenticate(request));
    }

    @PostMapping(URL_REFRESH_TOKEN)
    public void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        log.info("IN authenticate() -> authenticating user...");
        authService.refreshToken(req, res);
    }
}
