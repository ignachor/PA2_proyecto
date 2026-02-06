package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.enums.Rol;
import com.igna.tienda.infra.services.AuthServiceTx;
import jakarta.persistence.Persistence;

public class TestAuthJpaMain {
    
    public static void main(String[] args) {

        var emf = Persistence.createEntityManagerFactory("pa2PU");
        var auth = new AuthServiceTx(emf);
        

        auth.registrar("Raul", "Marquez", "raul@mail.com", "1234", Rol.CLIENTE);
        var u = auth.iniciarSesion("raul@mail.com", "1234");

        System.out.println("Login OK: " + u.getEmail() + " rol=" + u.getRol());

        emf.close();


    }
}
