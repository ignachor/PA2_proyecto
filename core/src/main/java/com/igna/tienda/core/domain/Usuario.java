package com.igna.tienda.core.domain;
import com.igna.tienda.core.domain.enums.Rol;
import java.util.UUID;

public class Usuario {
    private UUID id;
    private String nombre;
    private String apellido;
    private String email;
    private boolean activo; 
    private String password;
    private Rol rol;

    public Usuario() {
        this.id = UUID.randomUUID();
        this.activo = true;
    }

    public Usuario(UUID id, String nombre, String apellido, String email, String password, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.activo = true;
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

    public String getPassword() {
        return password;
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
}
