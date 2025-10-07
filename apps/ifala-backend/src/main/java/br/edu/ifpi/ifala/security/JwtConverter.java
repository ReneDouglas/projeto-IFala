package br.edu.ifpi.ifala.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class JwtConverter implements Converter<Jwt, JwtAuthenticationToken> {

  @Override
  public JwtAuthenticationToken convert(Jwt jwt) {
    Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
    return new JwtAuthenticationToken(jwt, authorities);
  }

  private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
    if (resourceAccess == null)
      return Collections.emptyList();

    Map<String, Object> appRoles = (Map<String, Object>) resourceAccess.get("app_ifala");
    if (appRoles == null)
      return Collections.emptyList();

    List<String> roles = (List<String>) appRoles.get("roles");
    if (roles == null)
      return Collections.emptyList();

    return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }
}
