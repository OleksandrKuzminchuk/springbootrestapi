package spring.boot.rest.api.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.boot.rest.api.exception.InvalidJwtAuthenticationException;
import spring.boot.rest.api.repository.TokenRepo;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepo tokenRepo;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("IN doFilterInternal() -> filtering...");
        try {
            final var token = jwtService.resolveToken(request, response, filterChain);
            if (token == null) return;
            final var userEmail = jwtService.getUsername(token);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
                final var userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                final var isTokenValid = tokenRepo.findByNameToken(token)
                        .map(t -> !t.isExpired() && !t.isRevoked()).orElse(Boolean.FALSE);
                if (jwtService.isTokenValid(token, userDetails) && Boolean.TRUE.equals(isTokenValid)){
                    final var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (InvalidJwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }
}
