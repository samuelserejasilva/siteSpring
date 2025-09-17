package com.portalauditoria.portalweb.config;

import com.portalauditoria.portalweb.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, UsuarioService usuarioService) throws Exception {
    http
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
        .anyRequest().authenticated()
      )
      .oauth2Login(oauth -> oauth
        .userInfoEndpoint(user -> user.oidcUserService(oidcUserService(usuarioService)))
        .defaultSuccessUrl("/", true)
      )
      .logout(l -> l.logoutSuccessUrl("/").permitAll());

    return http.build();
  }

  // >>> tipo correto é a interface funcional OAuth2UserService <<<
  private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService(UsuarioService usuarioService) {
    OidcUserService delegate = new OidcUserService();
    return (OidcUserRequest userRequest) -> {
      OidcUser oidcUser = delegate.loadUser(userRequest);
      usuarioService.upsertFromGoogle(oidcUser); // salva/atualiza no banco
      return oidcUser;
    };
  }
}
