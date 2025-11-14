package org.example.devac.services;

import org.example.devac.DAOs.MascotaDAO;
import org.example.devac.DAOs.UsuarioDAO;
import org.example.devac.dto.MascotaRequest;
import org.example.devac.models.EstadoMascota;
import org.example.devac.models.Mascota;
import org.example.devac.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    MascotaDAO<Mascota> mascotaDAO;

    @Autowired
    UsuarioDAO<Usuario> usuarioDAO;

    @Override
    public Mascota registrar(MascotaRequest request) {
        // Buscar el dueño por ID
        Usuario dueno = usuarioDAO.get(request.getDuenoId());
        if (dueno == null) {
            throw new RuntimeException("Usuario no encontrado con ID: " + request.getDuenoId());
        }

        // Crear la mascota
        Mascota mascota = new Mascota();
        mascota.setDueno(dueno);
        mascota.setNombre(request.getNombre());
        mascota.setTamaño(request.getTamaño());
        mascota.setColor(request.getColor());
        mascota.setFechaDePerdida(request.getFechaDePerdida());
        mascota.setFoto(request.getFoto());
        mascota.setCoordenadas(request.getCoordenadas());
        mascota.setDescripcion(request.getDescripcion());
        mascota.setTipo(request.getTipo());
        mascota.setRaza(request.getRaza());
        
        // Establecer estado (usar el del request o por defecto PERDIDO_PROPIO)
        if (request.getEstado() != null) {
            mascota.setEstado(request.getEstado());
        } else {
            mascota.setEstado(EstadoMascota.PERDIDO_PROPIO);
        }

        return mascotaDAO.persist(mascota);
    }

    @Override
    public Mascota editar(Mascota mascota) {
        return mascotaDAO.update(mascota);
    }

    public void eliminar(Mascota mascota) {
        mascotaDAO.delete(mascota);
    }

    public List<Mascota> findAllLost() {
        // Obtener todas las mascotas y filtrar por estado perdido
        List<Mascota> todas = mascotaDAO.getAll("id");
        return todas.stream()
            .filter(m -> m.getEstado() == EstadoMascota.PERDIDO_AJENO || 
                        m.getEstado() == EstadoMascota.PERDIDO_PROPIO)
            .collect(Collectors.toList());
    }

}
