package com.igna.tienda.web.controllers;

import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.domain.enums.CategoriaProducto;
import com.igna.tienda.infra.services.ProductoServiceTx;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

/**
 * Controller para la tienda de productos (vista CLIENTE).
 */
@Controller
@RequestMapping({"/tiendaCliente", "/catalogo"})
public class ProductoController {

    private final ProductoServiceTx productoServiceTx;

    public ProductoController(EntityManagerFactory emf) {
        this.productoServiceTx = new ProductoServiceTx(emf);
    }

    /**
     * GET /tiendaCliente - Lista productos activos con filtros opcionales.
     */
    @GetMapping
    public String catalogo(@RequestParam(value = "categoria", required = false) CategoriaProducto categoria,
                           @RequestParam(value = "busqueda", required = false) String busqueda,
                           Authentication auth,
                           Model model) {
        try {
            List<Producto> productos = productoServiceTx.listarProductosActivos();
            String busquedaNormalizada = busqueda == null ? "" : busqueda.trim();

            if (categoria != null) {
                productos = productos.stream()
                        .filter(producto -> producto.getCategoria() == categoria)
                        .toList();
            }

            if (!busquedaNormalizada.isEmpty()) {
                String criterio = busquedaNormalizada.toLowerCase(Locale.ROOT);
                productos = productos.stream()
                        .filter(producto -> producto.getNombre() != null
                                && producto.getNombre().toLowerCase(Locale.ROOT).contains(criterio))
                        .toList();
            }

            model.addAttribute("productos", productos);
            model.addAttribute("categorias", CategoriaProducto.values());
            model.addAttribute("categoriaSeleccionada", categoria);
            model.addAttribute("busqueda", busquedaNormalizada);
            model.addAttribute("usuario", auth.getName());

            return "tiendaCliente";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar tienda: " + e.getMessage());
            model.addAttribute("productos", List.of());
            model.addAttribute("categorias", CategoriaProducto.values());
            return "tiendaCliente";
        }
    }
}