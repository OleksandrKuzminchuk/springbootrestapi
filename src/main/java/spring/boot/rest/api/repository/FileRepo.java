package spring.boot.rest.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.boot.rest.api.model.File;

@Repository
public interface FileRepo extends JpaRepository<File, Long> {
}
