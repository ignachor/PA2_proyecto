package com.igna.tienda.core.services;
import java.util.UUID;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.repositories.UsuarioRepository; 


public class UsuarioService {
    private final UsuarioRepository uRepo;
    public UsuarioService(UsuarioRepository UsuarioRepository) {    
        this.uRepo = UsuarioRepository;
     }

    

    /*
    CU-20: Modificar UsuARIO
    
    Pasos:
    ¿Existe el usuario que quiero editar?

    ¿Los datos básicos son válidos?

    ¿El email cambió?

    No → sigo

    Sí → ¿me dieron contraseña?

        No → error

        Sí → ¿coincide con la actual?

            No → error

    ¿El nuevo email está libre?

    Aplico cambios

    Persisto
    */
    public void editarDatosPersonales(Usuario modificarUsuario){
        if (modificarUsuario == null){
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        if (modificarUsuario.getId() == null){
            throw new IllegalArgumentException("ID de usuario no proporcionado");
        }
        Usuario usuarioExistente = uRepo.buscarPorId(modificarUsuario.getId());
        if(usuarioExistente == null){
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        if (modificarUsuario.getNombre() == null || modificarUsuario.getApellido() == null){
            throw new IllegalArgumentException("Datos básicos incompletos");
        } else {
            usuarioExistente.cambiarDatosPersonales(modificarUsuario.getNombre(), modificarUsuario.getApellido());
            uRepo.guardar(usuarioExistente);
        }

        
    
    
    }

//SE DIVIDE LA LOGICA DE MODIFICAR USUARIO EN DOS METODOS, UNO PARA EDITAR LOS DATOS PERSONALES Y OTRO PARA EDITAR EL EMAIL, YA QUE ESTE ULTIMO REQUIERE VALIDACIONES ADICIONALES.
  public void editarEmail(Usuario modificarUsuario){
    //logica para editar el email del usuario
        if (modificarUsuario == null){
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        if (modificarUsuario.getId() == null){
            throw new IllegalArgumentException("ID de usuario no proporcionado");
        }
        Usuario usuarioExistente = uRepo.buscarPorId(modificarUsuario.getId());
        if(usuarioExistente == null){
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        if (modificarUsuario.getEmail() == null){
            throw new IllegalArgumentException("Email no proporcionado");
        } else if (!modificarUsuario.getEmail().equals(usuarioExistente.getEmail())){
            if (modificarUsuario.getPassword() == null){
                throw new IllegalArgumentException("Contraseña no proporcionada");
            } else if (!modificarUsuario.getPassword().equals(usuarioExistente.getPassword())){
                throw new IllegalArgumentException("Contraseña incorrecta");
            } else {
                if (uRepo.buscarPorEmail(modificarUsuario.getEmail()) != null){
                    throw new IllegalArgumentException("El nuevo email ya está en uso");
                } else {
                    usuarioExistente.cambiarEmail(modificarUsuario.getEmail());
                    uRepo.guardar(usuarioExistente);
                }
            }
        }
  }  
}



