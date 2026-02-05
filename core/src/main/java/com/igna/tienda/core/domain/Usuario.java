package com.igna.tienda.core.domain;
import com.igna.tienda.core.domain.enums.Rol;
import java.util.UUID;

public class Usuario {
    private UUID id;
    private String nombre;
    private String apellido;
    private String email;
    private boolean activo; 
    private String passwordHash;
    private Rol rol;

    public Usuario(UUID id, String nombre, String apellido, String email, String passwordHash, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
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
}
