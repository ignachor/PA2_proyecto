package com.igna.tienda.web.controllers;

import com.igna.tienda.core.domain.enums.Rol;
import com.igna.tienda.core.domain.value.Direccion;
import com.igna.tienda.infra.services.AuthServiceTx;
import com.igna.tienda.web.dto.RegistroForm;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller para autenticación (Login y Registro).
 * 
 * Reutiliza AuthServiceTx de la capa infra.
 */
@Controller
public class AuthController {

    private final AuthServiceTx authService;

    public AuthController(EntityManagerFactory emf) {
        this.authService = new AuthServiceTx(emf);
    }

    /**
     * GET /login - Muestra formulario de login
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Sesión cerrada exitosamente");
        }
        return "login";
    }

    /**
     * GET /registro - Muestra formulario de registro
     */
    @GetMapping("/registro")
    public String registroPage(Model model) {
        model.addAttribute("form", new RegistroForm());
        return "registro";
    }

    /**
     * POST /registro - Procesa el registro de nuevo cliente
     */
    @PostMapping("/registro")
    public String registrar(@ModelAttribute RegistroForm form, Model model) {
        try {
            // Validar que las contraseñas coincidan
            if (!form.getPassword().equals(form.getPasswordConfirm())) {
                model.addAttribute("error", "Las contraseñas no coinciden");
                model.addAttribute("form", form);
                return "registro";
            }

            // Crear objeto Direccion
            Direccion direccion = new Direccion(
                form.getCalle(),
                form.getNumero(),
                form.getCiudad(),
                form.getProvincia(),
                form.getCodigoPostal()
            );

            // Registrar usuario usando el servicio transaccional
            authService.registrar(
                form.getNombre(),
                form.getApellido(),
                form.getDni(),
                form.getEmail(),
                direccion,
                form.getPassword(),
                Rol.CLIENTE
            );

            // Redirigir al login con mensaje de éxito
            return "redirect:/login?registroExitoso=true";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("form", form);
            return "registro";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            model.addAttribute("form", form);
            return "registro";
        }
    }

    /**
     * GET / - Página de inicio
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
