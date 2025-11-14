package org.example.devac.services;

import org.example.devac.DAOs.MascotaDAO;
import org.example.devac.models.EstadoMascota;
import org.example.devac.models.Mascota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    MascotaDAO<Mascota> mascotaDAO;

    @Override
    public Mascota registrar(Mascota mascota) {
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
