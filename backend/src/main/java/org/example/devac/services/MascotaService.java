package org.example.devac.services;

import org.example.devac.dto.MascotaRequest;
import org.example.devac.models.Mascota;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface MascotaService {
    Mascota registrar(MascotaRequest request);
    Mascota registrar(MascotaRequest request, MultipartFile foto);
    Mascota editar(Mascota mascota);
    void eliminar(Mascota mascota);
    List<Mascota> findAllLost();
}
