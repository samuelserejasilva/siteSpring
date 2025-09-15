package com.portalauditoria.portalweb.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal OidcUser user) {
        if (user != null) {
            model.addAttribute("name", user.getFullName());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("picture", user.getPicture());
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
