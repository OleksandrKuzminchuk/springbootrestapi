package spring.boot.rest.api.jwt.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import spring.boot.rest.api.jwt.JwtUser;
import spring.boot.rest.api.jwt.JwtUserFactory;
import spring.boot.rest.api.model.User;
import spring.boot.rest.api.repository.UserRepo;

import static java.lang.String.format;

@Slf4j
@Service("jwtUserDetailServiceImpl")
public class JwtUserDetailServiceImpl implements UserDetailsService {

    public final UserRepo userRepo;

    @Autowired
    public JwtUserDetailServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("IN loadUserByUsername() -> checking... user by username - {}", username);
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(format("User with username - [%s] not found", username)));
        JwtUser jwtUser = JwtUserFactory.create(user);
        log.info("IN loadUserByUsername() -> checking... user by username - {} -> checked SUCCESSFULLY", username);
        return jwtUser;
    }
}
