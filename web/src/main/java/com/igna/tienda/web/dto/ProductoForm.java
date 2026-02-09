package com.igna.tienda.web.dto;

import com.igna.tienda.core.domain.enums.CategoriaProducto;

/**
 * DTO para formularios de productos (agregar/modificar).
 */
public class ProductoForm {
    private Long id;
    private String nombre;
    private String descripcion;
    private CategoriaProducto categoria;
    private double precio;
    private int cantidad;
    private int cantidadMinimo;
    private int fechaVencimiento;
    private boolean stock;

    public ProductoForm() {
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProducto categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCantidadMinimo() {
        return cantidadMinimo;
    }

    public void setCantidadMinimo(int cantidadMinimo) {
        this.cantidadMinimo = cantidadMinimo;
    }

    public int getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(int fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public boolean isStock() {
        return stock;
    }

    public void setStock(boolean stock) {
        this.stock = stock;
    }
}
