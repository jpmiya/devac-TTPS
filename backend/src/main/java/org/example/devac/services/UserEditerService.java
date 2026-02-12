package org.example.devac.services;

import jakarta.ws.rs.NotFoundException;
import org.example.devac.DAOs.UsuarioDAO;
import org.example.devac.models.Usuario;
import org.example.devac.utils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserEditerService {

    @Autowired
    private UsuarioDAO<Usuario> usuarioDAO;

    @Autowired
    private GeorefService georefService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario edit(Long id, Usuario cambios) {
        Usuario existente = usuarioDAO.get(id);
        if (existente == null) {
            throw new NotFoundException("Usuario no encontrado con ID: " + id);
        }

        // Obtener los nombres de las propiedades que son null en 'cambios'
        // y forzar que coordenadas no se persista (solo se usa para Georef).
        String[] nullProps = PropertyUtils.getNullPropertyNames(cambios);
        String[] ignorar = Arrays.copyOf(nullProps, nullProps.length + 1);
        ignorar[nullProps.length] = "coordenadas";

        // Copiar solo las propiedades no-null de 'cambios' a 'existente'
        BeanUtils.copyProperties(cambios, existente, ignorar);

        // Si vienen coordenadas, resolver barrio y ciudad con Georef
        if (cambios.getCoordenadas() != null && !cambios.getCoordenadas().isBlank()) {
            GeorefService.UbicacionResult ubicacion = georefService.resolverUbicacion(cambios.getCoordenadas());
            if (ubicacion != null) {
                existente.setBarrio(ubicacion.getBarrio());
                existente.setCiudad(ubicacion.getCiudad());
            }
        }

        // Manejar password por separado (si viene, hashearlo)
        if (cambios.getPassword() != null && !cambios.getPassword().isEmpty()) {
            String hashed = passwordEncoder.encode(cambios.getPassword());
            existente.setPassword(hashed);
        }

        return usuarioDAO.update(existente);
    }
}
