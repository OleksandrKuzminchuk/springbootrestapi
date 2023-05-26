package spring.boot.rest.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.boot.rest.api.model.Token;

import java.util.List;
import java.util.Optional;

import static spring.boot.rest.api.util.Constants.QUERY_FIND_ALL_TOKENS_BY_USER_ID_AND_EXPIRED_REVOKED_FALSE;

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {

    @Query(QUERY_FIND_ALL_TOKENS_BY_USER_ID_AND_EXPIRED_REVOKED_FALSE)
    List<Token> findAllValidTokensByUserId(Long userId);

    Optional<Token> findByNameToken(String token);
}
