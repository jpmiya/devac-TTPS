package org.example.devac.services;

import jakarta.ws.rs.NotFoundException;
import org.example.devac.DAOs.UsuarioDAO;
import org.example.devac.models.Usuario;
import org.example.devac.utils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserEditerService {

    @Autowired
    private UsuarioDAO<Usuario> usuarioDAO;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario edit(Long id, Usuario cambios) {
        Usuario existente = usuarioDAO.get(id);
        if (existente == null) {
            throw new NotFoundException("Usuario no encontrado con ID: " + id);
        }

        // Obtener los nombres de las propiedades que son null en 'cambios'
        String[] ignorar = PropertyUtils.getNullPropertyNames(cambios);

        // Copiar solo las propiedades no-null de 'cambios' a 'existente'
        BeanUtils.copyProperties(cambios, existente, ignorar);

        // Manejar password por separado (si viene, hashearlo)
        if (cambios.getPassword() != null && !cambios.getPassword().isEmpty()) {
            String hashed = passwordEncoder.encode(cambios.getPassword());
            existente.setPassword(hashed);
        }

        return usuarioDAO.update(existente);
    }
}
