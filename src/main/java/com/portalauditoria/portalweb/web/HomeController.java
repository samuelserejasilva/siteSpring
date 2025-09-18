package com.portalauditoria.portalweb.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.util.StreamUtils;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final ResourceLoader resourceLoader;

    // mude para "classpath:/static/css/stilo.css" se esse for o nome
    private static final String CSS_PATH = "classpath:/static/css/style.css";

    @Value("${app.inline-css.minify:false}")
    private boolean minifyCss;

    private String cssContent = "";

    public HomeController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        cssContent = loadCss(CSS_PATH);
        if (minifyCss) cssContent = minify(cssContent);
        log.info("AMP CSS carregado: {} bytes (minify={})", cssContent.length(), minifyCss);
    }

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal OidcUser user) {
        if (user != null) {
            model.addAttribute("name", user.getFullName());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("picture", user.getPicture());
        }
        model.addAttribute("cssContent", cssContent);
        return "index";
    }

    // Redireciona para o provedor Google configurado no Spring Security
    @GetMapping("/login")
    public String login() {
        return "redirect:/oauth2/authorization/google";
    }

    // ---------------- helpers ----------------
    private String loadCss(String location) {
        try (InputStream is = resourceLoader.getResource(location).getInputStream()) {
            return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Não consegui ler o CSS em {}", location, e);
            return "";
        }
    }

    private String minify(String css) {
        // remove comentários /* ... */
        css = css.replaceAll("/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/", "");
        // normaliza espaços
        css = css.replaceAll("\\s+", " ");
        // remove espaços em volta de símbolos
        css = css.replaceAll("\\s*([{}:;,>~+])\\s*", "$1");
        // remove ; antes de }
        css = css.replaceAll(";}", "}");
        return css.trim();
    }
}

