package spring.boot.rest.api.service;

import spring.boot.rest.api.model.File;
import spring.boot.rest.api.model.User;

import java.util.List;

public interface UserService extends GenericService<User, Long> {
    List<File> findFiles(Long id);
    User checkIfUserExists(Long id);
    boolean currentUserIsModeratorOrAdmin();
}
