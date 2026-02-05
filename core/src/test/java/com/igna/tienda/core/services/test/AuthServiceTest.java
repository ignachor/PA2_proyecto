package com.igna.tienda.core.services.test;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.domain.enums.Rol;
import com.igna.tienda.core.services.test.inmemory.InMemoryUsuarioRepository;
import com.igna.tienda.core.repositories.UsuarioRepository;
import com.igna.tienda.core.services.AuthService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthServiceTest {

    @Test
    void registro_y_login_ok() {
        UsuarioRepository repo = new InMemoryUsuarioRepository();
        AuthService auth = new AuthService(repo);

        auth.registrar("Jose","Crucillo", "igna@mail.com", "1234", Rol.CLIENTE);

        Usuario u = auth.iniciarSesion("igna@mail.com", "1234");

        assertEquals("igna@mail.com", u.getEmail());
    }
}
