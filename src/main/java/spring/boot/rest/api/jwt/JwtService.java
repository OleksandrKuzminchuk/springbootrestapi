package spring.boot.rest.api.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.function.Function;

public interface JwtService {
    String createToken(UserDetails userDetails);
    String createRefreshToken(UserDetails userDetails);
    String getUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    <T> T getClaim(String token, Function<Claims, T> claimsResolver);
    String resolveToken(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws IOException, ServletException;
}
