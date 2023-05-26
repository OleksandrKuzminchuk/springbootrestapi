package spring.boot.rest.api.jwt.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import spring.boot.rest.api.exception.InvalidJwtAuthenticationException;
import spring.boot.rest.api.jwt.JwtService;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static spring.boot.rest.api.util.Constants.*;

@Slf4j
@Service("jwtServiceImpl")
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.token.secret}")
    private String secretKey;
    @Value("${jwt.token.expired}")
    private long jwtExpired;
    @Value("${jwt.token.refresh_token.expired}")
    private long refreshExpiration;

    @Override
    public String createToken(UserDetails userDetails) {
        log.info("IN createToken() -> creating token...");
        return createToken(new HashMap<>(), userDetails);
    }

    @Override
    public String createRefreshToken(UserDetails userDetails) {
        log.info("IN createRefreshToken() -> creating refresh token...");
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    @Override
    public String getUsername(String token) {
        log.info("IN getUsername() -> getting username from token...");
        return getClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("IN isTokenValid() -> validating token...");
        try {
            final var username = getUsername(token);

            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);

        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtAuthenticationException(JWT_TOKEN_IS_EXPIRED_OR_INVALID);
        }
    }

    @Override
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        log.info("IN getClaim() -> getting a claim...");
        final var claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String resolveToken(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        log.info("IN resolveToken() -> resolving token...");
            final var authHeader = req.getHeader(AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER)) {
                filterChain.doFilter(req, res);
                return null;
            }
            return authHeader.substring(SEVEN);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSighKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSighKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String createToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpired);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long jwtExpired) {
        final var now = new Date();
        final var validity = new Date(now.getTime() + jwtExpired);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSighKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }
}
