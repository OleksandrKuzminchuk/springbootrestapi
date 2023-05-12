package spring.boot.rest.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Permission {
    READ_SELF("read:self"),
    DOWNLOAD_FILE("download:file"),
    READ_WRITE_DELETE_EVENTS("read_write_delete:events"),
    READ_WRITE_DELETE_USERS("read_write_delete:users"),
    READ_WRITE_DELETE_FILES("read_write_delete:files"),
    MANAGE_USERS("manage:users"),
    MANAGE_ROLES("manage:roles");

    private final String permission;
}
