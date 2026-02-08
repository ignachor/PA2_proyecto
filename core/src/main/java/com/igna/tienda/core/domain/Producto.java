package com.igna.tienda.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String descripcion;
    @Column(nullable = false)
    private String categoria;
    @Column(nullable = false)
    private double precio;
    @Column(nullable = false)
    private int cantidad;
    @Column(nullable = false)
    private int cantidadMinimo;
    @Column(nullable = false)
    private int fechaVencimiento;
    @Column(nullable = false)
    private boolean stock; // Indica si el producto está en stock o no

    public Producto(){

    }

    public Producto(String nombre, String descripcion, String categoria, double precio, int cantidad, int cantidadMinimo, boolean stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidad = cantidad;
        this.cantidadMinimo = cantidadMinimo;
        this.stock = stock;
    }

    public Long getId() {
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
