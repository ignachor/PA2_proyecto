package com.igna.tienda.web.controllers.admin;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.services.AdminServiceTx;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller para gestión de usuarios (vista de ADMIN).
 * 
 * Reutiliza AdminServiceTx de la capa infra.
 */
@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final AdminServiceTx adminService;

    public AdminUsuarioController(EntityManagerFactory emf) {
        this.adminService = new AdminServiceTx(emf);
    }

    /**
     * GET /admin/usuarios - Listar todos los usuarios
     */
    @GetMapping
    public String listarUsuarios(@RequestParam(value = "busqueda", required = false) String busqueda,
                                Model model) {
        try {
            List<Usuario> usuarios = adminService.listarUsuarios();

            // Filtrar por email si hay búsqueda
            if (busqueda != null && !busqueda.isBlank()) {
                usuarios = usuarios.stream()
                    .filter(u -> u.getEmail().toLowerCase().contains(busqueda.toLowerCase()) ||
                               u.getNombre().toLowerCase().contains(busqueda.toLowerCase()) ||
                               u.getApellido().toLowerCase().contains(busqueda.toLowerCase()))
                    .toList();
            }

            model.addAttribute("usuarios", usuarios);
            model.addAttribute("busqueda", busqueda);

            return "admin/usuarios";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar usuarios: " + e.getMessage());
            model.addAttribute("usuarios", List.of());
            return "admin/usuarios";
        }
    }

    /**
     * POST /admin/usuarios/activar - Activar usuario por email
     */
    @PostMapping("/activar")
    public String activarUsuario(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            adminService.activarUsuarioPorEmail(email);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario activado exitosamente");
            return "redirect:/admin/usuarios";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al activar usuario: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    /**
     * POST /admin/usuarios/desactivar - Desactivar usuario por email
     */
    @PostMapping("/desactivar")
    public String desactivarUsuario(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            adminService.desactivarUsuarioPorEmail(email);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario desactivado exitosamente");
            return "redirect:/admin/usuarios";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar usuario: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    /**
     * GET /admin/usuarios/detalle - Ver detalle de un usuario
     */
    @GetMapping("/detalle")
    public String verDetalleUsuario(@RequestParam String email, Model model) {
        try {
            Usuario usuario = adminService.listarUsuarios().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);

            if (usuario == null) {
                model.addAttribute("error", "Usuario no encontrado");
                return "redirect:/admin/usuarios";
            }

            model.addAttribute("usuario", usuario);
            return "admin/usuario-detalle";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar usuario: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }
}
