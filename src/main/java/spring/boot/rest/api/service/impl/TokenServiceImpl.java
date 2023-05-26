package spring.boot.rest.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.boot.rest.api.exception.NotFoundException;
import spring.boot.rest.api.model.Status;
import spring.boot.rest.api.model.Token;
import spring.boot.rest.api.model.TokenType;
import spring.boot.rest.api.repository.TokenRepo;
import spring.boot.rest.api.service.TokenService;

import java.time.LocalDateTime;
import java.util.List;

import static spring.boot.rest.api.util.Constants.FAILED_TO_FIND_TOKEN;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepo tokenRepo;
    @Override
    public List<Token> findAllValidTokensByUserId(Long userId) {
        log.info("IN findAllValidTokensByUserId() -> finding all valid tokens by user id - {}...", userId);
        final var result = tokenRepo.findAllValidTokensByUserId(userId);
        log.info("IN findAllValidTokensByUserId() -> finding all valid tokens by user id - {}... -> SUCCESSFULLY", userId);
        return result;
    }

    @Override
    public Token findByToken(String token) {
        log.info("IN findByToken() -> finding token...");
        final var result = tokenRepo.findByNameToken(token)
                .orElseThrow(() -> new NotFoundException(FAILED_TO_FIND_TOKEN));
        log.info("IN findByToken() -> finding token... -> SUCCESSFULLY");
        return result;
    }

    @Override
    public Token save(Token token) {
        isExistsToken(token);
        log.info("IN save() -> saving token...");
        token.setCreatedAt(token.getCreatedAt() == null ? LocalDateTime.now() : token.getCreatedAt());
        token.setUpdatedAt(LocalDateTime.now());
        token.setStatus(token.isExpired() || token.isRevoked() ? Status.DELETED : Status.ACTIVE);
        token.setTokenType(TokenType.BEARER);
        final var savedToken = tokenRepo.save(token);
        log.info("IN save() -> saving token... -> saved SUCCESSFULLY");
        return savedToken;
    }

    @Override
    public void saveAll(List<Token> tokens) {
        log.info("IN saveAll() -> saving all tokens...");
        tokens.forEach(this::save);
        log.info("IN saveAll() -> saving all tokens... -> seved all tokens SUCCESSFULLY");
    }

    private void isExistsToken(Token token) {
        if (token.getId() != null){
            this.findByToken(token.getNameToken());
        }
    }
}
