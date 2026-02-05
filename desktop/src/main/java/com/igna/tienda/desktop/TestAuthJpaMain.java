package com.igna.tienda.desktop;
import com.igna.tienda.core.domain.enums.Rol;
import com.igna.tienda.core.repositories.UsuarioRepository;
import com.igna.tienda.core.services.AuthService;
import com.igna.tienda.infra.persistence.jpa.JpaUsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestAuthJpaMain {
    
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pa2PU");
        EntityManager em = emf.createEntityManager();

        UsuarioRepository repo = new JpaUsuarioRepository(em);
        AuthService auth = new AuthService(repo);

        auth.registrar("Jose", "Crucillo", "igna@mail.com", "1234", Rol.CLIENTE);
        var u = auth.iniciarSesion("igna@mail.com", "1234");

        System.out.println("Login OK: " + u.getEmail() + " rol=" + u.getRol());

        em.close();
        emf.close();


    }
}
