package spring.boot.rest.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static spring.boot.rest.api.util.Constants.*;

@Getter
@AllArgsConstructor
public enum Permission {
    READ_SELF(PERMISSION_READ_SELF),
    DOWNLOAD_FILE(PERMISSION_DOWNLOAD_FILE),
    READ_WRITE_DELETE_EVENTS(PERMISSION_READ_WRITE_DELETE_EVENTS),
    READ_WRITE_DELETE_USERS(PERMISSION_READ_WRITE_DELETE_USERS),
    READ_WRITE_DELETE_FILES(PERMISSION_READ_WRITE_DELETE_FILES),
    MANAGE_USERS(PERMISSION_MANAGE_USERS),
    MANAGE_ROLES(PERMISSION_MANAGE_ROLES);

    private final String name;
}
