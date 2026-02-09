package com.igna.tienda.core.domain;

import com.igna.tienda.core.domain.enums.EstadoPedido;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario usuarioCliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> items = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // IMPLEMENTACION: estado requerido para CU de gestion de pedidos.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    protected Pedido() {
        // Constructor requerido por JPA.
    }

    public Pedido(Usuario usuarioCliente) {
        this.usuarioCliente = Objects.requireNonNull(usuarioCliente, "usuarioCliente requerido");
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuarioCliente() {
        return usuarioCliente;
    }

    public List<DetallePedido> getItems() {
        return Collections.unmodifiableList(items);
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    // Mantiene la relacion bidireccional pedido <-> detalle_pedido.
    public void addItem(DetallePedido item) {
        if (item == null) {
            throw new IllegalArgumentException("Item no proporcionado");
        }
        if (item.getPedido() != null && item.getPedido() != this) {
            throw new IllegalArgumentException("El item pertenece a otro pedido");
        }

        items.add(item);
        item.setPedido(this);
    }

    public void removeItem(DetallePedido item) {
        if (item == null) {
            return;
        }
        if (items.remove(item)) {
            item.setPedido(null);
        }
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetallePedido item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    // IMPLEMENTACION: permite que admin cambie estado del pedido.
    public void cambiarEstado(EstadoPedido nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("Estado no proporcionado");
        }
        this.estado = nuevoEstado;
    }
}
