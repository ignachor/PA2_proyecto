package com.igna.tienda.core.services;
import java.util.List;
import java.util.UUID;
import com.igna.tienda.core.domain.value.Direccion;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.repositories.UsuarioRepository; 
import com.igna.tienda.core.domain.enums.Rol;
import com.igna.tienda.core.domain.Producto;

public class AdminService {
    private final UsuarioRepository uRepo;
    
    public AdminService(UsuarioRepository UsuarioRepository) {
        this.uRepo = UsuarioRepository;
    }
 
    //CU-18: Activar Usuario
    public void ActivarUsuario(Usuario usuario){
        Usuario bajarUsuario = uRepo.buscarPorEmail(usuario.getEmail());
        if(bajarUsuario != null && bajarUsuario.getId().toString().equals(usuario.getId().toString()))
        {
            bajarUsuario = new Usuario(bajarUsuario.getId(), bajarUsuario.getNombre(), bajarUsuario.getApellido(), bajarUsuario.getDni(), bajarUsuario.getEmail(), bajarUsuario.getDireccion(), bajarUsuario.getPassword(), bajarUsuario.getRol());
            bajarUsuario.activar(); 
            uRepo.guardar(bajarUsuario); 
        } 
    }

    //CU-19: Desactivar Usuario
    public void DesactivarUsuario(Usuario usuario){ 
        Usuario bajarUsuario = uRepo.buscarPorEmail(usuario.getEmail());
        if(bajarUsuario != null && bajarUsuario.getId().toString().equals(usuario.getId().toString()))
        {
            bajarUsuario = new Usuario(bajarUsuario.getId(), bajarUsuario.getNombre(), bajarUsuario.getApellido(), bajarUsuario.getDni(), bajarUsuario.getEmail(), bajarUsuario.getDireccion(), bajarUsuario.getPassword(), bajarUsuario.getRol());
            bajarUsuario.desactivar(); 
            uRepo.guardar(bajarUsuario);
        }
    }

    
    //CU-20: Buscar Usuario por email
    public Usuario BuscarUsuario(String email){
         Usuario buscarUsuario = uRepo.buscarPorEmail(email);
         if(buscarUsuario != null){
            return buscarUsuario;
         } 
         return null;
    }


    //CU-17: Listar Usuario
    public List<Usuario> ListarUsuarios(){
        return uRepo.listarTodos();
    }



    

    public void DarBajaPedido(){
        //toDO: Implementar el método para dar de baja a un pedido
    }

    public void ModificarPedido(){
        //TODO: Implementar el método para modificar los datos de un pedido
    }

    public void ListarPedidos(){  
        //TODO: Implementar el método para listar todos los pedidos
    }
}

