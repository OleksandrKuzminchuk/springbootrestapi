package spring.boot.rest.api.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface JwtTokenProvider {
    String createToken(String username);
    Authentication getAuthentication(String token);
    String getUsername(String token);
    String resolveToken(HttpServletRequest req);
    boolean validateToken(String token);

}
