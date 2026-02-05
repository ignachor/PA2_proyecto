package com.igna.tienda.core.repositories;
import com.igna.tienda.core.domain.Usuario;
import java.util.Optional;
import java.util.UUID;
public interface UsuarioRepository {
    Usuario buscarPorEmail(String email);
    Usuario buscarPorId(UUID id);
    void guardar(Usuario usuario);

}
