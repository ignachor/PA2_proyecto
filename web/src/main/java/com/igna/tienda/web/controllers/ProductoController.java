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

/**
 * Controller para el catálogo de productos (vista de CLIENTE).
 * 
 * Reutiliza ProductoServiceTx de la capa infra.
 */
@Controller
@RequestMapping("/catalogo")
public class ProductoController {

    private final ProductoServiceTx productoService;

    public ProductoController(EntityManagerFactory emf) {
        this.productoService = new ProductoServiceTx(emf);
    }

    /**
     * GET /catalogo - Listar todos los productos activos
     */
    @GetMapping
    public String catalogo(@RequestParam(value = "categoria", required = false) CategoriaProducto categoria,
                          @RequestParam(value = "busqueda", required = false) String busqueda,
                          Authentication auth,
                          Model model) {
        try {
            List<Producto> productos;

            // Filtrar por categoría o búsqueda
            if (categoria != null) {
                productos = productoService.buscarProductosPorCategoria(categoria);
            } else {
                productos = productoService.listarProductosActivos();
            }

            model.addAttribute("productos", productos);
            model.addAttribute("categorias", CategoriaProducto.values());
            model.addAttribute("categoriaSeleccionada", categoria);
            model.addAttribute("usuario", auth.getName());

            return "catalogo";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar catálogo: " + e.getMessage());
            model.addAttribute("productos", List.of());
            model.addAttribute("categorias", CategoriaProducto.values());
            return "catalogo";
        }
    }
}
