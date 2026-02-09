package com.igna.tienda.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.igna.tienda.core.domain.enums.CategoriaProducto;
import com.igna.tienda.core.domain.enums.CategoriaProductoConverter;

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
    @Convert(converter = CategoriaProductoConverter.class)
    private CategoriaProducto categoria;
    @Column(nullable = false)
    private double precio;
    @Column(nullable = false)
    private int cantidad;
    @Column(nullable = false)
    private int cantidadMinimo;
    @Column(nullable = false)
    private int fechaVencimiento;
    @Column(nullable = false)
    private boolean stock; // Indica si el producto esta en stock o no

    public Producto() {

    }

    public Producto(String nombre, String descripcion, CategoriaProducto categoria, double precio, int cantidad, int cantidadMinimo, int fechaVencimiento, boolean stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidad = cantidad;
        this.cantidadMinimo = cantidadMinimo;
        this.fechaVencimiento = fechaVencimiento;
        this.stock = stock;
    }

    // MODIFICACION: constructor con id para poder enviar productos a modificar desde la UI.
    public Producto(Long id, String nombre, String descripcion, CategoriaProducto categoria, double precio, int cantidad, int cantidadMinimo, int fechaVencimiento, boolean stock) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidad = cantidad;
        this.cantidadMinimo = cantidadMinimo;
        this.fechaVencimiento = fechaVencimiento;
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

    public CategoriaProducto getCategoria() {
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

    public int getFechaVencimiento() {
        return fechaVencimiento;
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

    // IMPLEMENTACION: descuenta unidades para CU finalizar compra.
    public void descontarCantidad(int cantidadDescontar) {
        if (cantidadDescontar <= 0) {
            throw new IllegalArgumentException("Cantidad a descontar invalida");
        }
        if (cantidadDescontar > this.cantidad) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        this.cantidad -= cantidadDescontar;
        this.stock = this.cantidad > 0;
    }

    // IMPLEMENTACION: repone unidades y reactiva stock si corresponde.
    public void aumentarCantidad(int cantidadAumentar) {
        if (cantidadAumentar <= 0) {
            throw new IllegalArgumentException("Cantidad a aumentar invalida");
        }
        this.cantidad += cantidadAumentar;
        this.stock = this.cantidad > 0;
    }

    // MODIFICACION: se agrega stock para permitir reactivacion/desactivacion en el CU Modificar Producto.
    public void cambiarDatosProducto(String nombre, String descripcion, CategoriaProducto categoria, double precio, int cantidad, int cantidadMinimo, int fechaVencimiento, boolean stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidad = cantidad;
        this.cantidadMinimo = cantidadMinimo;
        this.fechaVencimiento = fechaVencimiento;
        this.stock = stock;
    }

}
