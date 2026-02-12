package org.example.devac.services;

import org.example.devac.dto.MascotaRequest;
import org.example.devac.models.Mascota;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;

import java.util.List;
import java.util.Map;

public interface MascotaService {
    Mascota registrar(MascotaRequest request);
    Mascota registrar(MascotaRequest request, MultipartFile foto);
    void eliminar(Mascota mascota);
    List<Mascota> findAllLost();
    Mascota buscarPorId(Long id);
    Optional<Mascota> findById(Long id);

    Mascota editar(Long mascotaId, MascotaRequest request, MultipartFile foto, Long usuarioId);
    Mascota editarConFoto(Long mascotaId, Mascota mascotaActualizada, MultipartFile foto);
}
