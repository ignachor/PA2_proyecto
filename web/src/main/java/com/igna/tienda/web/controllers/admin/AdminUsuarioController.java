package com.igna.tienda.web.controllers.admin;

import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.domain.enums.EstadoPedido;
import com.igna.tienda.core.domain.enums.Rol;
import com.igna.tienda.infra.services.AdminServiceTx;
import com.igna.tienda.infra.services.PedidoServiceTx;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Controller para gestion de usuarios (vista ADMIN).
 */
@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final AdminServiceTx adminService;
    private final PedidoServiceTx pedidoService;

    public AdminUsuarioController(EntityManagerFactory emf) {
        this.adminService = new AdminServiceTx(emf);
        this.pedidoService = new PedidoServiceTx(emf);
    }

    /**
     * Frame principal de usuarios: busqueda, seleccion y acciones.
     */
    @GetMapping
    public String usuariosFrame(@RequestParam(value = "busqueda", required = false) String busqueda,
                                @RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "verPedidos", defaultValue = "false") boolean verPedidos,
                                Model model) {
        try {
            List<Usuario> usuarios = filtrarUsuarios(busqueda);
            Usuario usuarioSeleccionado = buscarUsuarioPorEmail(usuarios, email);

            model.addAttribute("usuarios", usuarios);
            model.addAttribute("busqueda", busqueda == null ? "" : busqueda.trim());
            model.addAttribute("usuarioSeleccionado", usuarioSeleccionado);
            model.addAttribute("roles", Rol.values());
            model.addAttribute("estadosPedido", EstadoPedido.values());
            model.addAttribute("verPedidos", verPedidos);

            if (verPedidos && usuarioSeleccionado != null) {
                List<Pedido> pedidosUsuario = pedidoService.listarPedidosPorCliente(usuarioSeleccionado.getId());
                model.addAttribute("pedidosUsuario", pedidosUsuario);
            }

            return "admin/usuarios";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar usuarios: " + e.getMessage());
            model.addAttribute("usuarios", List.of());
            model.addAttribute("roles", Rol.values());
            model.addAttribute("estadosPedido", EstadoPedido.values());
            model.addAttribute("verPedidos", false);
            return "admin/usuarios";
        }
    }

    /**
     * Frame secundario: lista completa/filtrada para seleccionar usuario.
     */
    @GetMapping("/lista")
    public String listaUsuarios(@RequestParam(value = "busqueda", required = false) String busqueda,
                                Model model) {
        try {
            List<Usuario> usuarios = filtrarUsuarios(busqueda);
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("busqueda", busqueda == null ? "" : busqueda.trim());
            return "admin/usuarios-lista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al listar usuarios: " + e.getMessage());
            model.addAttribute("usuarios", List.of());
            return "admin/usuarios-lista";
        }
    }

    @PostMapping("/activar")
    public String activarUsuario(@RequestParam String email,
                                 @RequestParam(value = "busqueda", required = false) String busqueda,
                                 RedirectAttributes redirectAttributes) {
        try {
            adminService.activarUsuarioPorEmail(email);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario activado exitosamente");
            return redirectUsuarios(email, busqueda, false);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return redirectUsuarios(email, busqueda, false);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al activar usuario: " + e.getMessage());
            return redirectUsuarios(email, busqueda, false);
        }
    }

    @PostMapping("/desactivar")
    public String desactivarUsuario(@RequestParam String email,
                                    @RequestParam(value = "busqueda", required = false) String busqueda,
                                    RedirectAttributes redirectAttributes) {
        try {
            adminService.desactivarUsuarioPorEmail(email);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario desactivado exitosamente");
            return redirectUsuarios(email, busqueda, false);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return redirectUsuarios(email, busqueda, false);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar usuario: " + e.getMessage());
            return redirectUsuarios(email, busqueda, false);
        }
    }

    @PostMapping("/pedidos/estado")
    public String cambiarEstadoPedidoUsuario(@RequestParam Long pedidoId,
                                             @RequestParam EstadoPedido nuevoEstado,
                                             @RequestParam String email,
                                             @RequestParam(value = "busqueda", required = false) String busqueda,
                                             RedirectAttributes redirectAttributes) {
        try {
            adminService.cambiarEstadoPedido(pedidoId, nuevoEstado);
            redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado a " + nuevoEstado);
            return redirectUsuarios(email, busqueda, true);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return redirectUsuarios(email, busqueda, true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar estado: " + e.getMessage());
            return redirectUsuarios(email, busqueda, true);
        }
    }

    private List<Usuario> filtrarUsuarios(String busqueda) {
        List<Usuario> usuarios = adminService.listarUsuarios();
        if (busqueda == null || busqueda.isBlank()) {
            return usuarios;
        }

        String criterio = busqueda.trim().toLowerCase();
        return usuarios.stream()
                .filter(u -> u.getNombre() != null && u.getNombre().toLowerCase().contains(criterio)
                        || u.getApellido() != null && u.getApellido().toLowerCase().contains(criterio)
                        || u.getEmail() != null && u.getEmail().toLowerCase().contains(criterio))
                .toList();
    }

    private Usuario buscarUsuarioPorEmail(List<Usuario> usuarios, String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        String normalized = email.trim().toLowerCase();
        return usuarios.stream()
                .filter(u -> u.getEmail() != null && u.getEmail().toLowerCase().equals(normalized))
                .findFirst()
                .orElseGet(() -> adminService.listarUsuarios().stream()
                        .filter(u -> u.getEmail() != null && u.getEmail().toLowerCase().equals(normalized))
                        .findFirst()
                        .orElse(null));
    }

    private String redirectUsuarios(String email, String busqueda, boolean verPedidos) {
        StringBuilder sb = new StringBuilder("redirect:/admin/usuarios");
        sb.append("?email=").append(UriUtils.encode(email, StandardCharsets.UTF_8));
        if (busqueda != null && !busqueda.isBlank()) {
            sb.append("&busqueda=").append(UriUtils.encode(busqueda, StandardCharsets.UTF_8));
        }
        if (verPedidos) {
            sb.append("&verPedidos=true");
        }
        return sb.toString();
    }
}
