package spring.boot.rest.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.boot.rest.api.dto.request.AuthenticationRequestDto;
import spring.boot.rest.api.dto.request.RegisterRequestDto;
import spring.boot.rest.api.dto.response.AuthenticationResponseDto;
import spring.boot.rest.api.exception.InvalidJwtAuthenticationException;
import spring.boot.rest.api.exception.NotFoundException;
import spring.boot.rest.api.jwt.JwtService;
import spring.boot.rest.api.jwt.JwtUserFactory;
import spring.boot.rest.api.model.Role;
import spring.boot.rest.api.model.Token;
import spring.boot.rest.api.model.User;
import spring.boot.rest.api.repository.UserRepo;
import spring.boot.rest.api.service.AuthenticationService;
import spring.boot.rest.api.service.TokenService;
import spring.boot.rest.api.service.UserService;

import java.io.IOException;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static spring.boot.rest.api.util.Constants.BEARER;
import static spring.boot.rest.api.util.Constants.SEVEN;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final TokenService tokenService;
    private final UserService userService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponseDto register(RegisterRequestDto request) {
        log.info("IN register() -> registering new user...");
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .build();
        final var savedUser = userService.save(user);
        final var userDetails = JwtUserFactory.create(savedUser);
        final var jwtToken = jwtService.createToken(userDetails);
        final var refreshToken = jwtService.createRefreshToken(userDetails);
        saveUserToken(savedUser, jwtToken);
        log.info("IN register() -> registering new user... -> registered SUCCESSFULLY");

        return AuthenticationResponseDto.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
    }

    @Override
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        log.info("IN authenticate() -> authenticating user...");
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user " + request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }
        final var user = getUserByEmail(request.getEmail());
        final var userDetails = JwtUserFactory.create(user);
        final var jwtToken = jwtService.createToken(userDetails);
        final var refreshToken = jwtService.createRefreshToken(userDetails);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        log.info("IN authenticate() -> authenticating user... -> authenticated SUCCESSFULLY");

        return AuthenticationResponseDto.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
    }

    @Override
    public void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        log.info("IN refreshToken() -> refreshing token...");
        final var authHeader = req.getHeader(AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith(BEARER)){
            throw new InvalidJwtAuthenticationException("Invalid or missing authorization header");
        }
        refreshToken = authHeader.substring(SEVEN);
        userEmail = jwtService.getUsername(refreshToken);
        if (userEmail != null){
            try {
                var user = getUserByEmail(userEmail);
                var userDetails = JwtUserFactory.create(user);
                if (jwtService.isTokenValid(refreshToken, userDetails)){
                    var accessToken = jwtService.createToken(userDetails);
                    revokeAllUserTokens(user);
                    saveUserToken(user, accessToken);
                    var authResponse = AuthenticationResponseDto.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
                    res.setContentType(String.valueOf(APPLICATION_JSON));
                    new ObjectMapper().writeValue(res.getOutputStream(), authResponse);
                    log.info("IN refreshToken() -> refreshing token... -> refreshed token SUCCESSFULLY");
                }
            } catch (InvalidJwtAuthenticationException e) {
                log.error("JWT token is expired or invalid", e);
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is expired or invalid");
            }
        }
    }


    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .nameToken(jwtToken)
                .revoked(false)
                .expired(false)
                .build();
        tokenService.save(token);
    }

    private void revokeAllUserTokens(User user){
        var validUserTokens = tokenService.findAllValidTokensByUserId(user.getId());
        if (validUserTokens.isEmpty()){
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenService.saveAll(validUserTokens);
    }

    private User getUserByEmail(String userEmail) {
        return userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException(format("Can't find user by email - [%s]", userEmail)));
    }
}
