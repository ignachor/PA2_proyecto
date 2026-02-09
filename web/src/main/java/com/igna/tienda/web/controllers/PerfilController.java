package com.igna.tienda.web.controllers;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.domain.value.Direccion;
import com.igna.tienda.infra.persistence.jpa.JpaUsuarioRepository;
import com.igna.tienda.infra.services.UsuarioServiceTx;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Controller para gestión de perfil del cliente.
 * 
 * Reutiliza UsuarioServiceTx de la capa infra.
 */
@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioServiceTx usuarioService;
    private final EntityManagerFactory emf;

    public PerfilController(EntityManagerFactory emf) {
        this.emf = emf;
        this.usuarioService = new UsuarioServiceTx(emf);
    }

    /**
     * GET /perfil - Ver perfil del usuario
     */
    @GetMapping
    public String verPerfil(Authentication auth, Model model) {
        try {
            Usuario usuario = obtenerUsuario(auth.getName());
            model.addAttribute("usuario", usuario);
            return "perfil";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar perfil: " + e.getMessage());
            return "perfil";
        }
    }

    /**
     * POST /perfil/datos - Editar datos personales
     */
    @PostMapping("/datos")
    public String editarDatosPersonales(@RequestParam String nombre,
                                       @RequestParam String apellido,
                                       Authentication auth,
                                       RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(auth.getName());
            
            // Crear usuario con datos actualizados
            Usuario usuarioModificado = new Usuario(
                usuario.getId(),
                nombre,
                apellido,
                usuario.getDni(),
                usuario.getEmail(),
                usuario.getDireccion(),
                usuario.getPassword(),
                usuario.getRol()
            );

            usuarioService.editarDatosPersonales(usuarioModificado);

            redirectAttributes.addFlashAttribute("mensaje", "Datos personales actualizados");
            return "redirect:/perfil";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar datos: " + e.getMessage());
            return "redirect:/perfil";
        }
    }

    /**
     * POST /perfil/email - Editar email
     */
    @PostMapping("/email")
    public String editarEmail(@RequestParam String nuevoEmail,
                             @RequestParam String password,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(auth.getName());

            // Crear usuario con email actualizado y password para validación
            Usuario usuarioModificado = new Usuario(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getDni(),
                nuevoEmail,
                usuario.getDireccion(),
                password, // Password para validar
                usuario.getRol()
            );

            Usuario resultado = usuarioService.editarEmail(usuarioModificado);

            if (resultado != null) {
                redirectAttributes.addFlashAttribute("mensaje", "Email actualizado. Por favor, inicia sesión nuevamente.");
                return "redirect:/logout";
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "No se realizaron cambios en el email");
                return "redirect:/perfil";
            }

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar email: " + e.getMessage());
            return "redirect:/perfil";
        }
    }

    /**
     * POST /perfil/direccion - Editar dirección
     */
    @PostMapping("/direccion")
    public String editarDireccion(@RequestParam String calle,
                                  @RequestParam String numero,
                                  @RequestParam String ciudad,
                                  @RequestParam String provincia,
                                  @RequestParam String codigoPostal,
                                  Authentication auth,
                                  RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(auth.getName());

            // Crear nueva dirección
            Direccion nuevaDireccion = new Direccion(calle, numero, ciudad, provincia, codigoPostal);

            // Crear usuario con dirección actualizada
            Usuario usuarioModificado = new Usuario(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getDni(),
                usuario.getEmail(),
                nuevaDireccion,
                usuario.getPassword(),
                usuario.getRol()
            );

            usuarioService.editarDireccion(usuarioModificado);

            redirectAttributes.addFlashAttribute("mensaje", "Dirección actualizada");
            return "redirect:/perfil";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar dirección: " + e.getMessage());
            return "redirect:/perfil";
        }
    }

    /**
     * Método auxiliar para obtener el usuario logueado
     */
    private Usuario obtenerUsuario(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            JpaUsuarioRepository usuarioRepo = new JpaUsuarioRepository(em);
            Usuario usuario = usuarioRepo.buscarPorEmail(email);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }
            return usuario;
        } finally {
            em.close();
        }
    }
}
