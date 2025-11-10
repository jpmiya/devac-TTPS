package org.example.devac.services;

import org.example.devac.models.Usuario;
import org.example.devac.repositories.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    UsuarioRepo usuarioRepo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Usuario registrar(Usuario usuario) {
        String hashed = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(hashed);
        return usuarioRepo.save(usuario);
    }

    @Override
    public Usuario editar(Long id,Usuario usuario) {
       return usuarioRepo.save(usuario);
    }

    @Override
    public boolean login(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();
        // Comparar la contraseña ingresada (en texto plano) con el hash guardado
        return passwordEncoder.matches(password, usuario.getPassword());
    }
}
