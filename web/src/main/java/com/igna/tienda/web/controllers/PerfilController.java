package com.igna.tienda.web.controllers;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.domain.value.Direccion;
import com.igna.tienda.infra.persistence.jpa.JpaUsuarioRepository;
import com.igna.tienda.infra.services.UsuarioServiceTx;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Controller para gestion de perfil del cliente.
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
     * GET /perfil - Vista solo lectura del usuario.
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
     * GET /perfil/editar - Vista de edicion del perfil.
     */
    @GetMapping("/editar")
    public String editarPerfil(Authentication auth, Model model) {
        try {
            Usuario usuario = obtenerUsuario(auth.getName());
            model.addAttribute("usuario", usuario);
            return "perfil-editar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar edicion de perfil: " + e.getMessage());
            return "redirect:/perfil";
        }
    }

    /**
     * POST /perfil/datos - Editar datos personales.
     */
    @PostMapping("/datos")
    public String editarDatosPersonales(@RequestParam String nombre,
                                        @RequestParam String apellido,
                                        Authentication auth,
                                        RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(auth.getName());

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
     * POST /perfil/email - Editar email.
     */
    @PostMapping("/email")
    public String editarEmail(@RequestParam String nuevoEmail,
                              @RequestParam String password,
                              Authentication auth,
                              RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(auth.getName());

            Usuario usuarioModificado = new Usuario(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getDni(),
                    nuevoEmail,
                    usuario.getDireccion(),
                    password,
                    usuario.getRol()
            );

            Usuario resultado = usuarioService.editarEmail(usuarioModificado);

            if (resultado != null) {
                actualizarAutenticacionConNuevoEmail(auth, nuevoEmail);
                redirectAttributes.addFlashAttribute("mensaje", "Email actualizado");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "No se realizaron cambios en el email");
            }
            return "redirect:/perfil";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar email: " + e.getMessage());
            return "redirect:/perfil";
        }
    }

    /**
     * POST /perfil/direccion - Editar direccion.
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

            Direccion nuevaDireccion = new Direccion(calle, numero, ciudad, provincia, codigoPostal);

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

            redirectAttributes.addFlashAttribute("mensaje", "Direccion actualizada");
            return "redirect:/perfil";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar direccion: " + e.getMessage());
            return "redirect:/perfil";
        }
    }

    /**
     * Metodo auxiliar para obtener el usuario logueado.
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

    // Mantiene la sesion consistente cuando cambia el email (username).
    private void actualizarAutenticacionConNuevoEmail(Authentication auth, String nuevoEmail) {
        UsernamePasswordAuthenticationToken nuevaAuth = new UsernamePasswordAuthenticationToken(
                nuevoEmail,
                auth.getCredentials(),
                auth.getAuthorities()
        );
        nuevaAuth.setDetails(auth.getDetails());
        SecurityContextHolder.getContext().setAuthentication(nuevaAuth);
    }
}