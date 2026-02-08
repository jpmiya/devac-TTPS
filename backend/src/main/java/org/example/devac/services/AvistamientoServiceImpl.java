package org.example.devac.services;


import org.example.devac.DAOs.AvistamientoDAO;
import org.example.devac.DAOs.MascotaDAO;
import org.example.devac.DAOs.UsuarioDAO;
import org.example.devac.dto.AvistamientoRequest;
import org.example.devac.models.Avistamiento;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvistamientoServiceImpl implements AvistamientoService {
    @Autowired
    AvistamientoDAO<Avistamiento> avistamientoDAO;

    @Autowired
    UsuarioDAO<Usuario> usuarioDAO;

    @Autowired
    MascotaDAO<Mascota> mascotaDAO;

    @Autowired
    GeorefService georefService;

    @Override
    public Avistamiento createAvistamiento(AvistamientoRequest request) {
        // Buscar el usuario por ID
        Usuario usuario = usuarioDAO.get(request.getUsuarioId());
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado con ID: " + request.getUsuarioId());
        }

        // Buscar la mascota por ID
        Mascota mascota = mascotaDAO.get(request.getMascotaId());
        if (mascota == null) {
            throw new RuntimeException("Mascota no encontrada con ID: " + request.getMascotaId());
        }

        // Crear el avistamiento
        Avistamiento avistamiento = new Avistamiento(
            usuario,
            mascota,
            request.getFecha(),
            request.getFoto(),
            request.getCoordenadas(),
            request.getComentario()
        );

        // Resolver barrio y ciudad desde coordenadas usando Georef
        if (request.getCoordenadas() != null && !request.getCoordenadas().isBlank()) {
            GeorefService.UbicacionResult ubicacion = georefService.resolverUbicacion(request.getCoordenadas());
            if (ubicacion != null) {
                avistamiento.setBarrio(ubicacion.getBarrio());
                avistamiento.setCiudad(ubicacion.getCiudad());
            }
        }

        return avistamientoDAO.persist(avistamiento);
    }

    @Override
    public List<Avistamiento> getAvistamientos(){
        return avistamientoDAO.getAll("id");
    }

}
