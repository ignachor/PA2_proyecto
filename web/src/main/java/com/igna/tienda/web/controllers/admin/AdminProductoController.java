package com.igna.tienda.web.controllers.admin;

import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.domain.enums.CategoriaProducto;
import com.igna.tienda.infra.services.AdminServiceTx;
import com.igna.tienda.web.dto.ProductoForm;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller para gestión de productos (vista de ADMIN).
 * 
 * Reutiliza AdminServiceTx de la capa infra.
 */
@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

    private final AdminServiceTx adminService;

    public AdminProductoController(EntityManagerFactory emf) {
        this.adminService = new AdminServiceTx(emf);
    }

    /**
     * GET /admin/productos - Listar todos los productos (incluso inactivos)
     */
    @GetMapping
    public String listarProductos(@RequestParam(value = "categoria", required = false) CategoriaProducto categoria,
                                  Model model) {
        try {
            List<Producto> productos;

            if (categoria != null) {
                productos = adminService.buscarProductosPorCategoria(categoria);
            } else {
                productos = adminService.listaProductos();
            }

            model.addAttribute("productos", productos);
            model.addAttribute("categorias", CategoriaProducto.values());
            model.addAttribute("categoriaSeleccionada", categoria);

            return "admin/productos";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar productos: " + e.getMessage());
            model.addAttribute("productos", List.of());
            model.addAttribute("categorias", CategoriaProducto.values());
            return "admin/productos";
        }
    }

    /**
     * GET /admin/productos/agregar - Mostrar formulario para agregar producto
     */
    @GetMapping("/agregar")
    public String formularioAgregar(Model model) {
        model.addAttribute("form", new ProductoForm());
        model.addAttribute("categorias", CategoriaProducto.values());
        model.addAttribute("accion", "Agregar");
        return "admin/producto-form";
    }

    /**
     * POST /admin/productos/agregar - Agregar nuevo producto
     */
    @PostMapping("/agregar")
    public String agregarProducto(@ModelAttribute ProductoForm form, RedirectAttributes redirectAttributes) {
        try {
            Producto producto = new Producto(
                form.getNombre(),
                form.getDescripcion(),
                form.getCategoria(),
                form.getPrecio(),
                form.getCantidad(),
                form.getCantidadMinimo(),
                form.getFechaVencimiento(),
                form.isStock()
            );

            adminService.agregarProducto(producto);

            redirectAttributes.addFlashAttribute("mensaje", "Producto agregado exitosamente");
            return "redirect:/admin/productos";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/productos/agregar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agregar producto: " + e.getMessage());
            return "redirect:/admin/productos/agregar";
        }
    }

    /**
     * GET /admin/productos/editar/{id} - Mostrar formulario para editar producto
     */
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        try {
            Producto producto = obtenerProductoPorId(id);
            if (producto == null) {
                model.addAttribute("error", "Producto no encontrado");
                return "redirect:/admin/productos";
            }

            ProductoForm form = new ProductoForm();
            form.setId(producto.getId());
            form.setNombre(producto.getNombre());
            form.setDescripcion(producto.getDescripcion());
            form.setCategoria(producto.getCategoria());
            form.setPrecio(producto.getPrecio());
            form.setCantidad(producto.getCantidad());
            form.setCantidadMinimo(producto.getCantidadMinimo());
            form.setFechaVencimiento(producto.getFechaVencimiento());
            form.setStock(producto.getStock());

            model.addAttribute("form", form);
            model.addAttribute("categorias", CategoriaProducto.values());
            model.addAttribute("accion", "Editar");

            return "admin/producto-form";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar producto: " + e.getMessage());
            return "redirect:/admin/productos";
        }
    }

    /**
     * POST /admin/productos/editar - Modificar producto existente
     */
    @PostMapping("/editar")
    public String editarProducto(@ModelAttribute ProductoForm form, RedirectAttributes redirectAttributes) {
        try {
            Producto producto = new Producto(
                form.getId(),
                form.getNombre(),
                form.getDescripcion(),
                form.getCategoria(),
                form.getPrecio(),
                form.getCantidad(),
                form.getCantidadMinimo(),
                form.getFechaVencimiento(),
                form.isStock()
            );

            adminService.modificarProducto(producto);

            redirectAttributes.addFlashAttribute("mensaje", "Producto modificado exitosamente");
            return "redirect:/admin/productos";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/productos/editar/" + form.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al modificar producto: " + e.getMessage());
            return "redirect:/admin/productos/editar/" + form.getId();
        }
    }

    /**
     * POST /admin/productos/baja/{id} - Dar de baja producto
     */
    @PostMapping("/baja/{id}")
    public String darBajaProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Producto producto = obtenerProductoPorId(id);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/admin/productos";
            }

            adminService.darBajaProducto(producto);

            redirectAttributes.addFlashAttribute("mensaje", "Producto dado de baja");
            return "redirect:/admin/productos";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al dar de baja producto: " + e.getMessage());
            return "redirect:/admin/productos";
        }
    }

    private Producto obtenerProductoPorId(Long id) {
        if (id == null) {
            return null;
        }
        return adminService.listaProductos().stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst()
                .orElse(null);
    }
}
