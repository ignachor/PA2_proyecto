package com.igna.tienda.core.services;

import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.domain.enums.CategoriaProducto;
import com.igna.tienda.core.repositories.ProductoRepository;
import java.util.ArrayList;
import java.util.List;
public class ProductoService {
    private ProductoRepository pRepo;
    public ProductoService(ProductoRepository pRepo) {
        this.pRepo = pRepo;  
    }

    //CU-12: Agregar Producto
    public void AltaProducto(Producto producto){
       
        if (producto == null) {
            throw new IllegalArgumentException("Producto no proporcionado");
        }
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new IllegalArgumentException("Nombre de producto no proporcionado");
        }
        if (producto.getCategoria() == null) {
            throw new IllegalArgumentException("Categoria de producto no proporcionada");
        }
        if (producto.getDescripcion() == null || producto.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("Descripcion de producto no proporcionada");
        }
        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("Precio de producto invalido");
        }
        if (producto.getCantidad() < 0) {
            throw new IllegalArgumentException("Cantidad de producto invalida");
        }
        if (producto.getCantidadMinimo() < 0) {
            throw new IllegalArgumentException("Cantidad minima invalida");
        }

        if (producto.getFechaVencimiento() < 0) {
            throw new IllegalArgumentException("Fecha de Vencimiento necesaria");
        }

        Producto productoDarDeAlta = new Producto(
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getCategoria(),
            producto.getPrecio(),
            producto.getCantidad(),
            producto.getCantidadMinimo(),
            producto.getFechaVencimiento(),
            producto.getStock()
        );

        if (productoDarDeAlta.getCantidad() > 0) 
        {
            productoDarDeAlta.hayStock();
        } 
        else 
        {
            productoDarDeAlta.noHayStock();
        }

        pRepo.guardar(productoDarDeAlta);    
    }

    //CU-14: Dar de baja producto
    public void BajaProducto(Producto producto){
        if (producto == null) {
            throw new IllegalArgumentException("Producto no proporcionado");
        }   
        Producto productoBajar = producto;
        if (productoBajar.getNombre() == null){
            throw new IllegalArgumentException("Nombre no proporcionada para darse de baja");
        }

        productoBajar.noHayStock();
        pRepo.guardar(productoBajar);
      
    }


    //CU-13: Modificar Producto
    public void ModificarProducto(Producto modificarProducto){
        if (modificarProducto == null) {
            throw new IllegalArgumentException("Producto no proporcionado");
        } 
        if (modificarProducto.getNombre() == null) {
            throw new IllegalArgumentException("Nombre de producto obligatorio");
        }

        Producto productoExistente = pRepo.buscarPorID(modificarProducto.getId());

        if(productoExistente == null) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        if ((modificarProducto.getNombre() == null) || (modificarProducto.getNombre().isEmpty()) || (modificarProducto.getNombre().isBlank())){
            throw new IllegalArgumentException("Nombre no proporcionado, es un dato obligatorio");
        } else {
            // MODIFICACION: se propaga stock para habilitar reactivacion/desactivacion en la edicion.
            productoExistente.cambiarDatosProducto(modificarProducto.getNombre(), modificarProducto.getDescripcion(), modificarProducto.getCategoria(),
                                                modificarProducto.getPrecio(), modificarProducto.getCantidad(), modificarProducto.getCantidadMinimo(), 
                                                modificarProducto.getFechaVencimiento(), modificarProducto.getStock());
            pRepo.guardar(productoExistente);
        }
    }


    //CU- : Listar Productos
    public List<Producto> ListarProductos(){
        return pRepo.listarProductos();
        }   

    // CU usuario: listar solo productos activos/en stock.
    public List<Producto> ListarProductosActivos() {
        List<Producto> activos = new ArrayList<>();
        for (Producto producto : pRepo.listarProductos()) {
            if (producto != null && producto.getStock()) {
                activos.add(producto);
            }
        }
        return activos;
    }

    //CU- : Buscar Productos
    public Producto BuscarProducto(String nombre){
        Producto buscarProducto = pRepo.buscarPorNombre(nombre);
        // Regla para usuario: solo puede encontrar productos activos/en stock.
        if(buscarProducto != null && buscarProducto.getStock())
        {
            return buscarProducto;
        }
        return null;
    }

    // Busqueda administrativa: permite ver tambien productos dados de baja.
    public Producto BuscarProductoAdmin(String nombre){
        return pRepo.buscarPorNombre(nombre);
    }

    //CU- : Buscar Productos por categoria 
    public List<Producto> BuscarProductoCategoria(CategoriaProducto categoriaProducto){
        return pRepo.buscarProductosPorCategoria(categoriaProducto);
    }

    // CU usuario: buscar por categoria solo productos activos/en stock.
    public List<Producto> BuscarProductoCategoriaUsuario(CategoriaProducto categoriaProducto){
        List<Producto> activos = new ArrayList<>();
        for (Producto producto : pRepo.buscarProductosPorCategoria(categoriaProducto)) {
            if (producto != null && producto.getStock()) {
                activos.add(producto);
            }
        }
        return activos;
    }
}
