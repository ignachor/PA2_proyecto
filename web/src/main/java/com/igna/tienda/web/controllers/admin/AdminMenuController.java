package com.igna.tienda.web.controllers.admin;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminMenuController {

    @GetMapping({"", "/", "/menu"})
    public String menuAdmin(Authentication auth, Model model) {
        model.addAttribute("usuarioAdmin", auth != null ? auth.getName() : "admin");
        return "admin/menu";
    }
}
