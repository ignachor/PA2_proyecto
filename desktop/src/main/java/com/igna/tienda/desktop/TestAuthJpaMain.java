package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.enums.Rol;
import com.igna.tienda.infra.services.AuthServiceTx;
import jakarta.persistence.Persistence;
import com.igna.tienda.core.domain.value.Direccion;
public class TestAuthJpaMain {
    
    public static void main(String[] args) {

        var emf = Persistence.createEntityManagerFactory("pa2PU");
        var auth = new AuthServiceTx(emf);
        
        Direccion direccion = new Direccion("Calle Falsa", "123", "Springfield", "Illinois", "62704");   
        auth.registrar("Ayelen", "Gonzales", "12345678", "admin_1@mail.com", direccion, "1234", Rol.ADMIN);
        var u = auth.iniciarSesion("admin@mail.com", "1234");

        System.out.println("Login OK: " + u.getEmail() + " rol=" + u.getRol());

        emf.close();


    }
}
