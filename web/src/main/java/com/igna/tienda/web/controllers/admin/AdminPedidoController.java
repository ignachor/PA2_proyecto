package com.igna.tienda.web.controllers.admin;

import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.domain.enums.EstadoPedido;
import com.igna.tienda.infra.services.AdminServiceTx;
import com.igna.tienda.infra.services.PedidoServiceTx;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller para gestión de pedidos (vista de ADMIN).
 * 
 * Reutiliza AdminServiceTx y PedidoServiceTx de la capa infra.
 */
@Controller
@RequestMapping("/admin/pedidos")
public class AdminPedidoController {

    private final AdminServiceTx adminService;
    private final PedidoServiceTx pedidoService;

    public AdminPedidoController(EntityManagerFactory emf) {
        this.adminService = new AdminServiceTx(emf);
        this.pedidoService = new PedidoServiceTx(emf);
    }

    /**
     * GET /admin/pedidos - Listar todos los pedidos
     */
    @GetMapping
    public String listarPedidos(@RequestParam(value = "estado", required = false) EstadoPedido estado,
                               Model model) {
        try {
            List<Pedido> pedidos = adminService.listarTodosPedidos();

            // Filtrar por estado si se especifica
            if (estado != null) {
                pedidos = pedidos.stream()
                    .filter(p -> p.getEstado() == estado)
                    .toList();
            }

            model.addAttribute("pedidos", pedidos);
            model.addAttribute("estados", EstadoPedido.values());
            model.addAttribute("estadoSeleccionado", estado);

            return "admin/pedidos";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar pedidos: " + e.getMessage());
            model.addAttribute("pedidos", List.of());
            model.addAttribute("estados", EstadoPedido.values());
            return "admin/pedidos";
        }
    }

    /**
     * GET /admin/pedidos/{id} - Ver detalle de un pedido
     */
    @GetMapping("/{id}")
    public String verDetallePedido(@PathVariable Long id, Model model) {
        try {
            Pedido pedido = pedidoService.buscarPedidoPorId(id);

            model.addAttribute("pedido", pedido);
            model.addAttribute("estados", EstadoPedido.values());

            return "admin/pedido-detalle";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar pedido: " + e.getMessage());
            return "redirect:/admin/pedidos";
        }
    }

    /**
     * POST /admin/pedidos/cambiar-estado - Cambiar estado de un pedido
     */
    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam Long pedidoId,
                               @RequestParam EstadoPedido nuevoEstado,
                               RedirectAttributes redirectAttributes) {
        try {
            adminService.cambiarEstadoPedido(pedidoId, nuevoEstado);

            redirectAttributes.addFlashAttribute("mensaje", "Estado del pedido actualizado a: " + nuevoEstado);
            return "redirect:/admin/pedidos/" + pedidoId;

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/pedidos/" + pedidoId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar estado: " + e.getMessage());
            return "redirect:/admin/pedidos/" + pedidoId;
        }
    }
}
