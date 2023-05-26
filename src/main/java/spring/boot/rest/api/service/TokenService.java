package spring.boot.rest.api.service;

import spring.boot.rest.api.model.Token;

import java.util.List;

public interface TokenService {
    List<Token> findAllValidTokensByUserId(Long userId);
    Token findByToken(String token);
    Token save(Token token);
    void saveAll(List<Token> tokens);
}
