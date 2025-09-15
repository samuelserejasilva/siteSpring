package com.portalauditoria.portalweb.repository;

import com.portalauditoria.portalweb.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByProviderAndProviderId(String provider, String providerId);
}
