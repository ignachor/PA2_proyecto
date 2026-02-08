package com.igna.tienda.core.services;

import java.util.UUID;
import com.igna.tienda.core.domain.value.Direccion;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.repositories.UsuarioRepository;
import com.igna.tienda.core.domain.enums.Rol;
public class AuthService 
{
    private final UsuarioRepository uRepo;
    public AuthService(UsuarioRepository UsuarioRepository) 
    {
        this.uRepo = UsuarioRepository;
    }

    //CU-01: Registrar Cliente
    public Usuario registrar(String nombre, String apellido, String dni, String email, Direccion direccion, String password, Rol rol) 
    {

        String emailNorm = normalizarEmail(email);
        String passwordVal = validarContraseña(password);
        String dniVal = validarDni(dni);
        if(uRepo.buscarPorEmail(emailNorm) != null) 
        {
            throw new IllegalAccessError("El email ingresado ya está en uso");
        }

        Usuario u = new Usuario(
            UUID.randomUUID(),
            nombre,
            apellido,
            dniVal,
            emailNorm,
            direccion,
            passwordVal,
            rol
        );
        uRepo.guardar(u);
        return u;
    }
        //CU-02: Iniciar Sesión Cliente/Admin

        public Usuario iniciarSesion(String email, String password) 
        {
            String emailNorm = normalizarEmail(email);
            String passwordVal = validarContraseña(password);
            Usuario usuarioBuscar = uRepo.buscarPorEmail(emailNorm);
            
            if(usuarioBuscar == null) 
            {
                throw new IllegalArgumentException("Email o contraseña incorrectos");
            }

            if(usuarioBuscar.getRol() == Rol.CLIENTE) 
            {

            if(!usuarioBuscar.esActivo()) 
            {
                throw new IllegalArgumentException("El usuario se encuentra desactivado");
            }

            if(!usuarioBuscar.getPassword().equals(passwordVal)) 
            {
                throw new IllegalArgumentException("Contraseña incorrecta");
            }

            return usuarioBuscar; 
            }

            if(!usuarioBuscar.getPassword().equals(passwordVal)) 
            {
                throw new IllegalArgumentException("Contraseña incorrecta");
            }
           
            return usuarioBuscar;
        }
            public static String normalizarEmail(String email) 
            {
            if (email == null || email.isBlank()) 
            {
                throw new IllegalArgumentException("El email no puede estar vacío");
            }
            return  email.trim().toLowerCase();
            }

            public static String validarContraseña(String password) 
            {
            if (password == null || password.isBlank()) 
            {
                throw new IllegalArgumentException("La contraseña no puede estar vacía");
            }
            return password;
            }

            public static String validarDni(String dni) 
            {
            if (dni == null || dni.isBlank()) 
            {
                throw new IllegalArgumentException("El DNI no puede estar vacio");
            }
            return dni.trim();
            }
}
    
