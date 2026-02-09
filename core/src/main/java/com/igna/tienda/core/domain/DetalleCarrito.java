package com.igna.tienda.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.util.Objects;



@Entity
@Table(
        name = "detalle_carrito",
        uniqueConstraints = @UniqueConstraint(columnNames = {"carrito_id", "producto_id"})
)
public class DetalleCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private int cantidad;

    protected DetalleCarrito() {
        // Constructor requerido por JPA.
    }

    public DetalleCarrito(Producto producto, int cantidad) {
        this.producto = Objects.requireNonNull(producto, "producto requerido");
        setCantidad(cantidad);
    }

    public Long getId() {
        return id;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad invalida");
        }
        this.cantidad = cantidad;
    }

    public void sumarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad a sumar invalida");
        }
        this.cantidad += cantidad;
    }

    public BigDecimal getSubtotal() {
        return BigDecimal.valueOf(producto.getPrecio()).multiply(BigDecimal.valueOf(cantidad));
    }
}
