package org.example.devac.services;

import org.example.devac.models.Usuario;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface UsuarioService {
    Usuario registrar(Usuario usuario);
    Usuario editar(Long id,Usuario usuario);
    boolean login(String email, String password);
}
