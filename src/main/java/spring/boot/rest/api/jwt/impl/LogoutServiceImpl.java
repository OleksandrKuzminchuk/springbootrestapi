package spring.boot.rest.api.jwt.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import spring.boot.rest.api.jwt.LogoutService;
import spring.boot.rest.api.service.TokenService;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static spring.boot.rest.api.util.Constants.BEARER;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {
    private final TokenService tokenService;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String jwt;
        if (authHeader == null || !authHeader.startsWith(BEARER)){
            return;
        }
        jwt = authHeader.substring(7);
        var storedToken = tokenService.findByToken(jwt);
        if (storedToken != null){
            storedToken.setExpired(Boolean.TRUE);
            storedToken.setRevoked(Boolean.TRUE);
            tokenService.save(storedToken);
        }
    }
}
