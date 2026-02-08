package com.igna.tienda.core.services;
import java.util.UUID;
import com.igna.tienda.core.domain.value.Direccion;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.repositories.UsuarioRepository; 
import com.igna.tienda.core.domain.enums.Rol;
public class AdminService {
    private final UsuarioRepository uRepo;
    public AdminService(UsuarioRepository UsuarioRepository) {
        this.uRepo = UsuarioRepository;
    }

    //CU-18: Alta UsuARIO
    public void AltaUsuario(String nombre, String apellido, Direccion direccion, String email, String password){
        UUID id = UUID.randomUUID();
        Usuario nuevoUsuario = new Usuario(id, nombre, apellido, email, direccion, password, Rol.CLIENTE);
        uRepo.guardar(nuevoUsuario);
        //TODO: Implementar el método para dar de alta a un usuario
    }

    //CU-19: Baja UsuARIO
    public void BajaUsuario(String id, String email){ 
        Usuario bajarUsuario = uRepo.buscarPorEmail(email);
        if(bajarUsuario != null && bajarUsuario.getId().toString().equals(id)){
            bajarUsuario = new Usuario(bajarUsuario.getId(), bajarUsuario.getNombre(), bajarUsuario.getApellido(), bajarUsuario.getEmail(), bajarUsuario.getDireccion(), bajarUsuario.getPassword(), bajarUsuario.getRol());
            bajarUsuario.desactivar(); 
            uRepo.guardar(bajarUsuario);
        }

        //TODO: Implementar el método para dar de baja a un usuario
    }

    //CU-20: Modificar UsuARIO 


    //CU-17: Listar UsuARIOS
    public void ListarUsuarios(){
        //TODO: Implementar el método para listar todos los usuarios
    }

    public void BuscarUsuario(){
       //TODO: Implementar el método para buscar un usuario por su email o ID     
    }

    public void AltaProducto(){
        //TODO: Implementar el método para dar de alta a un producto
    }

    public void BajaProducto(){
        //TODO: Implementar el método para dar de baja a un producto
    }

    public void ModificarProducto(){
        //TODO: Implementar el método para modificar los datos de un producto
    }

    public void ListarProductos(){
        //TODO: Implementar el método para listar todos los productos
    }

    public void BuscarProducto(){
        //TODO: Implementar el método para buscar un producto por su nombre o ID
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
