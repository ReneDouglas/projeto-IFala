package br.edu.ifpi.ifala.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço para gerenciar uma blacklist de tokens JWT. Permite adicionar tokens à blacklist e
 * verificar se um token está na blacklist.
 * 
 * @author Phaola
 */

@Service
public class TokenBlacklistService {
  private final ConcurrentHashMap<String, Instant> blacklist = new ConcurrentHashMap<>();

  public void blacklistToken(String token, Instant expiresAt) {
    if (token == null || token.isBlank())
      return;
    blacklist.put(token, expiresAt);
  }

  public boolean isBlacklisted(String token) {
    if (token == null)
      return false;
    Instant exp = blacklist.get(token);
    if (exp == null)
      return false;
    if (exp.isBefore(Instant.now())) {
      blacklist.remove(token);
      return false;
    }
    return true;
  }
}

