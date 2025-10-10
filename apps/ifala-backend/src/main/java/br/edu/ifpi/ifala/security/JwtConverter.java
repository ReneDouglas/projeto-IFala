package br.edu.ifpi.ifala.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Converter implementation to transform a Jwt into a JwtAuthenticationToken.
 */
@Component
public class JwtConverter implements Converter<Jwt, JwtAuthenticationToken> {

  @Override
  public JwtAuthenticationToken convert(Jwt jwt) {
    Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
    return new JwtAuthenticationToken(jwt, authorities);
  }

  // ...existing code...
  private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    Object resourceAccessObj = jwt.getClaim("resource_access");
    if (!(resourceAccessObj instanceof Map)) {
      return Collections.emptyList();
    }

    Map<?, ?> resourceAccess = (Map<?, ?>) resourceAccessObj;
    Object appRolesObj = resourceAccess.get("app_ifala");
    if (!(appRolesObj instanceof Map)) {
      return Collections.emptyList();
    }

    Map<?, ?> appRoles = (Map<?, ?>) appRolesObj;
    Object rolesObj = appRoles.get("roles");
    if (!(rolesObj instanceof List)) {
      return Collections.emptyList();
    }

    List<?> rolesRaw = (List<?>) rolesObj;
    List<String> roles = rolesRaw.stream().filter(String.class::isInstance).map(String.class::cast)
        .collect(Collectors.toList());

    return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }
}
