package spring.boot.rest.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring.boot.rest.api.jwt.JwtAuthenticationFilter;
import spring.boot.rest.api.jwt.JwtService;
import spring.boot.rest.api.jwt.impl.JwtServiceImpl;
import spring.boot.rest.api.jwt.impl.LogoutServiceImpl;
import spring.boot.rest.api.jwt.impl.UserDetailServiceImpl;
import spring.boot.rest.api.repository.TokenRepo;
import spring.boot.rest.api.repository.UserRepo;

import static spring.boot.rest.api.util.Constants.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final LogoutServiceImpl logoutService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(FULL_URL_AUTHENTICATE, FULL_URL_REGISTER).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(new JwtAuthenticationFilter(jwtService(), userDetailsService(), tokenRepo), UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl(URL_API_V1_AUTH_LOGOUT)
                .addLogoutHandler(logoutService)
                .logoutSuccessHandler((request, response, authentication) ->
                        SecurityContextHolder.clearContext());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailServiceImpl(userRepo);
    }

    @Bean
    public JwtService jwtService(){
        return new JwtServiceImpl();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

