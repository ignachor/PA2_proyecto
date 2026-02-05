package com.igna.tienda.core.services;

import java.util.UUID;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.repositories.UsuarioRepository;
import com.igna.tienda.core.domain.enums.Rol;
public class AuthService {
    private final UsuarioRepository UsuarioRepository;
    public AuthService(UsuarioRepository UsuarioRepository) {
        this.UsuarioRepository = UsuarioRepository;
    }

    //CU-01: Registrar Cliente
    public Usuario registrar(String nombre, String apellido, String email, String password, Rol rol) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        String emailNorm = email.trim().toLowerCase();
        
        if(UsuarioRepository.buscarPorEmail(emailNorm) != null) {
            throw new IllegalAccessError("El email ingresado ya está en uso");
        }

        Usuario u = new Usuario(
            UUID.randomUUID(),
            nombre,
            apellido,
            emailNorm,
            password,
            rol = Rol.CLIENTE
        );
        UsuarioRepository.guardar(u);
        return u;
}
        //CU-02: Iniciar Sesión Cliente

        public Usuario iniciarSesion(String email, String password) {
            String emailNorm = email.trim().toLowerCase();
            Usuario usuarioBuscar = UsuarioRepository.buscarPorEmail(emailNorm);
            if(usuarioBuscar == null) {
                throw new IllegalArgumentException("Email o contraseña incorrectos");
            }

            if(!usuarioBuscar.esActivo()) {
                throw new IllegalAccessError("El usuario se encuentra desactivado");
            }

            if(!usuarioBuscar.getPassword().equals(password)) {
                throw new IllegalArgumentException("Contraseña incorrecta");
            }

            return usuarioBuscar;
        }
    }
