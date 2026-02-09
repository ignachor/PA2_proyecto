package com.igna.tienda.web.controllers;

import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.persistence.jpa.JpaUsuarioRepository;
import com.igna.tienda.infra.services.PedidoServiceTx;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

/**
 * Controller para pedidos (vista de CLIENTE).
 * 
 * Reutiliza PedidoServiceTx de la capa infra.
 */
@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoServiceTx pedidoService;
    private final EntityManagerFactory emf;

    public PedidoController(EntityManagerFactory emf) {
        this.emf = emf;
        this.pedidoService = new PedidoServiceTx(emf);
    }

    /**
     * GET /pedidos - Listar todos los pedidos del cliente
     */
    @GetMapping
    public String listarPedidos(Authentication auth, Model model) {
        try {
            UUID clienteId = obtenerClienteId(auth.getName());
            List<Pedido> pedidos = pedidoService.listarPedidosPorCliente(clienteId);

            model.addAttribute("pedidos", pedidos);
            return "pedidos";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar pedidos: " + e.getMessage());
            model.addAttribute("pedidos", List.of());
            return "pedidos";
        }
    }

    /**
     * GET /pedidos/{id} - Ver detalle de un pedido
     */
    @GetMapping("/{id}")
    public String verDetallePedido(@PathVariable Long id, Authentication auth, Model model) {
        try {
            Pedido pedido = pedidoService.buscarPedidoPorId(id);

            // Verificar que el pedido pertenece al cliente logueado
            UUID clienteId = obtenerClienteId(auth.getName());
            if (!pedido.getUsuarioCliente().getId().equals(clienteId)) {
                model.addAttribute("error", "No tienes permiso para ver este pedido");
                return "redirect:/pedidos";
            }

            model.addAttribute("pedido", pedido);
            return "pedido-detalle";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar pedido: " + e.getMessage());
            return "redirect:/pedidos";
        }
    }

    /**
     * Método auxiliar para obtener el UUID del cliente logueado
     */
    private UUID obtenerClienteId(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            JpaUsuarioRepository usuarioRepo = new JpaUsuarioRepository(em);
            Usuario usuario = usuarioRepo.buscarPorEmail(email);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }
            return usuario.getId();
        } finally {
            em.close();
        }
    }
}
