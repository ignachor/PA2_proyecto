package com.igna.tienda.core.domain;
import com.igna.tienda.core.domain.enums.Rol;
import com.igna.tienda.core.domain.value.Direccion;
import java.util.UUID;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "usuario", uniqueConstraints= @UniqueConstraint(columnNames = "email"))
public class Usuario {
    @Id
    @Column(length = 36)
    private String id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String apellido;
    @Column(nullable = false)
    private String dni;
    @Column(nullable = false, unique = true)
    private String email;
    @Embedded
    private Direccion direccion;
    @Column(nullable = false)
    private boolean activo;
    @Column(nullable = false) 
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // MODIFICACION: mappedBy debe coincidir con el atributo de Carrito que referencia a Usuario.
    @OneToOne(mappedBy = "usuarioCliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Carrito carrito;
    // MODIFICACION: mappedBy debe coincidir con el atributo de Pedido que referencia a Usuario.
    @OneToMany(mappedBy = "usuarioCliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> listaPedidos = new ArrayList<>();

    public Usuario() {

    }

    public Usuario(UUID id, String nombre, String apellido, String dni, String email, Direccion direccion, String password, Rol rol) {
        this.id = id.toString();
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.direccion = direccion;
        this.password = password;
        this.rol = rol;
        this.activo = true;
    }

    

    public UUID getId() {
        return UUID.fromString(id);
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }
     
    public Direccion getDireccion() {
        return direccion;
    }

    public String getPassword() {
        return password;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public List<Pedido> getListaPedidos() {
        return listaPedidos;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean esCliente() {
        return rol == Rol.CLIENTE;
    }
    
    public boolean esAdmin() {
        return rol == Rol.ADMIN;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }

    public boolean esActivo() {
        return activo;
    }

    public void cambiarEmail(String email) {
        this.email = email;
    }

    public void cambiarDatosPersonales(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public void cambiarDireccion(Direccion direccion) {
        this.direccion = direccion;
        }

    public void crearCarrito(Carrito carritoNuevo){
        this.carrito = carritoNuevo;
    }
}
