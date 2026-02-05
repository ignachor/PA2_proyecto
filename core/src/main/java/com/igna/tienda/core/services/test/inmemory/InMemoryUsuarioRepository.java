package com.igna.tienda.core.services.test.inmemory;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.repositories.UsuarioRepository;

import java.util.*;

public class InMemoryUsuarioRepository implements UsuarioRepository {

    private final Map<UUID, Usuario> byId = new HashMap<>();
    private final Map<String, UUID> idByEmail = new HashMap<>();

    private static String normEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        String key = normEmail(email);
        if (key == null) return null;

        UUID id = idByEmail.get(key);
        if (id == null) return null;

        return byId.get(id); // puede ser null si no existe
    }

    @Override
    public Usuario buscarPorId(UUID id) {
        return byId.get(id); // null si no existe
    }

    @Override
    public void guardar(Usuario u) {
        Objects.requireNonNull(u, "usuario obligatorio");
        byId.put(u.getId(), u);

        String key = normEmail(u.getEmail());
        if (key == null) throw new IllegalArgumentException("email obligatorio");
        idByEmail.put(key, u.getId());
    }
}
