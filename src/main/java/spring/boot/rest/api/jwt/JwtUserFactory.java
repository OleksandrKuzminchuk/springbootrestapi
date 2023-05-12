package spring.boot.rest.api.jwt;

import spring.boot.rest.api.model.Status;
import spring.boot.rest.api.model.User;

public class JwtUserFactory {
    public JwtUserFactory() {
    }

    public static JwtUser create(User user){
        return JwtUser.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().getAuthorities())
                .enabled(user.getStatus().equals(Status.ACTIVE))
                .build();
    }
}
