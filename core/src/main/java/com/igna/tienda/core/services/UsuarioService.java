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
    public Usuario editarDatosPersonales(Usuario modificarUsuario){
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

        if ((modificarUsuario.getNombre() == null || modificarUsuario.getNombre().isEmpty() || modificarUsuario.getNombre().isBlank()) || (modificarUsuario.getApellido() == null || modificarUsuario.getApellido().isEmpty() || modificarUsuario.getApellido().isBlank())){
            throw new IllegalArgumentException("Nombre o apellido no proporcionado, son datos obligatorios");
        } else {
            usuarioExistente.cambiarDatosPersonales(modificarUsuario.getNombre(), modificarUsuario.getApellido());
            uRepo.guardar(usuarioExistente);
            return usuarioExistente;
        }
        
    }

//SE DIVIDE LA LOGICA DE MODIFICAR USUARIO EN DOS METODOS, UNO PARA EDITAR LOS DATOS PERSONALES Y OTRO PARA EDITAR EL EMAIL, YA QUE ESTE ULTIMO REQUIERE VALIDACIONES ADICIONALES.
  public Usuario editarEmail(Usuario modificarUsuario){
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

        if (modificarUsuario.getEmail() == null || modificarUsuario.getEmail().isEmpty() || !modificarUsuario.getEmail().contains("@") || modificarUsuario.getPassword().isBlank()){
            throw new IllegalArgumentException("Email no proporcionado");
        } else if (!modificarUsuario.getEmail().equals(usuarioExistente.getEmail())){
            if (modificarUsuario.getPassword() == null || modificarUsuario.getPassword().isEmpty() || modificarUsuario.getPassword().isBlank()){
                throw new IllegalArgumentException("Contraseña no proporcionada");
            } else if (!modificarUsuario.getPassword().equals(usuarioExistente.getPassword())){
                throw new IllegalArgumentException("Contraseña incorrecta");
            } else {
                if (uRepo.buscarPorEmail(modificarUsuario.getEmail().toLowerCase()) != null){
                    throw new IllegalArgumentException("El nuevo email ya está en uso");
                } else {
                    usuarioExistente.cambiarEmail(modificarUsuario.getEmail());
                    uRepo.guardar(usuarioExistente);
                    return usuarioExistente;
                }
            }
        } 
        return null; //si el email no cambió, no hago nada y retorno null para indicar que no hubo cambios.  
  }
  
    public Usuario editarDireccion(Usuario modificarUsuario){
        if(modificarUsuario == null){
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        if(modificarUsuario.getId() == null){
            throw new IllegalArgumentException("ID de usuario no proporcionado");
        }
        Usuario usuarioExistente = uRepo.buscarPorId(modificarUsuario.getId());
        if(usuarioExistente == null){
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        if(modificarUsuario.getDireccion().getCalle() == null || modificarUsuario.getDireccion().getCalle().isEmpty() || modificarUsuario.getDireccion().getCalle().isBlank() ||
           modificarUsuario.getDireccion().getNumero() == null || modificarUsuario.getDireccion().getNumero().isEmpty() || modificarUsuario.getDireccion().getNumero().isBlank() || 
           modificarUsuario.getDireccion().getCiudad() == null || modificarUsuario.getDireccion().getCiudad().isEmpty() || modificarUsuario.getDireccion().getCiudad().isBlank() ||
           modificarUsuario.getDireccion().getProvincia() == null || modificarUsuario.getDireccion().getProvincia().isEmpty() || modificarUsuario.getDireccion().getProvincia().isBlank() ||
           modificarUsuario.getDireccion().getCodigoPostal() == null || modificarUsuario.getDireccion().getCodigoPostal().isEmpty() || modificarUsuario.getDireccion().getCodigoPostal().isBlank()){
            throw new IllegalArgumentException("Datos de direccion incompletos, todos los campos son obligatorios");
        } else {
            usuarioExistente.cambiarDireccion(modificarUsuario.getDireccion());
            uRepo.guardar(usuarioExistente);
            return usuarioExistente;
        }
    }
}



