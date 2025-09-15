package com.portalauditoria.portalweb.config;

import com.portalauditoria.portalweb.model.Usuario;
import com.portalauditoria.portalweb.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, UsuarioRepository repo) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/img/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .loginPage("/login")
                .successHandler(onAuthSuccess(repo))
            )
            // CSRF fica habilitado por padrão; logout via POST no template
            .logout(logout -> logout.logoutSuccessUrl("/"));

        return http.build();
    }

    private AuthenticationSuccessHandler onAuthSuccess(UsuarioRepository repo) {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException {
                Object p = authentication.getPrincipal();
                if (p instanceof OidcUser user) {
                    String sub     = user.getSubject();
                    String email   = user.getEmail();
                    String name    = user.getFullName() != null ? user.getFullName() : email;
                    String picture = user.getPicture();
                    String locale  = user.getLocale();

                    Usuario u = repo.findByProviderAndProviderId("google", sub).orElseGet(() -> {
                        Usuario nu = new Usuario();
                        nu.setProvider("google");
                        nu.setProviderId(sub);
                        nu.setEmail(email);
                        nu.setEnabled(true);
                        nu.setRoles("USER");
                        nu.setCreatedAt(LocalDateTime.now());
                        return nu;
                    });

                    boolean changed = false;

                    if (!name.equals(u.getName())) { u.setName(name); changed = true; }
                    if ((picture != null && !picture.equals(u.getPictureUrl()))
                        || (picture == null && u.getPictureUrl() != null)) {
                        u.setPictureUrl(picture); changed = true;
                    }
                    if ((locale != null && !locale.equals(u.getLocale()))
                        || (locale == null && u.getLocale() != null)) {
                        u.setLocale(locale); changed = true;
                    }

                    u.setLastLoginAt(LocalDateTime.now()); // sempre atualiza último login
                    if (u.getId() == null || changed) {
                        u.setUpdatedAt(LocalDateTime.now());
                        repo.save(u);
                    }
                }
                response.sendRedirect("/");
            }
        };
    }
}
