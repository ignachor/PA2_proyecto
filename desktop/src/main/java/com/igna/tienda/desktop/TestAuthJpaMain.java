package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.enums.Rol;
import com.igna.tienda.infra.services.AdminServiceTx;
import com.igna.tienda.infra.services.AuthServiceTx;
import jakarta.persistence.Persistence;
import com.igna.tienda.core.domain.value.Direccion;
import com.igna.tienda.core.domain.Producto;
public class TestAuthJpaMain {
    //Constructor de producto
    //public Producto(String nombre, String descripcion, String categoria, double precio, int cantidad, int cantidadMinimo, boolean stock)
    public static void main(String[] args) {

        var emf = Persistence.createEntityManagerFactory("pa2PU");
        var auth = new AuthServiceTx(emf);
        var producto =  new AdminServiceTx(emf);

        //Probar Creacion de usuario/admin
        //Direccion direccion = new Direccion("Calle Falsa", "123", "Springfield", "Illinois", "62704");   
        //auth.registrar("Ayelen", "Gonzales", "12345678", "admin_1@mail.com", direccion, "1234", Rol.ADMIN);
        
        //Probar Creacion de producto
        //Producto producto_1 = new Producto("Leche", "rica leche", "lacteos", 100.0, 10, 10, true);
        //producto.agregarProducto(producto_1);

        var u = auth.iniciarSesion("admin@mail.com", "1234");

        System.out.println("Login OK: " + u.getEmail() + " rol=" + u.getRol());

        emf.close();


    }
}
