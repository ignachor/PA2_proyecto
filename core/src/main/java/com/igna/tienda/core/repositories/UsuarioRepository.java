package com.igna.tienda.core.repositories;
import com.igna.tienda.core.domain.Usuario;
import java.util.List;
import java.util.UUID;
public interface UsuarioRepository {
    Usuario buscarPorEmail(String email);
    Usuario buscarPorId(UUID id);
    void guardar(Usuario usuario);
    List<Usuario> listarTodos();
}
