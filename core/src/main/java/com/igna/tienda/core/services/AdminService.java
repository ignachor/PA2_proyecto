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



    public void AltaProducto(Producto producto){
        if (producto == null) {
            throw new IllegalArgumentException("Producto no proporcionado");
        }
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new IllegalArgumentException("Nombre de producto no proporcionado");
        }
        if (producto.getCategoria() == null || producto.getCategoria().isBlank()) {
            throw new IllegalArgumentException("Categoria de producto no proporcionada");
        }
        if (producto.getDescripcion() == null || producto.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("Descripcion de producto no proporcionada");
        }
        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("Precio de producto invalido");
        }
        if (producto.getCantidad() < 0) {
            throw new IllegalArgumentException("Cantidad de producto invalida");
        }
        if (producto.getCantidadMinimo() < 0) {
            throw new IllegalArgumentException("Cantidad minima invalida");
        }

        String id = producto.getId();
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }

        Producto productoDarDeAlta = new Producto(
            id,
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getCategoria(),
            producto.getPrecio(),
            producto.getCantidad(),
            producto.getCantidadMinimo(),
            producto.getStock()
        );
        if (productoDarDeAlta.getCantidad() > 0) {
            productoDarDeAlta.hayStock();
        } else {
            productoDarDeAlta.noHayStock();
        }
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

