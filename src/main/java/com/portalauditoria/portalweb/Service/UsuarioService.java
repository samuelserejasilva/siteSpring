package com.portalauditoria.portalweb.service;

import com.portalauditoria.portalweb.model.Usuario;
import com.portalauditoria.portalweb.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UsuarioService {
  private final UsuarioRepository repo;

  public UsuarioService(UsuarioRepository repo) {
    this.repo = repo;
  }

  @Transactional
  public Usuario upsertFromGoogle(OidcUser oidc) {
    String sub = oidc.getSubject();
    String name = oidc.getFullName();
    String email = oidc.getEmail();
    String picture = oidc.getPicture();
    String locale = oidc.getLocale();

    return repo.findByProviderAndProviderId("google", sub)
      .map(u -> {
        u.setName(name);
        u.setEmail(email);
        u.setPictureUrl(picture);
        u.setLocale(locale);
        u.setLastLoginAt(LocalDateTime.now());
        return repo.save(u);
      })
      .orElseGet(() -> {
        Usuario u = new Usuario();
        u.setProvider("google");
        u.setProviderId(sub);
        u.setName(name);
        u.setEmail(email);
        u.setPictureUrl(picture);
        u.setLocale(locale);
        u.setEnabled(true);
        u.setRoles("USER");
        u.setLastLoginAt(LocalDateTime.now());
        return repo.save(u);
      });
  }
}
