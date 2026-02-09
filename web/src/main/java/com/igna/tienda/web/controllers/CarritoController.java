package com.igna.tienda.web.controllers;

import com.igna.tienda.core.domain.Carrito;
import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.persistence.jpa.JpaUsuarioRepository;
import com.igna.tienda.infra.services.CarritoServiceTx;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Controller para el carrito de compras (CLIENTE).
 * 
 * Reutiliza CarritoServiceTx de la capa infra.
 */
@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoServiceTx carritoService;
    private final EntityManagerFactory emf;

    public CarritoController(EntityManagerFactory emf) {
        this.emf = emf;
        this.carritoService = new CarritoServiceTx(emf);
    }

    /**
     * GET /carrito - Ver el carrito del cliente
     */
    @GetMapping
    public String verCarrito(Authentication auth, Model model) {
        try {
            UUID clienteId = obtenerClienteId(auth.getName());
            Carrito carrito = carritoService.obtenerCarrito(clienteId);

            model.addAttribute("carrito", carrito);
            model.addAttribute("total", carrito.getTotal());

            return "carrito";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar carrito: " + e.getMessage());
            return "carrito";
        }
    }

    /**
     * POST /carrito/agregar - Agregar producto al carrito
     */
    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam Long productoId,
                                  @RequestParam(defaultValue = "1") int cantidad,
                                  Authentication auth,
                                  RedirectAttributes redirectAttributes) {
        try {
            UUID clienteId = obtenerClienteId(auth.getName());
            carritoService.agregarProductoAlCarrito(clienteId, productoId, cantidad);

            redirectAttributes.addFlashAttribute("mensaje", "Producto agregado al carrito");
            return "redirect:/catalogo";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/catalogo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agregar producto: " + e.getMessage());
            return "redirect:/catalogo";
        }
    }

    /**
     * POST /carrito/modificar - Modificar cantidad de producto en carrito
     */
    @PostMapping("/modificar")
    public String modificarCantidad(@RequestParam Long productoId,
                                   @RequestParam int cantidad,
                                   Authentication auth,
                                   RedirectAttributes redirectAttributes) {
        try {
            UUID clienteId = obtenerClienteId(auth.getName());
            carritoService.modificarCantidadProducto(clienteId, productoId, cantidad);

            redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada");
            return "redirect:/carrito";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al modificar cantidad: " + e.getMessage());
            return "redirect:/carrito";
        }
    }

    /**
     * POST /carrito/eliminar - Eliminar producto del carrito
     */
    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Long productoId,
                                   Authentication auth,
                                   RedirectAttributes redirectAttributes) {
        try {
            UUID clienteId = obtenerClienteId(auth.getName());
            carritoService.eliminarProductoDelCarrito(clienteId, productoId);

            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado del carrito");
            return "redirect:/carrito";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar producto: " + e.getMessage());
            return "redirect:/carrito";
        }
    }

    /**
     * POST /carrito/finalizar - Finalizar compra y crear pedido
     */
    @PostMapping("/finalizar")
    public String finalizarCompra(Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            UUID clienteId = obtenerClienteId(auth.getName());
            Pedido pedido = carritoService.finalizarCompra(clienteId);

            redirectAttributes.addFlashAttribute("mensaje", "¡Compra realizada exitosamente! Pedido #" + pedido.getId());
            return "redirect:/pedidos";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al finalizar compra: " + e.getMessage());
            return "redirect:/carrito";
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
