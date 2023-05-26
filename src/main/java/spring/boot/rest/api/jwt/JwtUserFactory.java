package spring.boot.rest.api.jwt;

import lombok.extern.slf4j.Slf4j;
import spring.boot.rest.api.model.Status;
import spring.boot.rest.api.model.User;

@Slf4j
public class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(User user){
        log.info("IN JwtUserFactory.class -> method create() -> creating JwtUser from User...");
        return JwtUser.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().getAuthorities())
                .enabled(user.getStatus().equals(Status.ACTIVE))
                .build();
    }
}
