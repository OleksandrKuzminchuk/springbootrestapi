package spring.boot.rest.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Role {
    USER(Set.of(Permission.READ_SELF, Permission.DOWNLOAD_FILE)),
    MODERATOR(Set.of(Permission.READ_SELF, Permission.DOWNLOAD_FILE, Permission.READ_WRITE_DELETE_EVENTS, Permission.READ_WRITE_DELETE_FILES, Permission.READ_WRITE_DELETE_USERS)),
    ADMIN(Set.of(Permission.values()));

    private final Set<Permission> permissions;

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());
    }
}
