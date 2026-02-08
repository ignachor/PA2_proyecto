package com.igna.tienda.core.domain;

public class Producto {
    String id;
    String nombre;
    String descripcion;
    String categoria;
    double precio;
    int cantidad;
    int cantidadMinimo;
    int fechaVencimiento;
    boolean stock; // Indica si el producto está en stock o no

    public Producto(){

    }

    public Producto(String id, String nombre, String descripcion, String categoria, double precio, int cantidad, int cantidadMinimo, boolean stock) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidad = cantidad;
        this.cantidadMinimo = cantidadMinimo;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getCantidadMinimo() {
        return cantidadMinimo;
    }

    public boolean getStock() {
        return stock;
    }

    public void noHayStock() {
        this.stock = false;
    }

    public void hayStock() {
        this.stock = true;
    }

}
