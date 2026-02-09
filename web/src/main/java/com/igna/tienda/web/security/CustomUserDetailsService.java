package com.igna.tienda.web.security;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.persistence.jpa.JpaUsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Implementación de UserDetailsService para Spring Security.
 * 
 * Carga usuarios desde la BD usando JpaUsuarioRepository.
 * Convierte Usuario → UserDetails para Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EntityManagerFactory emf;

    public CustomUserDetailsService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            JpaUsuarioRepository usuarioRepo = new JpaUsuarioRepository(em);
            Usuario usuario = usuarioRepo.buscarPorEmail(email.trim().toLowerCase());

            if (usuario == null) {
                throw new UsernameNotFoundException("Usuario no encontrado: " + email);
            }

            // Convertir rol del dominio a autoridad de Spring Security
            List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
            );

            // Retornar UserDetails con email, password, habilitado y autoridades
            return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .disabled(!usuario.esActivo())
                .authorities(authorities)
                .build();
        } finally {
            em.close();
        }
    }
}
