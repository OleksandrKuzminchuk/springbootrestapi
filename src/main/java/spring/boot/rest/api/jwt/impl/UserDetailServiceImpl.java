package spring.boot.rest.api.jwt.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import spring.boot.rest.api.jwt.JwtUserFactory;
import spring.boot.rest.api.jwt.UserDetailService;
import spring.boot.rest.api.repository.UserRepo;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailService {

    public final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("IN loadUserByUsername() -> checking... user by username - {}", username);
        final var user = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(format("User with username - [%s] not found", username)));
        final var jwtUser = JwtUserFactory.create(user);
        log.info("IN loadUserByUsername() -> checking... user by username - {} -> checked SUCCESSFULLY", username);
        return jwtUser;
    }
}
