package com.igna.tienda.core.services;

import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.repositories.ProductoRepository;
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
        if (producto.getCategoria() == null || producto.getCategoria().isBlank()) {
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

        Producto productoDarDeAlta = new Producto(
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getCategoria(),
            producto.getPrecio(),
            producto.getCantidad(),
            producto.getCantidadMinimo(),
            producto.getStock()
        );
        if (productoDarDeAlta.getCantidad() > 0) {
            productoDarDeAlta.hayStock();
        } else {
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

    public void ModificarProducto(Producto producto){
        if (producto == null) {
            throw new IllegalArgumentException("Producto no proporcionado");
        }   
        //TODO: Implementar el método para modificar los datos de un producto
    }

    public void ListarProductos(Producto producto){
        if (producto == null) {
            throw new IllegalArgumentException("Producto no proporcionado");
        }   
        //TODO: Implementar el método para listar todos los productos
    }

    public void BuscarProducto(Producto producto){
        if (producto == null) {
            throw new IllegalArgumentException("Producto no proporcionado"); }
        //TODO: Implementar el método para buscar un producto por su nombre o ID
    }
}
