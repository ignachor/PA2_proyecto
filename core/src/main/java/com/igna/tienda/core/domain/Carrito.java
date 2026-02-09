package com.igna.tienda.core.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "carrito")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", unique = true, nullable = false)
    private Usuario usuarioCliente;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCarrito> items = new ArrayList<>();

    protected Carrito() {
        // Constructor requerido por JPA.
    }

    public Carrito(Usuario usuarioCliente) {
        this.usuarioCliente = Objects.requireNonNull(usuarioCliente, "usuarioCliente requerido");
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuarioCliente() {
        return usuarioCliente;
    }

    public List<DetalleCarrito> getItems() {
        return Collections.unmodifiableList(items);
    }

    // Mantiene la relacion bidireccional carrito <-> detalle_carrito.
    public void anadirProducto(DetalleCarrito item) {
        if (item == null) {
            throw new IllegalArgumentException("Item no proporcionado");
        }
        if (item.getCarrito() != null && item.getCarrito() != this) {
            throw new IllegalArgumentException("El item pertenece a otro carrito");
        }

        for (DetalleCarrito existente : items) {
            if (esMismoProducto(existente, item)) {
                existente.sumarCantidad(item.getCantidad());
                return;
            }
        }

        items.add(item);
        item.setCarrito(this);
    }

    // Elimina item y corta la relacion con el carrito actual.
    public void removerProducto(DetalleCarrito item) {
        if (item == null) {
            return;
        }
        if (items.remove(item)) {
            item.setCarrito(null);
        }
    }

    public void vaciar() {
        List<DetalleCarrito> copia = new ArrayList<>(items);
        for (DetalleCarrito item : copia) {
            removerProducto(item);
        }
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleCarrito item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    private boolean esMismoProducto(DetalleCarrito a, DetalleCarrito b) {
        if (a.getProducto() == null || b.getProducto() == null) {
            return false;
        }
        Long idA = a.getProducto().getId();
        Long idB = b.getProducto().getId();
        if (idA != null && idB != null) {
            return idA.equals(idB);
        }
        return a.getProducto() == b.getProducto();
    }
}
